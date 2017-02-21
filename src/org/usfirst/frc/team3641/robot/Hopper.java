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
	
	/**
	 * Initalize the Hopper (just the spark for the adjatator right now)
	 */
	private Hopper()
	{
		adjatator = new Spark(Constants.PWM.Sparks.HOPPER_ADJATATOR);
	}

	/**
	 * Start adjatating the hopper so we can feed the shooter.
	 */
	public static void adjatate()
	{
		if(!alreadyAdjatating)
		{
			if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.MID)) System.out.println("Now adjetating hopper");
			alreadyAdjatating = true;
		}
		adjatator.set(Constants.ADJATATOR_SPEED);
	}
	
	/**
	 * Stop adjatating the hopper.
	 */
	public static void stopAdjatating()
	{
		if(alreadyAdjatating)
		{
			if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.MID)) System.out.println("No longer adjetating hopper");
			alreadyAdjatating = false;
		}
		adjatator.set(0);
	}
}
