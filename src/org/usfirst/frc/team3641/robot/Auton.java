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
	private static int index;
	
	private static double initalLeftDistance, initalRightDistance;
	
	private static boolean lineFound = false;
	private static boolean endOfLine = false;
	
	private static boolean alreadyRunning;
	private static Timer timeoutTimer, autonTimer;
	
	private static boolean usingHorn = true;

	public static UDP udp;

	
	/**
	 * All the Auton states we can be in. For each state you use in a routine, add it here.
	 */
	private enum states
	{
		START, DONE,
		DRIVE_FORWARDS,
		DRIVE_TO_HOPPER_LINE,
		TURN_TO_HOPPER,
		DRIVE_TO_HOPPER,
		TARGET_BOILER,
		SCORE_RANKING_POINT,
		DRIVE_TO_GEAR_TURN,
		TURN_TO_GEAR,
		DRIVE_TO_GEAR,
		PLACE_GEAR,
		BACK_AWAY_FROM_GEAR,
		TURN_FROM_GEAR_TO_NORMAL,
		DRIVE_FROM_GEAR_TURN_TO_HOPPER_LINE,
		CALM_DOWN;
	}
	
	/**
	 * All the Auton routines.
	 */
	public enum modes
	{
		DO_NOTHING,
		CROSS_LINE,
		HOPPER_AUTON,
		LEFT_GEAR_AUTON,
		MIDDLE_GEAR_AUTON,
		RIGHT_GEAR_AUTON,
		COMBO_AUTON,
		LINE_ALIGN,
		LINE_FOLLOW,
		CENTER_GEAR;
		
		private static final modes[] values = modes.values(); //We cache the value array for preformance

		/**
		 * Get an auton mode from an integer.
		 * 
		 * @param i The number of the mode we want to run
		 * @return The mode with that number
		 */
		public static modes fromInt(int i)
		{
			if(i >= values.length || i<0)
			{
				Console.printWarning("Auton " + i + " out of range. Defaulting to " + values[0].toString());
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

	/**
	 * Initializes all the objects that need to be initialized.
	 */
	private Auton()
	{
		timeoutTimer = new Timer();
		autonTimer = new Timer();
		config = new PropertyReader("Auton");
	}
	
	/**
	 * Selects the auton mode and alliance.
	 * Also resets all the values possibly used in auton, so we can test without rebooting the rio.
	 * 
	 * @param mode The auton routine you wish to run.
	 * @param redAlliance The alliance you are on. This value effects the turns, since the field is
	 * asymmetrical this year.
	 */
	public static void setup(modes mode, boolean redAlliance)
	{
		Console.print("Starting Auton: " + mode.toString() + " on the " + ((redAlliance) ? "Red" : "Blue") + " Alliance\n", Constants.Verbosity.Level.LOW);
		readConfig();
		autonState = states.START;
		alreadyRunning = false;
		autonMode = mode;
		onRedAlliance = redAlliance;
		Tracking.resetState();
		Gearbox.shift(Gearbox.Gear.LOW);
//		Sensors.resetDriveDistance();
		Intake.setFlapDown();
		initTimeout(0);
		autonTimer.reset();
		autonTimer.start();
	}
	
	/**
	 * Run the auton in the mode and alliance selected in the setup method.
	 */
	public static void run()
	{
		Sensors.printAll();
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
			
		case LEFT_GEAR_AUTON:
			sideGearAuton(true);
			break;
			
		case RIGHT_GEAR_AUTON:
			sideGearAuton(false);
			break;
			
		case MIDDLE_GEAR_AUTON:
			middleGearAuton();
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
			
		case CENTER_GEAR:
			centerGear();
		}
	}
	
	/**
	 * Second simplest auton after DO_NOTHING. Just drives across the line and stops.
	 */
	@SuppressWarnings("incomplete-switch") //I don't care about the other values, I know they won't be used :P
	private static void crossBaseline()
	{
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_FORWARDS);
			break;

		case DRIVE_FORWARDS:
			boolean reachedLine = driveBy(Constants.Auton.distanceToBaseline);
			if(reachedLine) increment(states.DONE);
			break;
		}
	}

	/**
	 * Drives to the hopper, auto targets the turret, then fires the shooter until auton is over.
	 */
	@SuppressWarnings("incomplete-switch")
	private static void hopperAuton()
	{
		Tracking.State trackingState;
		double angle;
		boolean doneDriving, doneTurning, hitTheWall;
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_TO_HOPPER_LINE);
			break;
			
		case DRIVE_TO_HOPPER_LINE:
			doneDriving = driveBy(Constants.Auton.distanceToHopperLine, 2);
			if(doneDriving) increment(states.TURN_TO_HOPPER);
			break;

		case TURN_TO_HOPPER:
			angle = (onRedAlliance) ? Constants.Auton.hopperTurnAngle : -Constants.Auton.hopperTurnAngle; //If on red alliance, turn right. If on blue, turn left.
			doneTurning = turnBy(angle, 1.3);
			if(doneTurning) increment(states.DRIVE_TO_HOPPER);
			break;
			
		case DRIVE_TO_HOPPER:
			doneDriving = driveBy(Constants.Auton.distanceToHopperFromTurn, 1);
			hitTheWall = didWeHitSomething(.5);
			if(hitTheWall) Console.print("Ouch!", Constants.Verbosity.Level.LOW);
			if(doneDriving || hitTheWall) increment(states.TARGET_BOILER);
			break;

		case TARGET_BOILER:
			trackingState = Tracking.target(Tracking.Mode.FUEL_MODE);
			if(trackingState == Tracking.State.TRACKED_FUEL) increment(states.SCORE_RANKING_POINT);
			break;
			
		case SCORE_RANKING_POINT:
			Shooter.setDistance(Constants.Auton.distanceToHopperLine);
			Shooter.fire();
			break;
		}
	}

	/**
	 * Drives to the gear loading station and places the gear.
	 */
	@SuppressWarnings("incomplete-switch")
	private static void sideGearAuton(boolean left) //TODO: Add support for each of the three stations with different starting points.
	{
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_TO_GEAR_TURN);
			break;
			
		case DRIVE_TO_GEAR_TURN:
			boolean reachedLine = driveBy(Constants.Auton.distanceToGearTurn);
			if(reachedLine) increment(states.TURN_TO_GEAR);
			break;

		case TURN_TO_GEAR:
			double angle = Constants.Auton.gearTurnAngle;
			angle = (left) ? angle : -angle;	//We don't care about the fact that red and blue are mirrored, just left or right
			boolean doneTurning = turnBy(angle, 1);
			if(doneTurning) increment(states.DRIVE_TO_GEAR);
			break;
			
		case DRIVE_TO_GEAR:
			boolean reachedHopper = driveBy(Constants.Auton.distanceToGearFromTurn, .5);
			boolean hitTheWall = didWeHitSomething(.1);
			if(reachedHopper || hitTheWall) increment(states.PLACE_GEAR);
			break;
			
		case PLACE_GEAR:
			GearThingy.extend();
			increment(states.DONE);
			break;
		}		
	}
	
	@SuppressWarnings("incomplete-switch")
	public static void middleGearAuton()
	{
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_TO_GEAR);
			break;
			
		case DRIVE_TO_GEAR:
			boolean reachedHopper = driveBy(Constants.Auton.middleGearDistance, true, 0);
			boolean hitTheWall = didWeHitSomething(.1);
			if(reachedHopper || hitTheWall) increment(states.DONE);
			break;
			
		case PLACE_GEAR:
			GearThingy.extend();
			increment(states.DONE);
			break;
		}
	}
	
	/**
	 * Drives to the gear station, places the gear, then backs into hopper, tracks, and fires.
	 */
	@SuppressWarnings("incomplete-switch")
	private static void comboAuton()
	{
		double angle;
		boolean doneTurning, doneDriving, hitTheWall, doneWaiting;
		Tracking.State trackingState;
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_TO_GEAR_TURN);
			break;
			
		case DRIVE_TO_GEAR_TURN:
			doneDriving = driveBy(Constants.Auton.distanceToGearTurn);
			if(doneDriving) increment(states.TURN_TO_GEAR);
			break;

		case TURN_TO_GEAR:
			angle = (onRedAlliance) ? Constants.Auton.gearTurnAngle : -Constants.Auton.gearTurnAngle; //If on red alliance, turn right. If on blue, turn left.
			doneTurning = turnBy(angle);
			if(doneTurning) increment(states.DRIVE_TO_GEAR);
			break;
			
		case DRIVE_TO_GEAR:
			doneDriving = driveBy(Constants.Auton.distanceToGearFromTurn);
			if(doneDriving) increment(states.BACK_AWAY_FROM_GEAR);
			break;
						
		case BACK_AWAY_FROM_GEAR:
			doneDriving = driveBy(Constants.Auton.gearTurnBackDistance);
			if(doneDriving) increment(states.TURN_FROM_GEAR_TO_NORMAL);
			break;
			
		case TURN_FROM_GEAR_TO_NORMAL:
			angle = (onRedAlliance) ? Constants.Auton.gearTurnBackAngle : -Constants.Auton.gearTurnBackAngle; //If on red alliance, turn left. If on blue, turn right.
			doneTurning = turnBy(angle);
			if(doneTurning) increment(states.DRIVE_TO_HOPPER);
			break;
			
		case DRIVE_TO_HOPPER:
			doneDriving = driveBy(Constants.Auton.gearTurnBackToHopper);
			hitTheWall = didWeHitSomething(.1);
			if(doneDriving || hitTheWall) increment(states.TARGET_BOILER);
			break;
			
		case TARGET_BOILER:
			trackingState = Tracking.target(Tracking.Mode.FUEL_MODE);
			if(trackingState == Tracking.State.TRACKED_FUEL) increment(states.SCORE_RANKING_POINT);
			break;
			
		case SCORE_RANKING_POINT:
			Shooter.setDistance(Constants.Auton.distanceToHopperLine);
			Shooter.fire();
			break;
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	public static void centerGear()
	{
		switch(autonState)
		{
		case START:
			increment(states.DRIVE_TO_GEAR);
			break;
			
		case DRIVE_TO_GEAR:
			boolean done = driveBy(-1.45);
			if(done) increment(states.DONE);
			break;
		}
	}
	
	/**
	 * Aligns with a line using vision on a beagle bone.
	 */
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
			Console.print("RECEIVED: " + part1);

			DriveBase.turnDegrees(part1_double, 2);
		}

	}

	/**
	 * Follows a line using vision on a beagle bone.
	 */
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
				Console.print("RECEIVED: " + part1);

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
				Console.print("RECEIVED: " + part1);

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
				Console.print("" + range);

				if (range < 0.2) 
				{
					DriveBase.driveArcade(0, 0);
					endOfLine = true;
				}
			}
		}
	}

	/**
	 * Drives for a specified distance with a timeout.
	 * 
	 * @param distance The distance to drive in meters.
	 * @param timeout The number of seconds to try driving before timing out.
	 * @return True once done driving or when the timeout is up.
	 */
	public static boolean driveBy(double distance, boolean tank, double timeout)
	{
		if(!alreadyRunning)
		{
			initTimeout(timeout);
			Console.print("iLD: " + initalLeftDistance);
			Console.print("iRD: " + initalRightDistance);
			initalLeftDistance = Sensors.getLeftDriveDistance();
			initalRightDistance = Sensors.getRightDriveDistance();
			Console.print("niLD: " + initalLeftDistance);
			Console.print("niRD: " + initalRightDistance);

			if(usingHorn) Horn.setHorn(true);
			Console.print("Starting to drive by " + distance + "m in state " + autonState, Constants.Verbosity.Level.LOW);
			alreadyRunning = true;
		}
		String currentDistance = String.format("%.2f", Sensors.getLeftDriveDistance()-initalLeftDistance);
		Console.print(currentDistance + "m out of " + distance + "m", Constants.Verbosity.Level.HIGH);
		double error;
		if(tank) error = DriveBase.driveTankTo(initalLeftDistance  + distance, initalRightDistance + distance);
		else error = DriveBase.driveTo(initalLeftDistance  + distance);
		//Console.print("Error: " + error);
		boolean  withinThreshold = (Math.abs(error) <= Constants.Thresholds.AUTON_DRIVE_DISTANCE_ACCEPTABLE_ERROR);
		
		return (withinThreshold || timeoutUp(timeout));
	}
	
	public static boolean driveBy(double distance, double timeout)
	{
		return driveBy(distance, false, timeout);
	}
	
	public static boolean driveBy(double distance, boolean tank)
	{
		return driveBy(distance, tank, 0);
	}
	
	/**
	 * Drives for a specified distance.
	 * 
	 * @param distance The distance to drive in meters.
	 * @return True once done driving.
	 */
	public static boolean driveBy(double distance)
	{
		return driveBy(distance, 0);
	}
	
	/**
	 * Turns a specified number of degrees with a timeout.
	 * 
	 * @param angle The angle to turn to in degrees.
	 * @param timeout The number of seconds to try driving before timing out.
	 * @return True once done turning or when the timeout is up.
	 */
	private static boolean turnBy(double angle, double timeout)
	{
		if(!alreadyRunning)
		{
			Console.print("Starting to turn by " + angle + "° in state " + autonState, Constants.Verbosity.Level.LOW);
			initTimeout(timeout);
			Sensors.resetGyro();
			alreadyRunning = true;
			doneTurning = new boolean[Constants.Thresholds.NUMBER_OF_TURNING_CHECKS];
		}
		Console.print(Sensors.getAngle() + "° out of " + angle + "°", Constants.Verbosity.Level.HIGH);
		boolean done = DriveBase.turnTo(angle, 1);
		doneTurning[index] = done;
		index++;
		if(index >= doneTurning.length) index = 0;
		
		return (allAreTrue(doneTurning) || timeoutUp(timeout));
	}
	
	/**
	 * Turns a specified number of degrees.
	 * 
	 * @param angle The angle to turn to in degrees.
	 * @return True once done turning.
	 */
	private static boolean turnBy(double angle)
	{
		return turnBy(angle, 0);
	}
	
	/**
	 * Waits for a specified number of seconds.
	 * 
	 * @param timeout The time to wait in seconds.
	 * @return True if time is up.
	 */
	private static boolean waitFor(double timeout)
	{
		if(!alreadyRunning)
		{
			initTimeout(timeout);
			alreadyRunning = true;
		}
		return timeoutUp(timeout);
	}

	/**
	 * Starts a timeout timer with the specified number of seconds.
	 * 
	 * @param Timeout The number of seconds on the timer.
	 */
	private static void initTimeout(double Timeout)
	{
		if(Timeout != 0) Console.print("Starting a " + Timeout + "s Timer", Constants.Verbosity.Level.LOW);
		timeoutTimer.reset();
		timeoutTimer.start();
	}
	
	/**
	 * Checks if time is up on the timeout timer.
	 * 
	 * @param timeout The number of seconds on the timer.
	 * @return True once the time on the timer has reached the timeout.
	 */
	private static boolean timeoutUp(double timeout)
	{
		if(Constants.disableAutonTimeouts) return false;
		if(timeout == 0)
		{
			return false;
		}
		double time = timeoutTimer.get();
		Console.print(time + "s out of " + timeout + "s", Constants.Verbosity.Level.HIGH);
		boolean done = (time >= timeout);
		Console.print("\nScrew it, it's close enough. We're out of time", Constants.Verbosity.Level.LOW);
		return done;
	}
	
	/**
	 * Checks if the navx thinks we're still with a minimum time (We don't want it to return
	 * instantly because the robot hasn't started moving yet.
	 * 
	 * @param minTime Maximum time in seconds we expect the robot to take to start moving.
	 * @return True once the navx thinks we are still and the minimum time has been reached.
	 */
	private static boolean didWeHitSomething(double minTime)
	{
		return (Sensors.isStill() && timeoutTimer.get() > minTime); //We want to know if we are still, but it doesn't help us if it returns true immediately before we started driving.
	}
	
	/**
	 * Increments from the current state to the specified state. Stops driving, and resets the other
	 * variables the states depend on.
	 * 
	 * @param state The state to increment to.
	 */
	private static void increment(states state)
	{
		Console.print("Took " + timeoutTimer.get() + "s to complete " + autonState.toString(), Constants.Verbosity.Level.MID);
		Console.print("\nIncrementing from state " + autonState.toString() + " to state " + state.toString(), Constants.Verbosity.Level.LOW);
		autonState = state;
		DriveBase.driveArcade(0, 0); //Stop Driving!
		DriveBase.resetPID();
		initTimeout(0);
		alreadyRunning = false;
		Horn.setHorn(false);
	}
	
	/**
	 * Checks if all elements of an array are true.
	 * 
	 * @param array Array to check.
	 * @return True if all the elements in the array are true
	 */
	private static boolean allAreTrue(boolean[] array)
	{
		for(boolean b : array) if(b == false) return false;
		return true;
	}
	
	/**
	 * Attempts to read values from the config file. If it ever fails, it just uses the previous
	 * value
	 */
	private static void readConfig()
	{
		config.reloadFile();
		Constants.Auton.distanceToBaseline = config.readDouble("distanceToBaseline", Constants.Auton.distanceToBaseline);
		Constants.Auton.distanceToHopperLine = config.readDouble("distanceToHopperLine", Constants.Auton.distanceToHopperLine);
		Constants.Auton.distanceToHopperFromTurn = config.readDouble("distanceToHopperFromTurn", Constants.Auton.distanceToHopperFromTurn);
		Constants.Auton.distanceToGearTurn = config.readDouble("distanceToGearTurn", Constants.Auton.distanceToGearTurn);
		Constants.Auton.distanceToGearFromTurn = config.readDouble("distanceToGearFromTurn", Constants.Auton.distanceToGearFromTurn);
		Constants.Auton.hopperTurnAngle = config.readDouble("hopperTurnAngle", Constants.Auton.hopperTurnAngle);
		Constants.Auton.gearTurnAngle = config.readDouble("gearTurnAngle", Constants.Auton.gearTurnAngle);
		Constants.Auton.gearTurnBackAngle = config.readDouble("gearTurnBackAngle", Constants.Auton.gearTurnBackAngle);
		Constants.Auton.gearTurnBackDistance = config.readDouble("gearTurnBackDistance", Constants.Auton.gearTurnBackDistance);
		Constants.Auton.gearTurnBackToHopper = config.readDouble("gearTurnBackToHopper", Constants.Auton.gearTurnBackToHopper);
		usingHorn = config.readBoolean("usingHorn", usingHorn);
		
		Constants.Auton.middleGearDistance = config.readDouble("middleGearDistance", Constants.Auton.middleGearDistance);
	}
}
