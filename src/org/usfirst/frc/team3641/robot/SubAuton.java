package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SubAuton
{
	public static SubAuton instance;
	
	public static boolean alreadyDriving;
	public static double initialDistance, initialDifference;
	
	public static boolean alreadyRotating;
	public static double initialRotationAngle;
	
	public static boolean latched = false;
	
	public static SubAuton getInstance()
	{
		if(instance == null) instance = new SubAuton();
		return instance;
	}
	
	private SubAuton()
	{
		alreadyDriving = false;
	}
	
	public static double driveBy(double distance)
	{
		if(!alreadyDriving)
		{
			initialDistance = Sensors.getLeftDriveDistance();
			initialDifference = Sensors.getLeftDriveDistance() - Sensors.getRightDriveDistance();
			alreadyDriving = true;
		}
		double error = DriveBase.driveStraightTo(initialDistance + distance, initialDifference);
		return error;
	}
	
	public static void resetDriveBy()
	{
		alreadyDriving = false;
		DriveBase.resetPID();
	}
	
	public static double rotateBy(double angle)
	{
		if(!alreadyRotating)
		{
			initialRotationAngle = Sensors.getAngle();
			alreadyRotating = true;
		}
		double target = initialRotationAngle + angle;
		SmartDashboard.putNumber("Angle Target", target);
		double error = DriveBase.turnTo(target);
		return error;
	}
	
	public static void resetRotateBy()
	{
		alreadyRotating = false;
		DriveBase.driveArcade(0, 0);
		DriveBase.resetPID();
	}
	
	public static double ultrasonicLineup(double target)
	{
		double error = driveBy(target-Sensors.getUltrasonicDistance());
		return error;
	}
	
	public static void resetUltrasonicLineup()
	{
		resetDriveBy();
	}
		
	public static void placeGear() 
	{
		Serial.sendData("3");
		
	}
	
	public static void resetLatch()
	{
		SmartDashboard.putBoolean("Latched", false);
		latched = false;
	}

}
