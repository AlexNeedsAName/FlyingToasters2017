package org.usfirst.frc.team3641.robot;

public class Teleop
{
	private static Teleop instance;
	public static PS4 driver;
	public static E3D operator;
	public static Harmonix guitar;
	public static boolean arcadeMode;
	public static boolean b = false;
	public static double driveDirection = 1;
	private static boolean manualShooterMode = false;

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
		else if(driver.isDown(PS4.Button.CIRCLE)) Tracking.target(Tracking.Mode.GEAR_MODE);
		else if(!SubAuton.alreadyDriving)
		{
			if(arcadeMode)
			{
				if(Constants.runningAleksBot) DriveBase.driveArcade(operator.getAxis(E3D.Axis.Y), operator.getAxis(E3D.Axis.Z));
				else DriveBase.driveArcade(driver.getAxis(PS4.Axis.LEFT_Y) * driveDirection, driver.getAxis(PS4.Axis.RIGHT_X));
			}
			else
			{
				if(driveDirection == 1) DriveBase.driveTank(driver.getAxis(PS4.Axis.LEFT_Y), driver.getAxis(PS4.Axis.RIGHT_Y));
				else DriveBase.driveTank(-driver.getAxis(PS4.Axis.RIGHT_Y), -driver.getAxis(PS4.Axis.LEFT_Y));
			}
		}
		
		//Gearbox Stuff
		if(driver.isPressed(PS4.Button.RIGHT_BUMPER)) Gearbox.shift(Gearbox.Gear.LOW);
		else if(driver.isReleased(PS4.Button.RIGHT_BUMPER)) Gearbox.shift(Gearbox.Gear.HIGH);
		if(driver.isPressed(PS4.Button.LEFT_BUMPER)) DriveBase.enableClimbingMode();
		if(driver.isReleased(PS4.Button.LEFT_BUMPER)) DriveBase.disableClimbingMode();
		
		//Intake Stuff
		if(operator.isPressed(11)) Intake.setFlapUp();
		else if(operator.isReleased(11)) Intake.setFlapDown();
		if(Hopper.isAgitating() || operator.isDown(7)) Intake.setSpeed(1);
		else if(operator.isDown(E3D.Button.THUMB)) Intake.setSpeed(operator.getAxis(E3D.Axis.Y)/2);
		else Intake.setSpeed(-driver.getAxis(PS4.Axis.LEFT_TRIGGER) + driver.getAxis(PS4.Axis.RIGHT_TRIGGER));
				
		
		//Adjust Hopper Setpoint
		if(operator.isDown(5)) Hopper.runReverse();
		else if(operator.isDown(7)) Constants.Hopper.CENTER_AGITATOR_SPEED-=0.01;
		else if(operator.isDown(8)) Constants.Hopper.CENTER_AGITATOR_SPEED+=0.01;
		
		//Move the Shooter Setpoint.
		if(operator.isDown(10)) Constants.Shooter.TARGET_RPM += Constants.Shooter.ADJUSTMENT_MULTIPLIER;
		if(operator.isDown(9)) Constants.Shooter.TARGET_RPM -= Constants.Shooter.ADJUSTMENT_MULTIPLIER;
		
		if(operator.isDown(12))
		{
			double shooterTarget = operator.getAxis(E3D.Axis.THROTTLE);
			Shooter.set(shooterTarget);
			if(operator.isDown(E3D.Button.TRIGGER)) Hopper.Agitate();
		}
		else
		{
			
			//Run the flywheel.
			if(operator.isDown(E3D.Button.TRIGGER) || operator.isDown(10) || operator.isDown(9)) Console.print("Shooter Error: " + String.format("%.2f", Shooter.setRPM(Constants.Shooter.TARGET_RPM)) + " RPM");
			else Shooter.set(0);
					
			//Run the Hopper
			if(operator.isDown(E3D.Button.TRIGGER)) Hopper.autoAgitate();
			else Hopper.stopAgitating();
		}
		
		//Gear Thingy Stuff
		if(operator.isPressed(3)) GearThingy.setState(GearThingy.State.INTAKING);
		else if(operator.isReleased(3)) GearThingy.setState(GearThingy.State.DONE_INTAKING);
		else if(operator.isPressed(4)) GearThingy.setState(GearThingy.State.PLACING);
		else if(operator.isReleased(4)) GearThingy.setState(GearThingy.State.RESTING);
		GearThingy.runCurrentState();
	}

	/**
	 * Runs the teleop code for driving with the Guitar as a controller.
	 */
	public static void runGuitar()
	{
		guitar.poll();
		DriveBase.driveArcade(guitar.getAxis(Harmonix.Axis.WHAMMY_BAR) * guitar.getAxis(Harmonix.Axis.STRUM), guitar.getAxis(Harmonix.Axis.BUTTONS));
	}
	
	public static void dumpPnumatics()
	{
		b = !b;
		if(b)
		{
			Gearbox.shift(Gearbox.Gear.HIGH);
			Intake.setFlapUp();
			Intake.intakeUp();
		}
		else
		{
			Gearbox.shift(Gearbox.Gear.LOW);
			Intake.setFlapDown();
			Intake.intakeDown();
		}
		Gearbox.setPTO(b);
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