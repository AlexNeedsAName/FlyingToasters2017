package org.usfirst.frc.team3641.robot;

public class SubAuton
{
	public static SubAuton instance;
	
	public static boolean alreadyDriving;
	public static double initialLeft, initialRight;
	
	public static SubAuton getInstance()
	{
		if(instance == null) instance = new SubAuton();
		return instance;
	}
	
	private SubAuton()
	{
		alreadyDriving = false;
	}
	
	public static void driveBy(double distance)
	{
		if(!alreadyDriving)
		{
			initialLeft = Sensors.getLeftDriveDistance();
			initialRight = Sensors.getRightDriveDistance();
			alreadyDriving = true;
		}
		DriveBase.driveTankTo(initialLeft + distance, initialRight + distance);
	}
	
	public static void resetDriveBy()
	{
		alreadyDriving = false;
	}

}
