package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Auton
{
	private static Auton instance;
	private static States autonState;
	private static Routines autonMode;
	private static Stages autonStage;
	private static boolean onBlueAlliance;
		
	private static boolean lineFound = false;
	private static boolean endOfLine = false;
	
	private static boolean alreadyRunning;
	private static Timer timeoutTimer, autonTimer;
	
	public static UDP udp;

	
	/**
	 * All the Auton states we can be in. For each state you use in a routine, add it here.
	 */
	private enum States
	{
		START, DONE,
		DRIVE_FORWARDS,
		TURN,
		DRIVE_TO_HOPPER_LINE,
		TURN_TO_HOPPER,
		DRIVE_TO_HOPPER,
		TARGET_BOILER,
		SCORE_RANKING_POINT,
		DRIVE_TO_GEAR_TURN,
		TURN_TO_GEAR,
		DRIVE_TO_GEAR,
		READY_GEAR,
		PLACE_GEAR,
		BACK_AWAY_FROM_GEAR,
		TURN_FROM_GEAR_TO_NORMAL,
		DRIVE_FROM_GEAR_TURN_TO_HOPPER_LINE,
		CALM_DOWN;
	}
	
	/**
	 * All the Auton routines.
	 */
	public enum Routines
	{
		DO_NOTHING,
		CROSS_LINE,
		HOPPER_AUTON,
		LEFT_GEAR_AUTON,
		MIDDLE_GEAR_AUTON,
		RIGHT_GEAR_AUTON,
		COMBO_AUTON,
		TEST_DRIVE_PID,
		TEST_ROTATION_PID,
		LINE_ALIGN,
		LINE_FOLLOW;
		
		private static final Routines[] values = Routines.values(); //We cache the value array for preformance

		/**
		 * Get an auton mode from an integer.
		 * 
		 * @param i The number of the mode we want to run
		 * @return The mode with that number
		 */
		public static Routines fromInt(int i)
		{
			if(i >= values.length || i<0)
			{
				Console.printWarning("Auton " + i + " out of range. Defaulting to " + values[0].toString());
				i = 0;
			}
			return values[i];
		}
	}
	
	private static enum Stages
	{
		START,
		GEAR,
		PREP_FOR_SHOOT,
		SHOOT;
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
	}
	
	/**
	 * Selects the auton mode and alliance.
	 * Also resets all the values possibly used in auton, so we can test without rebooting the rio.
	 * 
	 * @param mode The auton routine you wish to run.
	 * @param redAlliance The alliance you are on. This value effects the turns, since the field is
	 * asymmetrical this year.
	 */
	public static void setup(Routines mode, boolean redAlliance)
	{
		Console.print("Starting Auton: " + mode.toString() + " on the " + ((redAlliance) ? "Red" : "Blue") + " Alliance\n", Constants.Verbosity.Level.LOW);
		Constants.Auton.reloadConfig();
		autonState = States.START;
		alreadyRunning = false;
		autonMode = mode;
		onBlueAlliance = !redAlliance;
		Tracking.resetState();
		Gearbox.shift(Gearbox.Gear.LOW);
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
			if(onBlueAlliance) gearAuton(1);
			else gearAuton(3);
			break;
						
		case MIDDLE_GEAR_AUTON:
			gearAuton(2);
			break;
			
		case RIGHT_GEAR_AUTON:
			if(onBlueAlliance) gearAuton(3);
			else gearAuton(1);
			break;
			
		case COMBO_AUTON:
			comboAuton();
			break;
			
		case TEST_DRIVE_PID:
			testDrivePID();
			break;
			
		case TEST_ROTATION_PID:
			testRotationPID();
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
	private static void testDrivePID()
	{
		switch(autonState)
		{
		case START:
			increment(States.DRIVE_FORWARDS);
			break;
			
		case DRIVE_FORWARDS:
			SubAuton.driveBy(Constants.Dashboard.DriveTestDistance);
			break;
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private static void testRotationPID()
	{
		switch(autonState)
		{
		case START:
			increment(States.TURN);
			break;
			
		case TURN:
			SubAuton.rotateBy(Constants.Dashboard.RotationTestDistance);
			break;
		}
	}

	
	/**
	 * Second simplest auton after DO_NOTHING. Just drives across the line and stops.
	 */
	@SuppressWarnings("incomplete-switch") //I don't care about the other values, I know they won't be used :P
	private static void crossBaseline()
	{
		double error;
		boolean reachedLine;
		switch(autonState)
		{
		case START:
			increment(States.DRIVE_FORWARDS);
			break;

		case DRIVE_FORWARDS:
			error = SubAuton.driveBy(Constants.Auton.baselineDistance);
			reachedLine = Math.abs(error) <= Constants.Thresholds.AUTON_DRIVE_DISTANCE_ACCEPTABLE_ERROR;
			if(reachedLine) increment(States.DONE);
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
		double error;
		boolean doneDriving, doneTurning;
		switch(autonState)
		{
		case START:
			increment(States.DRIVE_TO_HOPPER_LINE);
			break;
			
		case DRIVE_TO_HOPPER_LINE:
			error = SubAuton.driveBy(Constants.Auton.hopperDistanceToTurn);
			doneDriving = Math.abs(error) <= Constants.Thresholds.AUTON_DRIVE_DISTANCE_ACCEPTABLE_ERROR;
			if(doneDriving) increment(States.TURN_TO_HOPPER);
			break;

		case TURN_TO_HOPPER:
			angle = (onBlueAlliance) ? Constants.Auton.hopperTurnAngle : -Constants.Auton.hopperTurnAngle; //If on red alliance, turn right. If on blue, turn left.
			error = SubAuton.rotateBy(angle);
			doneTurning = Math.abs(error) <= Constants.Thresholds.AUTON_DRIVE_ANGLE_ACCEPTABLE_ERROR;
			if(doneTurning) increment(States.DRIVE_TO_HOPPER);
			break;
			
		case DRIVE_TO_HOPPER:
			
			boolean timeoutUp = waitFor(1);
			Intake.setFlapUp();
			Shooter.setRPM(Constants.Shooter.TARGET_RPM);
			error = SubAuton.driveBy(Constants.Auton.hopperDistanceAfterTurn);
			doneDriving = Math.abs(error) <= Constants.Thresholds.AUTON_DRIVE_DISTANCE_ACCEPTABLE_ERROR;
			Console.print("Error:" + error);
			//hitTheWall = didWeHitSomething(.5);
			//if(hitTheWall) Console.print("Ouch!", Constants.Verbosity.Level.LOW);
			if(doneDriving || timeoutUp) increment(States.SCORE_RANKING_POINT);
			break;

		case TARGET_BOILER:
			trackingState = Tracking.target(Tracking.Mode.FUEL_MODE);
			if(trackingState == Tracking.State.TRACKED_FUEL) increment(States.SCORE_RANKING_POINT);
			break;
			
		case SCORE_RANKING_POINT:
			Shooter.setRPM(Constants.Shooter.TARGET_RPM);
			Hopper.Agitate();
			Intake.setSpeed(1);
			break;
		}
	}

	/**
	 * Drives to the specified gear and places it. On the red alliance, left is 1, middle is 2, and right is 3.
	 * On the blue alliance, left is 3, middle is 2, and right is 1. This allows us to use the same points to
	 * line up on both sides of the field.
	 * 
	 * @param gearNumber Gear number one, two, or three.
	 */
	@SuppressWarnings("incomplete-switch")
	private static void gearAuton(int gearNumber)
	{
		double distance = 0;
		double angle = 0, error;
		boolean done = false;
		switch(autonState)
		{
		case START:
			if(gearNumber == 2) increment(States.DRIVE_TO_GEAR);
			else increment(States.DRIVE_TO_GEAR_TURN);
			break;
			
		case DRIVE_TO_GEAR_TURN:
			if(gearNumber == 1) distance = Constants.Auton.gearOneDistanceToTurn;
			else if(gearNumber == 3) distance = Constants.Auton.gearThreeDistanceToTurn;
			
			done = Math.abs(SubAuton.driveBy(distance)) < Constants.Thresholds.AUTON_DRIVE_DISTANCE_ACCEPTABLE_ERROR;
			if(done) increment(States.TURN_TO_GEAR);
			break;

		case TURN_TO_GEAR:
			if(gearNumber == 1) angle = Constants.Auton.gearOneTurnAngle;
			else if(gearNumber == 3) angle = Constants.Auton.gearThreeTurnAngle;
			angle = (onBlueAlliance) ? angle : -angle;
			error = SubAuton.rotateBy(angle);
			done = Math.abs(error) <= Constants.Thresholds.AUTON_DRIVE_ANGLE_ACCEPTABLE_ERROR;
			if(done) increment(States.DRIVE_TO_GEAR);
			SmartDashboard.putBoolean("Done Turning", done);
			SmartDashboard.putBoolean("Still", Sensors.isStill());
			
			break;
			
		case DRIVE_TO_GEAR:
			if(gearNumber == 1) distance = Constants.Auton.gearOneDistanceAfterTurn;
			else if(gearNumber == 2) distance = Constants.Auton.gearTwoDistance;
			else if (gearNumber == 3) distance = Constants.Auton.gearThreeDistanceAfterTurn;
			
			done = Math.abs(SubAuton.driveBy(distance)) < Constants.Thresholds.AUTON_DRIVE_DISTANCE_ACCEPTABLE_ERROR;
			if(done) increment(States.READY_GEAR);
			break;
			
		case READY_GEAR:
			DriveBase.disableClimbingMode();
			DriveBase.driveArcade(0, 0);
			GearThingy.setState(GearThingy.State.PLACING);
			increment(States.PLACE_GEAR);
			break;
			
		case PLACE_GEAR:
			if(GearThingy.runCurrentState() == GearThingy.State.RESTING)
			{
				increment(States.DONE);
				GearThingy.runCurrentState();
			}
			break;
			
		case DONE:
			DriveBase.driveArcade(0, 0);
			break;
		}		
	}
	
	@SuppressWarnings("incomplete-switch")
	private static void transitionGearToHopper()
	{
		boolean done;
		double angle, distance, error;
		switch(autonState)
		{
		case START:
			//GearThingy.setUp();
			increment(States.BACK_AWAY_FROM_GEAR);
			break;
			
		case BACK_AWAY_FROM_GEAR:
			error = SubAuton.driveBy(-Constants.Auton.gearThreeDistanceAfterTurn);
			done = Math.abs(error) <= Constants.Thresholds.AUTON_DRIVE_DISTANCE_ACCEPTABLE_ERROR;
			if(done) increment(States.TURN_TO_HOPPER);
			break;
			
		case TURN_TO_HOPPER:
			angle = Constants.Auton.hopperTurnAngle - Constants.Auton.gearThreeTurnAngle;
			angle = (onBlueAlliance) ? angle : -angle;
			error = SubAuton.rotateBy(angle);
			done = Math.abs(error) <= Constants.Thresholds.AUTON_DRIVE_ANGLE_ACCEPTABLE_ERROR;
			if(done) increment(States.DRIVE_TO_HOPPER);
			break;
			
		case DRIVE_TO_HOPPER:
			distance = Constants.Auton.gearThreeTurnToHopperDistance;
			error = SubAuton.driveBy(distance);
			done = Math.abs(error) <= Constants.Thresholds.AUTON_DRIVE_DISTANCE_ACCEPTABLE_ERROR;
			if(done) increment(States.DONE);
			break;
		}
	}
	
	private static void comboAuton()
	{
		switch(autonStage)
		{
		case START:
			autonStage = Stages.GEAR;
			increment(States.START);
			break;
			
		case GEAR:
			gearAuton(3);
			if(autonState == States.DONE)
			{
				autonStage = Stages.PREP_FOR_SHOOT;
				increment(States.START);
			}
			break;
			
		case PREP_FOR_SHOOT:
			transitionGearToHopper();
			if(autonState == States.DONE)
			{
				increment(States.DRIVE_TO_HOPPER);
				autonStage = Stages.SHOOT;
			}
			break;
			
		case SHOOT:
			hopperAuton();
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
	private static void increment(States state)
	{
		SubAuton.resetDriveBy();
		SubAuton.resetRotateBy();
		Console.print("Took " + timeoutTimer.get() + "s to complete " + autonState.toString(), Constants.Verbosity.Level.MID);
		Console.print("\nIncrementing from state " + autonState.toString() + " to state " + state.toString(), Constants.Verbosity.Level.LOW);
		autonState = state;
		DriveBase.driveArcade(0, 0); //Stop Driving!
		DriveBase.resetPID();
		initTimeout(0);
		alreadyRunning = false;
	}
}
