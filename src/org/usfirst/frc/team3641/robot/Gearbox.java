package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Gearbox
{
	private static Gearbox instance;
	private static DoubleSolenoid shifter, PTO;
	private static boolean inPTOMode = false;

	public static Gearbox getInstance()
	{
		if(instance == null) instance = new Gearbox();
		return instance;
	}

	/**
	 * Different states you can shift the gearbox to.
	 */
	public enum Gear
	{
		LOW, HIGH
	}

	private Gearbox()
	{
		if(!Constants.runningAleksBot)
		{
			shifter = new DoubleSolenoid(Constants.SHIFTER_CHANNEL_FORWARD, Constants.SHIFTER_CHANNEL_REVERSE);
			PTO = new DoubleSolenoid(Constants.PTO_CHANNEL_FORWARD, Constants.PTO_CHANNEL_REVERSE);
		}
	}

	public static void shift(Gear gear)
	{
		if(gear == Gear.LOW)
		{
			if(Constants.VERBOSE >= Constants.MID) System.out.println("Shifting Down");
			if(!Constants.runningAleksBot) shifter.set(DoubleSolenoid.Value.kForward);
		}
		else
		{
			if(Constants.VERBOSE >= Constants.MID) System.out.println("Shifting Down");
			if(!Constants.runningAleksBot) shifter.set(DoubleSolenoid.Value.kReverse);
		}
	}
	
	public static void togglePTO()
	{
		if(!Constants.runningAleksBot) setPTO(!inPTOMode);
		if(Constants.VERBOSE >= Constants.LOW) System.out.println(((inPTOMode) ? "Engaging" : "Disengaging") + " PTO");
	}
	
	public static void setPTO(boolean on)
	{
		inPTOMode = on;
		PTO.set(inPTOMode ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
	}

}
