package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.Timer;

@SuppressWarnings("unused") //I might not be using those functions now, but they're there to be building blocks for future routines.
public class Auton
{
	private static Auton instance;
	private static PropertyReader config;
	private static states autonState;
	private static modes autonMode;
	private static boolean onRedAlliance;
	
	private static boolean[] doneTurning;
	static boolean alreadyInPosition;
	private static int index;
	
	private static boolean negativeErrorWhenDone;
	private static double initalDistance;
	
	private static boolean lineFound = false;
	private static boolean endOfLine = false;
	
	private static boolean alreadyRunning;
	private static Timer timeoutTimer, autonTimer;
	
	//Distances as measured from back wall
	private static double
			distanceToBaseline = 2.62,
			distanceToHopperLine = 2.29,
			distanceToHopperFromTurn = 1,
			distanceToGearTurn = 2,
			distanceToGearFromTurn = 1,
			gearTurnAngle = -75,
			hopperTurnAngle = 90,
			gearTurnBackAngle = 0,
			gearTurnBackToHopper = 1,
			gearTurnBackDistance = 1;
	
	private static boolean usingHorn = true;

	public static UDP udp;

	private enum states
	{
		START,
		DONE,
		DRIVE_FORWARDS,
		DRIVE_TO_HOPPER_LINE,
		TURN_TO_HOPPER,
		DRIVE_TO_HOPPER,
		SCORE_RANKING_POINT,
		DRIVE_TO_GEAR_TURN,
		TURN_TO_GEAR,
		PLACE_GEAR,
		BACK_AWAY_FROM_GEAR,
		TURN_FROM_GEAR_TO_NORMAL,
		DRIVE_FROM_GEAR_TURN_TO_HOPPER_LINE,
		CALM_DOWN;
	}
	
	public enum modes
	{
		DO_NOTHING,
		CROSS_LINE,
		HOPPER_AUTON,
		GEAR_AUTON,
		COMBO_AUTON,
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
		autonTimer = new Timer();
		config = new PropertyReader("Auton");
	}
	
