package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

public class GearThingy
{
	private static GearThingy instance;
	private static Solenoid actuator;
	private static Timer shakeTimer;
	private static boolean alreadyShaking;
	
	public static GearThingy getInstance()
	{
		if(instance == null) instance = new GearThingy();
		return instance;
	}
	
	private GearThingy()
	{
		actuator = new Solenoid(Constants.Pnumatics.GEAR_THINGY);
		shakeTimer = new Timer();
		shakeTimer.reset();
		shakeTimer.start();
	}
	
	public static void extend()
	{
		Console.print("Extending Gear Thingy", Constants.Verbosity.Level.LOW);
		actuator.set(true);
	}
	
	public static void retract()
	{
		Console.print("Retracting Gear Thingy", Constants.Verbosity.Level.LOW);
		actuator.set(false);
	}
	
	public static void shake()
	{
		if(!alreadyShaking)
		{
			shakeTimer.reset();
			shakeTimer.start();
			alreadyShaking = true;
		}
		double time = shakeTimer.get();
		double rounded = Math.round(time);
		if(time - rounded <= .5) extend();
		else retract();
	}
	
	public static void resetShake()
	{
		retract();
		alreadyShaking = false;
	}

}
