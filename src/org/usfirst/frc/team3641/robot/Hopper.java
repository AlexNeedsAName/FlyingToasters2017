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
		centerAgitator = new Spark(Constants.PWM.Sparks.CENTER_AGITATOR);
		leftAgitator = new Spark(Constants.PWM.Sparks.LEFT_AGITATOR);
		rightAgitator = new Spark(Constants.PWM.Sparks.RIGHT_AGITATOR);
	}

	/**
	 * Start agitating the hopper so we can feed the shooter.
	 */
	public static void adjatate()
	{
		if(!alreadyAdjatating)
		{
			Console.print("Now adjetating hopper", Constants.Verbosity.Level.MID);
			alreadyAdjatating = true;
		}
		Intake.setSpeed(1);
		centerAgitator.set(Constants.Hopper.CENTER_AGITATOR_SPEED);
		leftAgitator.set(Constants.Hopper.LEFT_AGITATOR_SPEED);
		rightAgitator.set(Constants.Hopper.RIGHT_AGITATOR_SPEED);
	}
	
	/**
	 * Runs the hopper if the shooter is at the correct speed.
	 */
	public static void autoAdjatate()
	{
		if(Shooter.atTarget()) adjatate();
		else stopAdjatating();
	}
	
	public static void runReverse()
	{
		centerAgitator.set(-Constants.Hopper.CENTER_AGITATOR_SPEED);
		leftAgitator.set(-Constants.Hopper.LEFT_AGITATOR_SPEED);
		rightAgitator.set(-Constants.Hopper.RIGHT_AGITATOR_SPEED);
	}
	
	/**
	 * Stop agitating the hopper.
	 */
	public static void stopAdjatating()
	{
		if(alreadyAdjatating)
		{
			Console.print("No longer adjetating hopper", Constants.Verbosity.Level.MID);
			alreadyAdjatating = false;
		}
		Intake.setSpeed(0);
		centerAgitator.set(0);
		leftAgitator.set(0);
		rightAgitator.set(0);
	}
}
