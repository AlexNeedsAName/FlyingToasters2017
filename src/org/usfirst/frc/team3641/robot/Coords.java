package org.usfirst.frc.team3641.robot;

public class Coords
{
	public static double rectToPolarAngle(double x, double y)
	{
		if(x == 0 && y == 0) return 0;
		if(x >  0 && y == 0) return 0;
		if(x == 0 && y >  0) return 90;
		if(x <  0 && y == 0) return 180;
		if(x == 0 && y <  0) return 270;
		
		double angle = Math.toDegrees(Math.atan(y/x));
		
		if(x < 0) angle += 180;
		else if (y < 0) angle += 360;
		
		return angle;
	}
	
	public static double rectToPolarRadius(double x, double y)
	{
		return Math.sqrt(x*x + y*y);
	}
	
	public static double polarToRectX(double radius, double angle)
	{
		return radius * Math.cos(angle);
	}
	
	public static double polarToRectY(double radius, double angle)
	{
		return radius * Math.sin(angle);
	}
	
	public static double fixDegrees(double degrees)
	{
		while(degrees >= 360) degrees -= 360;
		while(degrees < 0) degrees += 360;
		return degrees;
	}
	
	public static double calcAngleError(double targetAngle, double currentAngle)
	{
		double counterClockwiseDistance, clockwiseDistance;

		if(targetAngle == currentAngle) return 0;
		else
		{
			counterClockwiseDistance = fixDegrees(targetAngle - currentAngle);
			clockwiseDistance = fixDegrees(360 - (targetAngle - currentAngle));

			if(Math.abs(counterClockwiseDistance) < Math.abs(clockwiseDistance)) return counterClockwiseDistance;
			else return -clockwiseDistance;
		}
	}
}
