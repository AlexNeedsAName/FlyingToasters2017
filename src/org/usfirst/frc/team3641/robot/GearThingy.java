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
	
	/**
	 * Extends the gear placer/intake with pneumatics.
	 */
	public static void extend()
	{
		Console.print("Extending Gear Thingy", Constants.Verbosity.Level.LOW);
		actuator.set(true);
	}
	
	/**
	 * Retracts the gear placer/intake with pneumatics.
	 */
	public static void retract()
	{
		Console.print("Retracting Gear Thingy", Constants.Verbosity.Level.LOW);
		actuator.set(false);
	}
	
	/**
	 * Shakes the gear thingy twice a second.
	 */
	public static void shake() //TODO: Make it actually work.
	{
		if(!alreadyShaking)
		{
			shakeTimer.reset();
			shakeTimer.start();
			alreadyShaking = true;
		}
		int time = (int) (2*shakeTimer.get()/Constants.Gear.SHAKE_RATE);
		
		if(time % 2 == 0) extend();
		else retract();
	}
	
	/**
	 * Stop shaking the gear thingy.
	 */
	public static void resetShake()
	{
		retract();
		alreadyShaking = false;
	}

}
