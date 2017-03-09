package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.Spark;

public class Hopper
{
	private static Hopper instance;
	private static Spark centerAgitator, leftAgitator, rightAgitator;
	private static boolean alreadyAdjatating = false;
		
	public static Hopper getInstance()
	{
		if(instance == null) instance = new Hopper();
		return instance;
	}
	
	/**
	 * Initialize the Hopper (just the spark for the agitator right now)
	 */
	private Hopper()
	{
		centerAgitator = new Spark(Constants.PWM.Sparks.CENTER_ADJATATOR);
		leftAgitator = new Spark(Constants.PWM.Sparks.LEFT_ADJATATOR);
		rightAgitator = new Spark(Constants.PWM.Sparks.RIGHT_ADJATATOR);
	}

	/**
	 * Start agitating the hopper so we can feed the shooter.
	 */
	public static void adjatate()
	{
		if(!alreadyAdjatating)
		{
			Console.print("Now adjetating hopper", Constants.Verbosity.Level.LOW);
			alreadyAdjatating = true;
		}
		centerAgitator.set(Constants.Hopper.CENTER_AGITATOR_SPEED);
		leftAgitator.set(Constants.Hopper.LEFT_AGITATOR_SPEED);
		rightAgitator.set(Constants.Hopper.RIGHT_AGITATOR_SPEED);
	}
	
	public static void runReverse()
	{
		centerAgitator.set(-Constants.Hopper.CENTER_AGITATOR_SPEED);
		leftAgitator.set(-Constants.Hopper.LEFT_AGITATOR_SPEED);
		rightAgitator.set(-Constants.Hopper.RIGHT_AGITATOR_SPEED);
	}
	
	/**
	 * Stop adjatating the hopper.
	 */
	public static void stopAdjatating()
	{
		if(alreadyAdjatating)
		{
			Console.print("No longer adjetating hopper", Constants.Verbosity.Level.LOW);
			alreadyAdjatating = false;
		}
		centerAgitator.set(0);
		leftAgitator.set(0);
		rightAgitator.set(0);
	}
}
