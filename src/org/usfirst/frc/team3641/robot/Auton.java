package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.Timer;

public class Auton
{
	private static Auton instance;
	private static int autonState = Constants.START;
	private static int autonMode;
	private static boolean onRedAlliance;

	private static boolean lineFound = false;
	private static boolean endOfLine = false;

	private static boolean alreadyRunning = false;
	private static double finalDist;
	private static double finalAngle;
	private static Timer timeoutTimer;
	private static double timeout;

	public static UDP udp;

	public static Auton getInstance()
	{
		if(instance == null) instance = new Auton();
		return instance;
	}

	private Auton()
	{
		timeoutTimer = new Timer();
	}
	
	public static void setup(int mode, boolean redAlliance)
	{
		autonMode = mode;
		onRedAlliance = redAlliance;
	}

	public static void run()
	{
		switch(autonMode)
		{
		case Constants.DO_NOTHING:				
			break;

		case Constants.CROSS_BASELINE:
			crossBaseline();
			break;

		case Constants.HOPPER_AUTON:
			hopperAuton();
			break;

		case Constants.LINE_ALIGN:
			lineAlign();
			break;

		case Constants.LINE_FOLLOW:
			lineFollow();
			break;
		}
	}

	private static void crossBaseline()
	{
		boolean done;
		switch(autonState)
		{
		case Constants.START:
			increment(Constants.DRIVE_FORWARDS);
			break;

		case Constants.DRIVE_FORWARDS:
			done = driveForwards(3, .5);
			if(done) increment(Constants.DONE);
			break;
		}
	}

	private static void hopperAuton()
	{
		boolean done;
		switch(autonState)
		{
		case Constants.START:
			increment(Constants.DRIVE_TO_HOPPER_LINE);
			break;
			
		case Constants.DRIVE_TO_HOPPER_LINE:
			done = driveForwards(3, .5);
			if(done) increment(Constants.TURN_TO_HOPPER);
			break;

		case Constants.TURN_TO_HOPPER:
			double angle = (onRedAlliance) ? -90 : 90; //If on red alliance, turn right. If on blue, turn left.
			done = turnBy(angle);
			if(done) increment(Constants.DRIVE_TO_HOPPER);
			break;
			
		case Constants.DRIVE_TO_HOPPER:
			done = driveForwards(3, .5);
			if(done || Sensors.isStill()) increment(Constants.SCORE_RANKING_POINT);
			break;

		case Constants.SCORE_RANKING_POINT:
			Tracking.target(Constants.FUEL_MODE);
			break;
		}
	}
	
	private static void lineAlign()
	{
		String receivedData;
		if(udp == null) udp = new UDP("beaglebone.local", 3641);

		//Request info about line position
		if (alreadyRunning == false) 
		{
			udp.sendData("1");
			alreadyRunning = true;
		}

		receivedData = udp.getData();

		if (receivedData != null) 
		{
			//This code allows for the incoming data to split up into parts by spaces
			String[] parts = receivedData.split(" ");
			String part1 = parts[0];
			double part1_double = Double.parseDouble(part1);
			System.out.println("RECEIVED: " + part1);

			DriveBase.turnDegrees(part1_double, 2);
		}

	}

	private static void lineFollow()
	{
		String receivedData;
		if(udp == null) udp = new UDP("beaglebone.local", 3641);
		Sensors.poll();

		//Find the line first!
		if (lineFound == false) 
		{
			udp.sendData("2");
			receivedData = udp.getData();
			if (receivedData != null) 
			{
				//This code allows for the incoming data to split up into parts by spaces
				String[] parts = receivedData.split(" ");
				String part1 = parts[0];
				double part1_double = Double.parseDouble(part1);
				System.out.println("RECEIVED: " + part1);

				if (part1_double == 999) 
				{
					DriveBase.driveArcade(-0.6, 0);
				}
				else
				{
					lineFound = true;
					DriveBase.driveArcade(-0.6, 0);
					Timer.delay(1); //I just didn't have an encoder, it will be removed soon
					DriveBase.driveArcade(0, 0);
					DriveBase.turnDegrees(55, 2);
				}
			}
		}

		//Follow the line until you loose it
		if (lineFound == true && endOfLine == false) 
		{
			udp.sendData("2");
			receivedData = udp.getData();
			if (receivedData != null) 
			{
				//This code allows for the incoming data to split up into parts by spaces
				String[] parts = receivedData.split(" ");
				String part1 = parts[0];
				double part1_double = Double.parseDouble(part1);
				System.out.println("RECEIVED: " + part1);

				if (part1_double != 999)
				{
					if (part1_double > -30 && part1_double < 30)
					{
						DriveBase.driveArcade(-0.4, 0);
					}
					if (part1_double <= -30)
					{
						DriveBase.driveArcade(-0.4, -0.3);
					}
					if (part1_double >= 30)
					{
						DriveBase.driveArcade(-0.4, 0.3);
					}
				}
				else
				{
					DriveBase.driveArcade(0, 0);
				}

				double range = Sensors.getDistance();
				System.out.println(range);

				if (range < 0.2) 
				{
					DriveBase.driveArcade(0, 0);
					endOfLine = true;
				}
			}
		}
	}
	private static boolean driveForwards(double distance, double speed, double timeout)
	{
		if(!alreadyRunning)
		{
			initTimeout(timeout);
			timeoutTimer.start();
			finalDist = Sensors.getDriveDistance() + distance;
			alreadyRunning = true;
		}
		DriveBase.driveArcade(speed, 0);
		boolean done = (Sensors.getDriveDistance() <= finalDist);
		return (done || timeoutUp());
	}

	private static boolean driveForwards(double distance, double speed)
	{
		return driveForwards(distance, speed, 0);
	}

	private static boolean turnBy(double angle, double timeout)
	{
		if(!alreadyRunning)
		{
			initTimeout(timeout);
			finalAngle = Sensors.getAngle() + angle;
			alreadyRunning = true;
		}
		boolean done = DriveBase.turnTo(finalAngle, 1);
		
		return (done || timeoutUp());
	}
	
	private static boolean turnBy(double angle)
	{
		return turnBy(angle, 0);
	}

	private static void initTimeout(double Timeout)
	{
		timeoutTimer.reset();
		timeoutTimer.start();
		timeout = Timeout;
	}
	
	private static boolean timeoutUp()
	{
		return (timeout > 0 && timeoutTimer.get() >= timeout);
	}
	
	private static void increment(int state)
	{
		autonState = state;
		alreadyRunning = false;
	}
}
