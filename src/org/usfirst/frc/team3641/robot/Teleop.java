package org.usfirst.frc.team3641.robot;

public class Teleop
{
	private static Teleop instance;
	public static PS4 driver;
	public static E3D operator;
	public static Harmonix guitar;
	public static boolean arcadeMode;
	public static double driveDirection = 1;
	public static CheesyDrive cheesyDrive;

	public static Teleop getInstance()
	{
		if(instance == null) instance = new Teleop();
		return instance;
	}

	/**
	 * Initializes Teleop and its controllers.
	 */
	private Teleop()
	{
		driver = new PS4(Constants.Controllers.DRIVER);
		operator = new E3D(Constants.Controllers.OPERATOR);
		guitar = new Harmonix(Constants.Controllers.GUITAR);
		cheesyDrive = new CheesyDrive();
		arcadeMode = true;
	}

	/**
	 * Runs the main teleop code. Should be run from a loop (teleopPerodic)
	 */
	public static void run()
	{		
		driver.poll();
		operator.poll();
						
		if(operator.isDown(E3D.Button.THUMB_POV_UP)) Console.print("Up");
		if(operator.isDown(E3D.Button.THUMB_POV_DOWN)) Console.print("Down");
		if(operator.isDown(E3D.Button.THUMB_POV_LEFT)) Console.print("Left");
		if(operator.isDown(E3D.Button.THUMB_POV_RIGHT)) Console.print("Right");
		
		//Debug Stuff
		if(driver.isDown(PS4.Button.TOUCHPAD_BUTTON)) Sensors.resetSensors();
		if(driver.isPressed(PS4.Button.SHARE)) Robot.underglow.setColor(RGB.Color.BLUE);
		else if(driver.isPressed(PS4.Button.OPTIONS)) Robot.underglow.setColor(RGB.Color.RED);
		else if(driver.isPressed(PS4.Button.PLAYSTATION_BUTTON)) Robot.underglow.setColor(RGB.Color.OFF);
		else if(driver.isReleased(PS4.Button.SHARE) || driver.isReleased(PS4.Button.OPTIONS) || driver.isReleased(PS4.Button.PLAYSTATION_BUTTON)) Robot.underglow.setAllianceColor();
						
		//Change Settings with D-Pad
		if(driver.isPressed(PS4.Button.DPAD_LEFT)) driveDirection = 1;
		else if(driver.isPressed(PS4.Button.DPAD_RIGHT)) driveDirection = -1;
		if(driver.isPressed(PS4.Button.DPAD_UP)) arcadeMode = true;
		else if(driver.isPressed(PS4.Button.DPAD_DOWN)) arcadeMode = false;
						
		//PTO PID Lock
		if(driver.isPressed(PS4.Button.TRIANGLE) && Gearbox.inPTOMode()) DriveBase.lockDrivebase();
		else if(driver.isReleased(PS4.Button.TRIANGLE)) DriveBase.unlockDrivebase();
		
		//Driving and stuff.
		if(DriveBase.isLocked()) DriveBase.runLock();
		else if(operator.isDown(E3D.Button.TRIGGER) && !operator.isDown(6)) Tracking.target(Tracking.Mode.FUEL_MODE);
		else if(!SubAuton.alreadyDriving)
		{
			if(arcadeMode)
			{
				if(Constants.runningAleksBot) DriveBase.driveTeleop(operator.getAxis(E3D.Axis.Y), operator.getAxis(E3D.Axis.Z));
				else
				{ 
					//DriveBase.driveTeleop(driver.getAxis(PS4.Axis.LEFT_Y) * driveDirection, driver.getAxis(PS4.Axis.RIGHT_X));
					//cheesyDrive.chezyDrive(driver.getAxis(PS4.Axis.LEFT_Y) * driveDirection, driver.getAxis(PS4.Axis.RIGHT_X), driver.isPressed(PS4.Button.RIGHT_TRIGGER_BUTTON));
					DriveBase.driveGrilledCheese(driver.getAxis(PS4.Axis.LEFT_Y) * driveDirection, driver.getAxis(PS4.Axis.RIGHT_X));
				}
			}
			else
			{
				if(driveDirection == 1) DriveBase.driveTank(driver.getAxis(PS4.Axis.LEFT_Y), driver.getAxis(PS4.Axis.RIGHT_Y));
				else DriveBase.driveTank(-driver.getAxis(PS4.Axis.RIGHT_Y), -driver.getAxis(PS4.Axis.LEFT_Y));
			}
		}
		if(operator.isReleased(E3D.Button.TRIGGER)) Tracking.resetState();
		
		//Gearbox Stuff
		if(driver.isPressed(PS4.Button.RIGHT_BUMPER)) Gearbox.shift(Gearbox.Gear.HIGH);
		else if(driver.isReleased(PS4.Button.RIGHT_BUMPER)) Gearbox.shift(Gearbox.Gear.LOW);
		if(driver.isPressed(PS4.Button.LEFT_BUMPER)) DriveBase.enableClimbingMode();
		if(driver.isReleased(PS4.Button.LEFT_BUMPER)) DriveBase.disableClimbingMode();
		
		//Intake Stuff
		if(operator.isPressed(11)) Intake.setFlapUp();
		else if(operator.isReleased(11)) Intake.setFlapDown();
		if(Hopper.isAgitating() || operator.isDown(7)) Intake.setSpeed(1);
		else if(operator.isDown(E3D.Button.THUMB)) Intake.setSpeed(5*operator.getAxis(E3D.Axis.Y)/8);
		else Intake.setSpeed(-driver.getAxis(PS4.Axis.LEFT_TRIGGER) + driver.getAxis(PS4.Axis.RIGHT_TRIGGER));
		
		//Adjust Hopper Setpoint
		if(operator.isDown(5)) Hopper.runReverse();
		else if(operator.isDown(7)) Constants.Hopper.CENTER_AGITATOR_SPEED-=0.01;
		else if(operator.isDown(8)) Constants.Hopper.CENTER_AGITATOR_SPEED+=0.01;
		
		//Move the Shooter Setpoint.
		if(operator.isDown(10)) Constants.Shooter.TARGET_RPM += Constants.Shooter.ADJUSTMENT_MULTIPLIER;
		if(operator.isDown(9)) Constants.Shooter.TARGET_RPM -= Constants.Shooter.ADJUSTMENT_MULTIPLIER;
		
		//Run the flywheel.
		if(operator.isDown(E3D.Button.TRIGGER)) Shooter.setRPM(Constants.Shooter.TARGET_RPM);
		else Shooter.set(0);
		
		//Run the Hopper
		if(operator.isDown(E3D.Button.TRIGGER) && operator.isDown(6)) Shooter.fire();
		else if(operator.isReleased(E3D.Button.TRIGGER)) Shooter.stopFiring();
		
		//Gear Thingy Stuff
		if(operator.isPressed(3)) GearThingy.setState(GearThingy.State.INTAKING);
		else if(operator.isReleased(3)) GearThingy.setState(GearThingy.State.DONE_INTAKING);
		else if(operator.isPressed(4)) GearThingy.setState(GearThingy.State.PLACING);
		else if(operator.isReleased(4)) GearThingy.setState(GearThingy.State.RESTING);
		GearThingy.runCurrentState();
		
		if(driver.isPressed(PS4.Button.CIRCLE)) Tracking.resetState();
		if(driver.isDown(PS4.Button.CIRCLE)) Tracking.target(Tracking.Mode.JUST_DISTANCE);
	}
	
	/**
	 * Runs the teleop code for driving with the Guitar as a controller.
	 */
	public static void runGuitar()
	{
		guitar.poll();
		DriveBase.driveTeleop(guitar.getAxis(Harmonix.Axis.WHAMMY_BAR) * guitar.getAxis(Harmonix.Axis.STRUM), guitar.getAxis(Harmonix.Axis.BUTTONS));
	}
	
	public static double squareInput(double input)
	{
		return squareInput(input, 2);
	}
	
	public static double squareInput(double input, double power)
	{
		return (input < 0) ? -Math.pow(Math.abs(input), power) : Math.pow(input, power); 
	}
	
	public static void runTest()
	{
		driver.poll();
		operator.poll();
		
		if(operator.isPressed(4))
		{
			String data = "3";
			Serial.sendData(data);
			Console.print("Sent " + data + " over serial.");
			
		}
		else if(operator.isDown(4))
		{
			String data = Serial.getData();
			if(data != null) Console.print("Got " + data + " from serial.");
		}
	}
}