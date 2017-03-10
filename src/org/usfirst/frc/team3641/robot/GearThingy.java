package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Solenoid;

public class GearThingy
{
	private static GearThingy instance;
	private static Solenoid actuator;
	
	public static GearThingy getInstance()
	{
		if(instance == null) instance = new GearThingy();
		return instance;
	}
	
	private GearThingy()
	{
		actuator = new Solenoid(Constants.Pnumatics.GEAR_THINGY);
	}
	
	public static void extend()
	{
		actuator.set(true);
	}
	
	public static void retract()
	{
		actuator.set(false);
	}

}
