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
		dualshock.readValues();
		
		if(dualshock.getLeftBumper()) DriveBase.setDriveMode(Constants.REVERSE_MODE);
		else if (dualshock.getRightBumper()) DriveBase.setDriveMode(Constants.NORMAL_MODE);

		//Put any functions that should block normal drive base input in front of this:
		if(Constants.runningAleksBot) DriveBase.driveArcade(operator.getYAxis(), operator.getZAxis());
		else DriveBase.driveArcade(dualshock.getLeftY(), dualshock.getRightX());
		
	
		//Put any functions that should not interfere with the drive base here:
		
		//Manual Mode
		if(operator.getButton(2)) 
		{
			Turret.set(operator.getTwist());
			//Shooter.setRPM(1750);
			if(operator.getTrigger()) Shooter.forceFire();
			else Shooter.stopFiring();
		}		
		else
		{
			if(operator.getTrigger()) Tracking.target(Constants.FUEL_MODE);
			else Tracking.resetState();
			SmartDashboard.putNumber("Vision State", Tracking.getState());
		}
	}
}
