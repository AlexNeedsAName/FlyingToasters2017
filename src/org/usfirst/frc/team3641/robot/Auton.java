package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.Timer;

@SuppressWarnings("unused") //I might not be using those functions now, but they're there to be building blocks for future routines.
public class Auton
{
	private static Auton instance;
	private static states autonState;
	private static modes autonMode;
	private static boolean onRedAlliance;
	
	private static final int LOW = 1, MID = 2, HIGH = 3;

	static boolean negativeErrorWhenDone;
	
	private static boolean lineFound = false;
	private static boolean endOfLine = false;
	
	private static boolean alreadyRunning;
	private static Timer timeoutTimer;
	private static int VERBOSE;

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
			if(i >= values.length || i<0)
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
	
	public static void setup(modes mode, boolean redAlliance, int verbosity)
	{
		autonState = states.START;
		alreadyRunning = false;
		autonMode = mode;
		onRedAlliance = redAlliance;
		VERBOSE = verbosity;
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
	
	@SuppressWarnings("incomplete-switch") //I don't care about the other values, I know they won't be used :P
	private static void crossBaseline()
	{
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_FORWARDS);
			break;

		case DRIVE_FORWARDS:
			boolean reachedLine = driveBy(3);
			if(reachedLine) increment(states.DONE);
			break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	private static void hopperAuton()
	{
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_TO_HOPPER_LINE);
			break;
			
		case DRIVE_TO_HOPPER_LINE:
			boolean reachedLine = driveBy(3);
			if(reachedLine) increment(states.TURN_TO_HOPPER);
			break;

		case TURN_TO_HOPPER:
			double angle = (onRedAlliance) ? 90 : -90; //If on red alliance, turn right. If on blue, turn left.
			boolean doneTurning = turnBy(angle, 1);
			if(doneTurning) increment(states.DRIVE_TO_HOPPER);
			break;
			
		case DRIVE_TO_HOPPER:
			boolean reachedHopper = driveBy(3, .5);
			boolean hitTheWall = (Sensors.isStill() && timeoutTimer.get() > .5); //We don't want it to check if it's still too soon, otherwise it just doesn't go 
			if(reachedHopper || hitTheWall) increment(states.SCORE_RANKING_POINT);
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
	public static boolean driveBy(double distance, double timeout)
	{
		if(!alreadyRunning)
		{
			initTimeout(timeout);
			Sensors.resetDriveDistance();
			negativeErrorWhenDone = (distance < Sensors.getDriveDistance());
			if(VERBOSE >= LOW) System.out.println("Starting to drive by " + distance + "m in state " + autonState);
			alreadyRunning = true;
		}
		if(VERBOSE >= HIGH) System.out.println(Sensors.getDriveDistance() + "m out of " + distance + "m");
		double error = DriveBase.driveTo(distance);
		boolean crossedLine = (negativeErrorWhenDone) ? (error < 0) : (error > 0);
		boolean  withinThreshold = (Math.abs(error) <= Constants.AUTON_DRIVE_DISTANCE_ACCEPTABLE_ERROR);
		
		return (crossedLine || withinThreshold || timeoutUp(timeout));
	}
	
	public static boolean driveBy(double distance)
	{
		return driveBy(distance, 0);
	}

	private static boolean turnBy(double angle, double timeout)
	{
		if(!alreadyRunning)
		{
			if(VERBOSE >= LOW) System.out.println("Starting to turn by " + angle + "° in state " + autonState);
			initTimeout(timeout);
			Sensors.resetGyro();
			alreadyRunning = true;
		}
		if(VERBOSE >= HIGH) System.out.println(Sensors.getAngle() + "° out of " + angle + "°");
		boolean done = DriveBase.turnTo(angle, 1);
		
		return (done || timeoutUp(timeout));
	}
	
	private static boolean turnBy(double angle)
	{
		return turnBy(angle, 0);
	}

	private static void initTimeout(double Timeout)
	{
		if(VERBOSE >= LOW && Timeout != 0) System.out.println("Starting a " + Timeout + "s Timer");
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
		if(done && VERBOSE >= LOW) System.out.println("\nScrew it, it's close enough. We're out of time");
		return done;
	}
	
	private static void increment(states state)
	{
		if(VERBOSE >= LOW) System.out.println("\nIncrementing from state " + autonState.toString() + " to state " + state.toString());
		autonState = state;
		DriveBase.driveArcade(0, 0); //Stop Driving!
		initTimeout(0);
		alreadyRunning = false;
	}
}
