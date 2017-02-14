package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Teleop
{
	private static Teleop instance;
	public static PS4 driver;
	public static E3D operator;

	public static Teleop getInstance()
	{
		if(instance == null) instance = new Teleop();
		return instance;
	}

	private Teleop()
	{
		driver = new PS4(Constants.PS4_PORT);
		operator = new E3D(Constants.OPERATOR_PORT);
	}

	public static void run()
	{
		driver.poll();
		operator.poll();
				
		//Change Drive Direction
		if(driver.isPressed(PS4.Button.DPAD_LEFT)) DriveBase.setDriveMode(Constants.NORMAL_MODE);
		else if(driver.isPressed(PS4.Button.DPAD_RIGHT)) DriveBase.setDriveMode(Constants.REVERSE_MODE);
		
		//Drive Robot
		if(Constants.runningAleksBot) DriveBase.driveArcade(operator.getAxis(E3D.Axis.Y), operator.getAxis(E3D.Axis.Z));
		else DriveBase.driveArcade(driver.getAxis(PS4.Axis.LEFT_Y), driver.getAxis(PS4.Axis.RIGHT_X));
		
		//Gearbox Things
		if(driver.isPressed(PS4.Button.PLAYSTATION_BUTTON)) Gearbox.togglePTO();
		
		if(driver.isPressed(PS4.Button.SHARE)) Gearbox.shiftLow();
		else if(driver.isPressed(PS4.Button.OPTIONS)) Gearbox.shiftHigh();
		
		//Intake Stuff
		if(driver.isPressed(PS4.Button.LEFT_BUMPER)) Intake.intakeDown();
		else if (driver.isPressed(PS4.Button.RIGHT_BUMPER)) Intake.intakeUp();
		Intake.set(driver.getAxis(PS4.Axis.RIGHT_TRIGGER));

		//Shooter Stuff
		if(!operator.isDown(E3D.Button.THUMB)) //Autonomous Subsystem Mode
		{
			if(operator.isDown(E3D.Button.TRIGGER)) Tracking.target(Constants.FUEL_MODE);
			else if(operator.isReleased(E3D.Button.TRIGGER)) Tracking.resetState();
			
			SmartDashboard.putNumber("Vision State", Tracking.getState());
		}
		else //Manual Mode
		{
			Turret.set(operator.getAxis(E3D.Axis.Z)/2);
			
			if(operator.isDown(E3D.Button.TRIGGER)) Shooter.forceFire();
			else if(operator.isReleased(E3D.Button.TRIGGER)) Shooter.stopFiring();
		}
	}
}