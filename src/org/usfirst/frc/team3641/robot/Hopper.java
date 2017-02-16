package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.Spark;

public class Hopper
{
	private static Hopper instance;
	private static Spark adjatator;
	private static boolean alreadyAdjatating = false;
		
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
		if(!alreadyAdjatating)
		{
			if(Constants.VERBOSE >= Constants.MID) System.out.println("Now adjetating hopper");
			alreadyAdjatating = true;
		}
		adjatator.set(Constants.ADJATATOR_SPEED);
	}
	public static void stopAdjatating()
	{
		if(alreadyAdjatating)
		{
			if(Constants.VERBOSE >= Constants.MID) System.out.println("No longer adjetating hopper");
			alreadyAdjatating = false;
		}
		adjatator.set(0);
	}
}
