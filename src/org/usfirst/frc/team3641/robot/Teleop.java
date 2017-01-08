package org.usfirst.frc.team3641.robot;

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
		DriveBase.readGyro();
		
		//Put any functions that should block normal drive base input here:
		if(dualshock.getCircleButton()) Tracking.target(Constants.GEAR_MODE);
		else if(dualshock.getTriangleButton()) Tracking.target(Constants.FUEL_MODE);
		else
		{
			Tracking.resetState();
			DriveBase.driveArcade(dualshock.getLeftY(), dualshock.getRightX());
		}
		
		//Put any functions that should not interfere with the drive base here:
	}
	
}
