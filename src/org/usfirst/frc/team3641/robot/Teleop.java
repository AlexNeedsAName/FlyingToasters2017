package org.usfirst.frc.team3641.robot;

public class Teleop
{
	private static Teleop instance;
	public static PS4 driver;
	public static E3D operator;
	public static Harmonix guitar;
	public static boolean arcadeMode;
	public static boolean b = false;

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
				
		if(driver.isDown(PS4.Button.TOUCHPAD_BUTTON)) Sensors.resetSensors();
						
		//Change Settings with D-Pad
		if(driver.isPressed(PS4.Button.DPAD_LEFT)) DriveBase.setDriveMode(DriveBase.DriveMode.NORMAL);
		else if(driver.isPressed(PS4.Button.DPAD_RIGHT)) DriveBase.setDriveMode(DriveBase.DriveMode.REVERSE);
		if(driver.isPressed(PS4.Button.DPAD_UP)) arcadeMode = true;
		else if(driver.isPressed(PS4.Button.DPAD_DOWN)) arcadeMode = false;
						
		//PTO PID Lock
		if(driver.isPressed(PS4.Button.TRIANGLE) && Gearbox.inPTOMode()) DriveBase.lockDrivebase();
		else if(driver.isReleased(PS4.Button.TRIANGLE)) DriveBase.unlockDrivebase();
		
		//Driving and stuff.
		if(DriveBase.isLocked()) DriveBase.runLock();
		else if(driver.isDown(PS4.Button.CIRCLE)) Tracking.target(Tracking.Mode.GEAR_MODE);
		else if(driver.isPressed(PS4.Button.SHARE)) SubAuton.resetDriveBy();
		else if(driver.isDown(PS4.Button.SHARE)) SubAuton.driveBy(.03); //cm
		else
		{
			if(arcadeMode)
			{
				if(Constants.runningAleksBot) DriveBase.driveArcade(operator.getAxis(E3D.Axis.Y), operator.getAxis(E3D.Axis.Z));
				else DriveBase.driveArcade(driver.getAxis(PS4.Axis.LEFT_Y), driver.getAxis(PS4.Axis.RIGHT_X));
			}
			else
			{
				DriveBase.driveTank(driver.getAxis(PS4.Axis.LEFT_Y), driver.getAxis(PS4.Axis.RIGHT_Y));
			}
		}
		
		//Gearbox Stuff
		if(driver.isPressed(PS4.Button.RIGHT_BUMPER)) Gearbox.shift(Gearbox.Gear.LOW);
		else if(driver.isReleased(PS4.Button.RIGHT_BUMPER)) Gearbox.shift(Gearbox.Gear.HIGH);
		if(driver.isPressed(PS4.Button.LEFT_BUMPER)) DriveBase.enableClimbingMode();
		if(driver.isReleased(PS4.Button.LEFT_BUMPER)) DriveBase.disableClimbingMode();
		
		//Intake Stuff
		if(driver.isPressed(PS4.Button.LEFT_STICK_BUTTON)) Intake.intakeDown();
		else if (driver.isPressed(PS4.Button.RIGHT_STICK_BUTTON)) Intake.intakeUp();
		if(operator.isPressed(11)) Intake.setFlapUp();
		else if(operator.isReleased(11)) Intake.setFlapDown();
		
		if(Hopper.isAgitating() || operator.isDown(7)) Intake.setSpeed(1);
		else if(operator.isDown(E3D.Button.THUMB)) Intake.setSpeed(operator.getAxis(E3D.Axis.Y));
		else Intake.setSpeed(-driver.getAxis(PS4.Axis.LEFT_TRIGGER) + driver.getAxis(PS4.Axis.RIGHT_TRIGGER));
				
		//Move the Shooter Setpoint.
		if(operator.isDown(10)) Constants.Shooter.TARGET_RPM += Constants.Shooter.ADJUSTMENT_MULTIPLIER;
		if(operator.isDown(9)) Constants.Shooter.TARGET_RPM -= Constants.Shooter.ADJUSTMENT_MULTIPLIER;
		
		//Run the flywheel.
		if(operator.isDown(E3D.Button.TRIGGER) || operator.isDown(10) || operator.isDown(9)) Console.print("Shooter Error: " + String.format("%.2f", Shooter.setRPM(Constants.Shooter.TARGET_RPM)) + " RPM");
		else if(operator.isDown(8)) Shooter.set(1);
		else Shooter.set(0);
				
		//Run the Hopper
		if(operator.isDown(5)) Hopper.runReverse();
		else if(operator.isDown(7)) Hopper.Agitate();
		else if(operator.isDown(E3D.Button.TRIGGER)) Hopper.autoAgitate();
		else Hopper.stopAgitating();
		
		if(operator.isDown(E3D.Button.THUMB)) GearThingy.intake();
		else if(operator.isReleased(E3D.Button.THUMB)) GearThingy.stopWheels();
		
		if(operator.isDown(3)) GearThingy.pickupGear();
		else if(operator.isReleased(3)) GearThingy.resetPickupGear();
		
		if(operator.isDown(4)) GearThingy.placeGear();
		else if(operator.isReleased(4)) GearThingy.resetPlaceGear();
		
		if(operator.isDown(6)) GearThingy.setDown();//dumpPnumatics();
		if(operator.isReleased(6)) GearThingy.setUp();
		
		//Gear Thingy Stuff. (Hopefully this changes soon with our new floor pickup mechanism)
		if(operator.isPressed(12)) GearThingy.setDown();
		else if(operator.isReleased(12)) GearThingy.setUp();
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