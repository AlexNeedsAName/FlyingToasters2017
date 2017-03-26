package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;

public class GearThingy
{
	private static GearThingy instance;
	private static Solenoid actuator;
	private static Spark wheelSpark;
	
	private static boolean alreadyPlacingGear = false, alreadyPickingUp = false;
	private static Timer placerTimer;
	
	public static GearThingy getInstance()
	{
		if(instance == null) instance = new GearThingy();
		return instance;
	}
	
	private GearThingy()
	{
		actuator = new Solenoid(Constants.Pnumatics.GEAR_THINGY);
		wheelSpark = new Spark(Constants.PWM.Sparks.GEAR_WHEELS);
		placerTimer = new Timer();
	}
	
	/**
	 * Extends the gear placer/intake with pneumatics.
	 */
	public static void setDown()
	{
		Console.print("Extending Gear Thingy", Constants.Verbosity.Level.LOW);
		actuator.set(true);
	}
	
	/**
	 * Retracts the gear placer/intake with pneumatics.
	 */
	public static void setUp()
	{
		Console.print("Retracting Gear Thingy", Constants.Verbosity.Level.LOW);
		actuator.set(false);
	}
	
	public static void intake()
	{
		wheelSpark.set(1);
	}
	
	public static void stopWheels()
	{
		wheelSpark.set(0);
	}
	
	public static void eject()
	{
		wheelSpark.set(-1);
	}
	
	public static void placeGear()
	{
		if(!alreadyPlacingGear)
		{
			placerTimer.reset();
			placerTimer.start();
			setDown();
			alreadyPlacingGear = true;
		}
		if(placerTimer.get() >= 0.5) eject();
	}
	
	public static void resetPlaceGear()
	{
		alreadyPlacingGear = false;
		setUp();
		stopWheels();
	}
	
	public static void pickupGear()
	{
		if(!alreadyPickingUp)
		{
			setDown();
			alreadyPickingUp = true;
		}
		intake();
	}
	
	public static void resetPickupGear()
	{
		alreadyPickingUp = false;
		setUp();
		stopWheels();
	}

}
