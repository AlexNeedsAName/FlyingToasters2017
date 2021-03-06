package org.usfirst.frc.team3641.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Hopper
{
	private static Hopper instance;
	private static TalonSRX centerAgitator;
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
		centerAgitator = new TalonSRX(Constants.CAN.Talons.CENTER_AGITATOR);
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
		centerAgitator.set(ControlMode.PercentOutput, Constants.Hopper.CENTER_AGITATOR_SPEED);
	}
		
	public static void runReverse()
	{
		centerAgitator.set(ControlMode.PercentOutput, -Constants.Hopper.CENTER_AGITATOR_SPEED);
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
		centerAgitator.set(ControlMode.PercentOutput, 0);
	}
	
	public static boolean isAgitating()
	{
		return alreadyAgitating;
	}
}
