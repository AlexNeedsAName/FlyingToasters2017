package org.usfirst.frc.team3641.robot;

public class Teleop
{
	private static Teleop instance;
	public static PS4 driver;
	public static E3D operator;
	public static Harmonix guitar;

	public static Teleop getInstance()
	{
		if(instance == null) instance = new Teleop();
		return instance;
	}

	/**
	 * Initalizes Teleop and its controllers.
	 */
	private Teleop()
	{
		driver = new PS4(Constants.Controllers.DRIVER);
		operator = new E3D(Constants.Controllers.OPERATOR);
		guitar = new Harmonix(Constants.Controllers.GUITAR);
	}

	/**
	 * Runs the main teleop code. Should be run from a loop (teleopPerodic)
	 */
	public static void run()
	{
		driver.poll();
		operator.poll();
		
		if(driver.isDown(PS4.Button.TOUCHPAD_BUTTON)) Sensors.resetDriveDistance();
		
		if(driver.isPressed(PS4.Button.TOUCHPAD_BUTTON)) Horn.setHorn(true);
		else if(driver.isReleased(PS4.Button.TOUCHPAD_BUTTON)) Horn.setHorn(false);
		
		//Change Drive Direction
		if(driver.isPressed(PS4.Button.DPAD_LEFT)) DriveBase.setDriveMode(DriveBase.DriveMode.NORMAL);
		else if(driver.isPressed(PS4.Button.DPAD_RIGHT)) DriveBase.setDriveMode(DriveBase.DriveMode.REVERSE);
		
		//if(driver.isPressed(PS4.Button.OPTIONS)) DriveBase.toggleSquaredRotation();
		//if(driver.isPressed(PS4.Button.SHARE)) DriveBase.toggleSquaredPower();
				
		if(driver.isDown(PS4.Button.CIRCLE))
		{
			Tracking.target(Tracking.Mode.GEAR_MODE);
		}
		else
		{
			//Drive Robot
			if(Constants.runningAleksBot) DriveBase.driveArcade(operator.getAxis(E3D.Axis.Y), operator.getAxis(E3D.Axis.Z));
			else DriveBase.driveArcade(driver.getAxis(PS4.Axis.LEFT_Y), driver.getAxis(PS4.Axis.RIGHT_X));
		}
		
		//Gearbox Things
		if(driver.isPressed(PS4.Button.PLAYSTATION_BUTTON)) Gearbox.togglePTO();
		
		if(driver.isPressed(PS4.Button.SHARE)) Gearbox.shift(Gearbox.Gear.LOW);
		else if(driver.isPressed(PS4.Button.OPTIONS)) Gearbox.shift(Gearbox.Gear.HIGH);
		
		//Intake Stuff
		if(driver.isPressed(PS4.Button.LEFT_BUMPER)) Intake.intakeDown();
		else if (driver.isPressed(PS4.Button.RIGHT_BUMPER)) Intake.intakeUp();
		Intake.set(driver.getAxis(PS4.Axis.RIGHT_TRIGGER));

		//Shooter Stuff
		if(!operator.isDown(E3D.Button.THUMB)) //Autonomous Subsystem Mode
		{
			if(operator.isReleased(E3D.Button.THUMB)) Turret.set(0);
			if(operator.isDown(E3D.Button.TRIGGER)) Tracking.target(Tracking.Mode.FUEL_MODE);  
		}
		
		else //Manual Mode
		{
			Turret.set(operator.getAxis(E3D.Axis.Z)/2);
			
			if(operator.isDown(E3D.Button.TRIGGER)) Shooter.forceFire();
			else if(operator.isReleased(E3D.Button.TRIGGER)) Shooter.stopFiring();
		}
		
		if(operator.isReleased(E3D.Button.TRIGGER) || driver.isReleased(PS4.Button.CIRCLE)) Tracking.resetState();
	}

	/**
	 * Runs the teleop code for driving with the Guitar as a controller.
	 */
	public static void runGuitar()
	{
		guitar.poll();
		DriveBase.driveArcade(guitar.getAxis(Harmonix.Axis.WHAMMY_BAR) * guitar.getAxis(Harmonix.Axis.STRUM), guitar.getAxis(Harmonix.Axis.BUTTONS));
	}
}