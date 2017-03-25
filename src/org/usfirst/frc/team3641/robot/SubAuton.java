package org.usfirst.frc.team3641.robot;

public class SubAuton
{
	public static SubAuton instance;
	
	public static boolean alreadyDriving;
	public static double initialDistance, initialDifference;
	
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
	
	public static void driveBy(double distance)
	{
		if(!alreadyDriving)
		{
			initialDistance = Sensors.getLeftDriveDistance();
			initialDifference = Sensors.getLeftDriveDistance() - Sensors.getRightDriveDistance();
			alreadyDriving = true;
		}
		DriveBase.driveStraightTo(initialDistance + distance, initialDifference);
	}
	
	public static void resetDriveBy()
	{
		alreadyDriving = false;
		DriveBase.resetPID();
	}
	
	public static void rotateBy(double angle, double threshold)
	{
		if(!alreadyRotating)
		{
			initialRotationAngle = Sensors.getAngle();
		}
		double target = initialRotationAngle + angle;
		DriveBase.turnTo(target, threshold);
	}
	
	public static void rotateBy(double angle)
	{
		rotateBy(angle, Constants.Thresholds.ANGLE_THRESHOLD);
	}
	
	public static void resetRotateBy()
	{
		alreadyRotating = false;
		DriveBase.resetPID();
	}

}
