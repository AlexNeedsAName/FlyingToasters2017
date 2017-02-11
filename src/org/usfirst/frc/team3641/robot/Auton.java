package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;

public class Auton
{
	private static Auton instance;
	private static int autonState;
	private static int autonMode;

	private static boolean alreadyDriving = false;
	private static boolean lineFound = false;
	private static boolean endOfLine = false;


	private static double initalDist;
	private static double finalDist;
	private static double initalAngle;
	private static double finalAngle;

	private static boolean onRedAlliance;

	private static boolean runOnce;

	public static UDP udp;

	public static Auton getInstance(int mode, boolean redAlliance)
	{
		if(instance == null) instance = new Auton(mode, redAlliance);
		return instance;
	}

	private Auton(int mode, boolean redAlliance)
	{
		runOnce = false;
		autonState = Constants.START;
		onRedAlliance = redAlliance;
		autonMode = mode;

	}

	public static void run()
	{
		String receivedData;
		Boolean continueMouseControl = true;
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

			if(udp == null) udp = new UDP("beaglebone.local", 3641);

			//Request info about line position
			if (runOnce == false) 
			{
				udp.sendData("1");
				runOnce = true;
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

			break;


		case Constants.DEFAULT_AUTO:
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

			break;

		}
	}

	private static void crossBaseline()
	{
		if(autonState == Constants.START)
		{
			increment(Constants.DRIVE_FORWARDS);
		}

		if(autonState == Constants.DRIVE_FORWARDS)
		{
			boolean done = driveForwards(3, .5);
			if(done) increment(Constants.DONE);
		}
	}

	private static void hopperAuton()
	{
		if(autonState == Constants.START)
		{
			increment(Constants.DRIVE_TO_HOPPER_LINE);
		}

		if(autonState == Constants.DRIVE_TO_HOPPER_LINE);
		{
			boolean done = driveForwards(3, .5);
			if(done) increment(Constants.TURN_TO_HOPPER);
		}

		if(autonState == Constants.TURN_TO_HOPPER)
		{
			double angle = (onRedAlliance) ? -90 : 90; //If on red alliance, turn right. If on blue, turn left.
			boolean done = turnBy(angle);
			if(done) increment(Constants.DRIVE_TO_HOPPER);
		}

		if(autonState == Constants.DRIVE_TO_HOPPER)

			if(autonState == Constants.SCORE_RANKING_POINT)
			{
				int trackingState = Tracking.target(Constants.FUEL_MODE);
				if(trackingState == Constants.TRACKED_FUEL)
				{
					double targetRPM = 1750;//TODO: Add distance calc based on kinematic equation
					double error = Shooter.setRPM(targetRPM);
					if(error < 50) Shooter.fire();
				}
			}
	}

	private static boolean driveForwards(double distance, double speed)
	{
		if(!alreadyDriving)
		{
			initalDist = Sensors.getDriveDistance();
			finalDist = initalDist + distance;
			alreadyDriving = true;
		}

		if(Sensors.getDriveDistance() <= finalDist)
		{
			DriveBase.driveArcade(speed, 0);
			return false;
		}
		else return true;

	}

	private static boolean turnBy(double angle)
	{
		if(!alreadyDriving)
		{
			initalAngle = Sensors.getAngle();
			finalAngle = initalAngle + angle;
			alreadyDriving = true;
		}
		boolean done = DriveBase.turnTo(finalAngle, 1);
		return done;
	}

	private static void increment(int state)
	{
		autonState = state;
		alreadyDriving = false;
	}
}