	public static void setup(modes mode, boolean redAlliance)
	{
		if(Constants.VERBOSE >= Constants.LOW) System.out.println("Starting Auton:\n");
		readConfig();
		autonState = states.START;
		alreadyRunning = false;
		autonMode = mode;
		onRedAlliance = redAlliance;
		Tracking.resetState();
		initTimeout(0);
		alreadyInPosition = false;
		autonTimer.reset();
		autonTimer.start();
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
			
		case GEAR_AUTON:
			gearAuton();
			break;
			
		case COMBO_AUTON:
			comboAuton();
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
			boolean reachedLine = driveBy(distanceToBaseline);
			if(reachedLine) increment(states.DONE);
			break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	private static void hopperAuton()
	{
		int trackingState;
		double angle;
		boolean doneDriving, doneTurning, hitTheWall;
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_TO_HOPPER_LINE);
			break;
			
		case DRIVE_TO_HOPPER_LINE:
			doneDriving = driveBy(distanceToHopperLine, 2);
			if(doneDriving) increment(states.TURN_TO_HOPPER);
			break;

		case TURN_TO_HOPPER:
			angle = (onRedAlliance) ? hopperTurnAngle : -hopperTurnAngle; //If on red alliance, turn right. If on blue, turn left.
			doneTurning = turnBy(angle, 1.3);
			if(doneTurning) increment(states.DRIVE_TO_HOPPER);
			break;
			
		case DRIVE_TO_HOPPER:
			doneDriving = driveBy(distanceToHopperFromTurn, 1);
			hitTheWall = didWeHitSomething(.5);
			if(hitTheWall && Constants.VERBOSE >= Constants.LOW) System.out.println("Ouch!");
			if(doneDriving || hitTheWall) increment(states.SCORE_RANKING_POINT);
			break;

		case SCORE_RANKING_POINT:
			trackingState = Tracking.target(Constants.FUEL_MODE);
			if(trackingState == Constants.TRACKED_FUEL && !alreadyInPosition)
			{
				if(Constants.VERBOSE >= Constants.LOW && !alreadyInPosition)
				{
					double time = autonTimer.get();
					System.out.println("In position. It took " + String.format("%.2f", time) + "s, leaving us " + String.format("%.2f", 15-time) + "s left to shoot.");
				}
				alreadyInPosition = true;
			}
			break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	private static void gearAuton()
	{
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_TO_GEAR_TURN);
			break;
			
		case DRIVE_TO_GEAR_TURN:
			boolean reachedLine = driveBy(distanceToGearTurn);
			if(reachedLine) increment(states.TURN_TO_GEAR);
			break;

		case TURN_TO_GEAR:
			double angle = (onRedAlliance) ? gearTurnAngle : -gearTurnAngle; //If on red alliance, turn right. If on blue, turn left.
			boolean doneTurning = turnBy(angle, 1);
			if(doneTurning) increment(states.PLACE_GEAR);
			break;
			
		case PLACE_GEAR:
			boolean reachedHopper = driveBy(distanceToGearFromTurn, .5);
			boolean hitTheWall = didWeHitSomething(.1);
			if(reachedHopper || hitTheWall) increment(states.DONE);
			break;
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private static void comboAuton()
	{
		double angle;
		boolean doneTurning, doneDriving, hitTheWall, doneWaiting;
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_TO_GEAR_TURN);
			break;
			
		case DRIVE_TO_GEAR_TURN:
			doneDriving = driveBy(distanceToGearTurn);
			if(doneDriving) increment(states.TURN_TO_GEAR);
			break;

		case TURN_TO_GEAR:
			angle = (onRedAlliance) ? gearTurnAngle : -gearTurnAngle; //If on red alliance, turn right. If on blue, turn left.
			doneTurning = turnBy(angle);
			if(doneTurning) increment(states.PLACE_GEAR);
			break;
			
		case PLACE_GEAR:
			doneDriving = driveBy(distanceToGearFromTurn);
			if(doneDriving) increment(states.BACK_AWAY_FROM_GEAR);
			break;
						
		case BACK_AWAY_FROM_GEAR:
			doneDriving = driveBy(gearTurnBackDistance);
			if(doneDriving) increment(states.TURN_FROM_GEAR_TO_NORMAL);
			break;
			
		case TURN_FROM_GEAR_TO_NORMAL:
			angle = (onRedAlliance) ? gearTurnBackAngle : -gearTurnBackAngle; //If on red alliance, turn left. If on blue, turn right.
			doneTurning = turnBy(angle);
			if(doneTurning) increment(states.DRIVE_TO_HOPPER);
			break;
			
		case DRIVE_TO_HOPPER:
			doneDriving = driveBy(gearTurnBackToHopper);
			hitTheWall = didWeHitSomething(.1);
			if(doneDriving || hitTheWall) increment(states.SCORE_RANKING_POINT);
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
			initalDistance = Sensors.getDriveDistance();
			negativeErrorWhenDone = (distance < Sensors.getDriveDistance());
			if(usingHorn) Horn.setHorn(true);
			if(Constants.VERBOSE >= Constants.LOW) System.out.println("Starting to drive by " + distance + "m in state " + autonState);
			alreadyRunning = true;
		}
		String currentDistance = String.format("%.2f", Sensors.getDriveDistance()-initalDistance);
		if(Constants.VERBOSE >= Constants.HIGH) System.out.println( currentDistance + "m out of " + distance + "m");
		double error = DriveBase.driveTo(initalDistance  + distance);
		//boolean crossedLine = (negativeErrorWhenDone) ? (error > 0) : (error < 0);
		boolean  withinThreshold = (Math.abs(error) <= Constants.AUTON_DRIVE_DISTANCE_ACCEPTABLE_ERROR);
		
