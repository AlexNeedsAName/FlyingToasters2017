package org.usfirst.frc.team3641.robot;

public class Coords
{
	/**
	 * Converts rectangular coordinates to their polar angle.
	 * 
	 * @param x The rectangular x coordinate.
	 * @param y The rectangular y coordinate.
	 * @return The polar angle of the point
	 */
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
	
	/**
	 * Converts rectangular coordinates to their polar radius.
	 * 
	 * @param x The rectangular x coordinate.
	 * @param y The rectangular y coordinate.
	 * @return The polar radius of the point
	 */
	public static double rectToPolarRadius(double x, double y)
	{
		return Math.sqrt(x*x + y*y);
	}
	
	/**
	 * Converts polar coordinates to their rectangular x.
	 * 
	 * @param radius The polar radius.
	 * @param angle The polar angle.
	 * @return The rectangular x coordinate.
	 */
	public static double polarToRectX(double radius, double angle)
	{
		return radius * Math.cos(angle);
	}
	
	/**
	 * Converts polar coordinates to their rectangular y.
	 * 
	 * @param radius The polar radius.
	 * @param angle The polar angle.
	 * @return The rectangular y coordinate.
	 */
	public static double polarToRectY(double radius, double angle)
	{
		return radius * Math.sin(angle);
	}
	
	/**
	 * Takes an angle in degrees and finds a coterminal angle where 0<=θ<360.
	 * 
	 * @param degrees
	 * @return
	 */
	public static double fixDegrees(double degrees)
	{
		while(degrees >= 360) degrees -= 360;
		while(degrees < 0) degrees += 360;
		return degrees;
	}
	
	/**
	 * Finds the shortest distance between two angles.
	 * 
	 * @param targetAngle Your target value.
	 * @param currentAngle Your current value.
	 * @return The shortest way to get from the current to the target.
	 */
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
