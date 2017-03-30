package org.usfirst.frc.team3641.robot;

import com.ctre.CANTalon;

public class Hopper
{
	private static Hopper instance;
	private static CANTalon centerAgitator;
	private static boolean alreadyAgitating = false;
		
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
		centerAgitator = new CANTalon(Constants.CAN.Talons.CENTER_AGITATOR);
	}

	/**
	 * Start agitating the hopper so we can feed the shooter.
	 */
	public static void Agitate()
	{
		if(!alreadyAgitating)
		{
			Console.print("Now agitating hopper", Constants.Verbosity.Level.MID);
			alreadyAgitating = true;
		}
		centerAgitator.set(Constants.Hopper.CENTER_AGITATOR_SPEED);
	}
	
	/**
	 * Runs the hopper if the shooter is at the correct speed.
	 */
	public static void autoAgitate()
	{
		if(Shooter.atTarget()) Agitate();
		else stopAgitating();
	}
	
	public static void runReverse()
	{
		centerAgitator.set(-Constants.Hopper.CENTER_AGITATOR_SPEED);
	}
	
	/**
	 * Stop agitating the hopper.
	 */
	public static void stopAgitating()
	{
		if(alreadyAgitating)
		{
			Console.print("No longer agitating hopper", Constants.Verbosity.Level.MID);
			alreadyAgitating = false;
		}
		centerAgitator.set(0);
	}
	
	public static boolean isAgitating()
	{
		return alreadyAgitating;
	}
}