		return (withinThreshold || timeoutUp(timeout));
	}
	
	public static boolean driveBy(double distance)
	{
		return driveBy(distance, 0);
	}

	private static boolean turnBy(double angle, double timeout)
	{
		if(!alreadyRunning)
		{
			if(Constants.VERBOSE >= Constants.LOW) System.out.println("Starting to turn by " + angle + "° in state " + autonState);
			initTimeout(timeout);
			Sensors.resetGyro();
			alreadyRunning = true;
			doneTurning = new boolean[Constants.NUMBER_OF_TURNING_CHECKS];
		}
		if(Constants.VERBOSE >= Constants.HIGH) System.out.println(Sensors.getAngle() + "° out of " + angle + "°");
		boolean done = DriveBase.turnTo(angle, 1);
		doneTurning[index] = done;
		index++;
		if(index >= doneTurning.length) index = 0;
		
		return (allAreTrue(doneTurning) || timeoutUp(timeout));
	}
	
	private static boolean turnBy(double angle)
	{
		return turnBy(angle, 0);
	}
	
	private static boolean waitFor(double timeout)
	{
		if(!alreadyRunning)
		{
			initTimeout(timeout);
			alreadyRunning = true;
		}
		return timeoutUp(timeout);
	}

	private static void initTimeout(double Timeout)
	{
		if(Constants.VERBOSE >= Constants.LOW && Timeout != 0) System.out.println("Starting a " + Timeout + "s Timer");
		timeoutTimer.reset();
		timeoutTimer.start();
	}
	
	private static boolean timeoutUp(double timeout)
	{
		if(Constants.disableTimeouts) return false;
		if(timeout == 0)
		{
			return false;
		}
		double time = timeoutTimer.get();
//		if(Constants.VERBOSE) System.out.println(time + "s out of " + timeout + "s");
		boolean done = (time >= timeout);
		if(done && Constants.VERBOSE >= Constants.LOW) System.out.println("\nScrew it, it's close enough. We're out of time");
		return done;
	}
	
	private static boolean didWeHitSomething(double minTime)
	{
		return (Sensors.isStill() && timeoutTimer.get() > minTime); //We want to know if we are still, but it doesn't help us if it returns true immediately before we started driving.
	}
	
	private static void increment(states state)
	{
		if(Constants.VERBOSE >= Constants.MID) System.out.println("Took " + timeoutTimer.get() + "s to complete " + autonState.toString());
		if(Constants.VERBOSE >= Constants.LOW) System.out.println("\nIncrementing from state " + autonState.toString() + " to state " + state.toString());
		autonState = state;
		DriveBase.driveArcade(0, 0); //Stop Driving!
		initTimeout(0);
		alreadyRunning = false;
		if(autonState == states.SCORE_RANKING_POINT)
		{
			double time = autonTimer.get();
			System.out.println("In position. It took " + String.format("%.2f", time) + "s, leaving us " + String.format("%.2f", 15-time) + "s left to shoot.");
		}
		Horn.setHorn(false);
	}
	
	private static boolean allAreTrue(boolean[] array)
	{
		for(boolean b : array) if(b == false) return false;
		return true;
	}
	
	private static void readConfig()
	{
		System.out.println("GTBD: " + gearTurnBackDistance);
		System.out.println("GTBA: " + gearTurnBackAngle);
		config.reloadFile();
		distanceToBaseline = config.readDouble("distanceToBaseline", distanceToBaseline);
		distanceToHopperLine = config.readDouble("distanceToHopperLine", distanceToHopperLine);
		distanceToHopperFromTurn = config.readDouble("distanceToHopperFromTurn", distanceToHopperFromTurn);
		distanceToGearTurn = config.readDouble("distanceToGearTurn", distanceToGearTurn);
		distanceToGearFromTurn = config.readDouble("distanceToGearFromTurn", distanceToGearFromTurn);
		hopperTurnAngle = config.readDouble("hopperTurnAngle", hopperTurnAngle);
		gearTurnAngle = config.readDouble("gearTurnAngle", gearTurnAngle);
		gearTurnBackAngle = config.readDouble("gearTurnBackAngle", gearTurnBackAngle);
		gearTurnBackDistance = config.readDouble("gearTurnBackDistance", gearTurnBackDistance);
		gearTurnBackToHopper = config.readDouble("gearTurnBackToHopper", gearTurnBackToHopper);
		usingHorn = config.readBoolean("usingHorn", usingHorn);
		System.out.println("GTBD: " + gearTurnBackDistance);
		System.out.println("GTBA: " + gearTurnBackAngle);
	}
}
