package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.Spark;

public class Hopper
{
	private static Hopper instance;
	private static Spark adjatator;
		
	public static Hopper getInstance()
	{
		if(instance == null) instance = new Hopper();
		return instance;
	}
	
	private Hopper()
	{
		adjatator = new Spark(Constants.HOPPER_ADJATATOR_SPARK);
	}

	public static void adjatate()
	{
		adjatator.set(Constants.ADJATATOR_SPEED);
	}
	public static void stopAdjatating()
	{
		adjatator.set(0);
	}
}
