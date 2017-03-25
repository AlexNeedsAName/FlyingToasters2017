package org.usfirst.frc.team3641.robot;

public class SubAuton
{
	public static SubAuton instance;
	
	public static boolean alreadyDriving;
	public static double initialDistance, initialAngle;
	
	public static boolean alreadyRotating;
	public static double initialRotationAngle;
	
	public static SubAuton getInstance()
	{
		if(instance == null) instance = new SubAuton();
		return instance;
	}
	
	private SubAuton()
	{
		alreadyDriving = false;
	}
	
	public static boolean driveBy(double distance, double threshold)
	{
		double error = driveBy(distance);
		return (error <= threshold);
	}
	
	public static double driveBy(double distance)
	{
		if(!alreadyDriving)
		{
			initialDistance = Sensors.getLeftDriveDistance();
			initialAngle = Sensors.getAngle();
			alreadyDriving = true;
		}
		double error = DriveBase.driveStraightTo(initialDistance + distance, initialAngle);
		return error;
	}
	
	public static void resetDriveBy()
	{
		alreadyDriving = false;
		DriveBase.resetPID();
	}
	
	public static boolean rotateBy(double angle, double threshold)
	{
		double error = rotateBy(angle);
		return (error <= threshold);
	}
	
	public static double rotateBy(double angle)
	{
		if(!alreadyRotating)
		{
			initialRotationAngle = Sensors.getAngle();
		}
		double target = initialRotationAngle + angle;
		double error = DriveBase.turnTo(target);
		return error;
	}
		
	public static void resetRotateBy()
	{
		alreadyRotating = false;
		DriveBase.resetPID();
	}

}
