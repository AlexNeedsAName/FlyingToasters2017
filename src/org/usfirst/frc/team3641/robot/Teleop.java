package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Teleop
{
	private static Teleop instance;
	private static PS4 dualshock;
	private static Extreme3DPro operator;
	private static boolean pressedLastLoop = false;

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
		dualshock.readValues();
		
		//Change Drive Direction
		if(dualshock.getDPadUp()) DriveBase.setDriveMode(Constants.NORMAL_MODE);
		else if(dualshock.getDPadDown()) DriveBase.setDriveMode(Constants.REVERSE_MODE);
		
		//Drive Robot
		if(Constants.runningAleksBot) DriveBase.driveArcade(operator.getYAxis(), operator.getZAxis());
		else DriveBase.driveArcade(dualshock.getLeftY(), dualshock.getRightX());
		
		//Gearbox Things
		if(dualshock.getPlayStationButton() && !pressedLastLoop)
		{
			Gearbox.togglePTO();
			pressedLastLoop = true;
		}
		else if(!dualshock.getPlayStationButton()) pressedLastLoop = false;
		
		if(dualshock.getOptionsButton()) Gearbox.shiftLow();
		else if(dualshock.getOptionsButton()) Gearbox.shiftHigh();
		
		//Intake Stuff
		if(dualshock.getLeftBumper()) Intake.intakeDown();
		else if (dualshock.getRightBumper()) Intake.intakeUp();
		Intake.set(dualshock.getRightTrigger());

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
