package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.Timer;

public class Auton
{
	private static Auton instance;
	private static states autonState;
	private static modes autonMode;
	private static boolean onRedAlliance;

	private static boolean lineFound = false;
	private static boolean endOfLine = false;

	private static boolean alreadyRunning;
	private static Timer timeoutTimer;
	private static boolean VERBOSE;

	public static UDP udp;

	private enum states
	{
		START,
		DONE,
		DRIVE_FORWARDS,
		DRIVE_TO_HOPPER_LINE,
		TURN_TO_HOPPER,
		DRIVE_TO_HOPPER,
		SCORE_RANKING_POINT;
	}
	
	public enum modes
	{
		DO_NOTHING,
		CROSS_LINE,
		HOPPER_AUTON,
		LINE_ALIGN,
		LINE_FOLLOW;
		
		private static final modes[] values = modes.values(); //We cache the value array for preformance
		public static modes fromInt(int i)
		{
			if(i >= values.length)
			{
				System.err.println("WARNING: Auton " + i + " out of range. Defaulting to " + values[0].toString());
				i = 0;
			}
			return values[i];
		}
	}
	
	public static Auton getInstance()
	{
		if(instance == null) instance = new Auton();
		return instance;
	}

	private Auton()
	{
		timeoutTimer = new Timer();
	}
	
	public static void setup(modes mode, boolean redAlliance, boolean verboseMode)
	{
		autonState = states.START;
		alreadyRunning = false;
		autonMode = mode;
		onRedAlliance = redAlliance;
		VERBOSE = verboseMode;
	}
	
	public static void test()
	{
		Sensors.resetDriveDistance(Sensors.getDriveDistance() + Teleop.driver.getAxis(PS4.Axis.LEFT_Y) / 10);
		run();
	}

	public static void run()
	{
		switch(autonMode)
		{
		case DO_NOTHING:				
			break;

		case CROSS_LINE:
			crossBaseline();
			break;

		case HOPPER_AUTON:
			hopperAuton();
			break;

		case LINE_ALIGN:
			lineAlign();
			break;

		case LINE_FOLLOW:
			lineFollow();
			break;
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private static void crossBaseline()
	{
		boolean done;
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_FORWARDS);
			break;

		case DRIVE_FORWARDS:
			done = driveBy(3, .5);
			if(done) increment(states.DONE);
			break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	private static void hopperAuton()
	{
		boolean done;
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_TO_HOPPER_LINE);
			break;
			
		case DRIVE_TO_HOPPER_LINE:
			done = driveBy(3, .5);
			if(done) increment(states.TURN_TO_HOPPER);
			break;

		case TURN_TO_HOPPER:
			double angle = (onRedAlliance) ? 90 : -90; //If on red alliance, turn right. If on blue, turn left.
			done = turnBy(angle, 5);
			if(done) increment(states.DRIVE_TO_HOPPER);
			break;
			
		case DRIVE_TO_HOPPER:
			done = driveBy(3, .5);
			if(done || (Sensors.isStill() && timeoutTimer.get() > 1)) increment(states.SCORE_RANKING_POINT);
			break;

		case SCORE_RANKING_POINT:
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
	private static boolean driveBy(double distance, double speed, double timeout)
	{
		if(!alreadyRunning)
		{
			initTimeout(timeout);
			if(VERBOSE) System.out.println("Starting to drive by " + distance + "m in state " + autonState);
			alreadyRunning = true;
		}
		if(VERBOSE) System.out.print(Sensors.getDriveDistance() + "m out of " + distance + "m\r");
		DriveBase.driveArcade(speed, 0);
		boolean done = (Sensors.getDriveDistance() <= distance);
		return (done || timeoutUp(timeout));
	}

	private static boolean driveBy(double distance, double speed)
	{
		return driveBy(distance, speed, 0);
	}

	private static boolean turnBy(double angle, double timeout)
	{
		if(!alreadyRunning)
		{
			if(VERBOSE) System.out.println("Starting to turn by " + angle + "° in state " + autonState);
			initTimeout(timeout);
			Sensors.resetGyro();
			alreadyRunning = true;
		}
		if(VERBOSE) System.out.print(Sensors.getAngle() + "° out of " + angle + "°\r");
		boolean done = DriveBase.turnTo(angle, 1);
		
		return (done || timeoutUp(timeout));
	}
	
	@SuppressWarnings("unused")
	private static boolean turnBy(double angle)
	{
		return turnBy(angle, 0);
	}

	private static void initTimeout(double Timeout)
	{
		if(VERBOSE) System.out.println("Starting a " + Timeout + "s Timer");
		timeoutTimer.reset();
		timeoutTimer.start();
	}
	
	private static boolean timeoutUp(double timeout)
	{
		if(timeout == 0)
		{
			return false;
		}
		double time = timeoutTimer.get();
//		if(VERBOSE) System.out.println(time + "s out of " + timeout + "s");
		boolean done = (time >= timeout);
		if(done && VERBOSE) System.out.println("\nScrew it, it's close enough. We're out of time\r");
		return done;
	}
	
	private static void increment(states state)
	{
		if(VERBOSE) System.out.println("\nIncrementing from state " + autonState.toString() + " to state " + state.toString());
		autonState = state;
		alreadyRunning = false;
	}
}
