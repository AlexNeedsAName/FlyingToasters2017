package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Teleop
{
	private static Teleop instance;
	private static PS4 dualshock;
	private static Extreme3DPro operator;

	public static Teleop getInstance()
	{
		if(instance == null) instance = new Teleop();
		return instance;
	}

	private Teleop()
	{
		dualshock = new PS4(Constants.PS4_PORT);
		operator = new Extreme3DPro(Constants.OPERATOR_PORT);
	}

	public static void run()
	{
		dualshock.poll();
		
		//Change Drive Direction
		if(dualshock.isPressed(PS4.Button.DPAD_LEFT)) DriveBase.setDriveMode(Constants.NORMAL_MODE);
		else if(dualshock.isPressed(PS4.Button.DPAD_RIGHT)) DriveBase.setDriveMode(Constants.REVERSE_MODE);
		
		//Drive Robot
		if(Constants.runningAleksBot) DriveBase.driveArcade(operator.getYAxis(), operator.getZAxis());
		else DriveBase.driveArcade(dualshock.getAxis(PS4.Axis.LEFT_Y), dualshock.getAxis(PS4.Axis.RIGHT_X));
		
		//Gearbox Things
		if(dualshock.isPressed(PS4.Button.PLAYSTATION_BUTTON)) Gearbox.togglePTO();
		
		if(dualshock.isPressed(PS4.Button.SHARE)) Gearbox.shiftLow();
		else if(dualshock.isPressed(PS4.Button.OPTIONS)) Gearbox.shiftHigh();
		
		//Intake Stuff
		if(dualshock.isPressed(PS4.Button.LEFT_BUMPER)) Intake.intakeDown();
		else if (dualshock.isPressed(PS4.Button.RIGHT_BUMPER)) Intake.intakeUp();
		Intake.set(dualshock.getAxis(PS4.Axis.RIGHT_TRIGGER));

		//Shooter Stuff
		if(!operator.getButton(2)) //Autonomous Subsystem Mode
		{
			if(operator.getTrigger()) Tracking.target(Constants.FUEL_MODE);
			else Tracking.resetState();
			SmartDashboard.putNumber("Vision State", Tracking.getState());
		}
		else //Manual Mode
		{
			Turret.set(operator.getTwist()/2);
			if(operator.getTrigger()) Shooter.forceFire();
			else Shooter.stopFiring();
		}
	}
}
