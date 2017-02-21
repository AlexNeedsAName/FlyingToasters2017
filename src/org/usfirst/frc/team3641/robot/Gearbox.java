package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Gearbox
{
	private static Gearbox instance;
	private static DoubleSolenoid shifter, PTO;
	private static Gear currentGear;
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

	/**
	 * Initalize the Gearbox.
	 */
	private Gearbox()
	{
		if(!Constants.runningAleksBot)
		{
			shifter = new DoubleSolenoid(Constants.Pnumatics.SHIFTER_FORWARD, Constants.Pnumatics.SHIFTER_REVERSE);
			PTO = new DoubleSolenoid(Constants.Pnumatics.PTO_FORWARD, Constants.Pnumatics.PTO_REVERSE);
		}
	}

	/**
	 * Shift gears
	 * 
	 * @param gear The gear you want to shift to.
	 */
	public static void shift(Gear gear)
	{
		if(gear != currentGear)
		{
			if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.LOW)) System.out.println("Shifting to " + gear.toString() + " gear.");
			if(!Constants.runningAleksBot) shifter.set((gear == Gear.LOW) ? (DoubleSolenoid.Value.kForward) : (DoubleSolenoid.Value.kReverse));
			currentGear = gear;
		}
	}
	
	/**
	 * Toggles the PTO.
	 */
	public static void togglePTO()
	{
		if(!Constants.runningAleksBot) setPTO(!inPTOMode);
		DriveBase.driveArcade(inPTOMode ? 1 : 0, 0);
	}
	
	/**
	 * Sets the PTO to a specified state.
	 * 
	 * @param on True for on, false for off.
	 */
	public static void setPTO(boolean on)
	{
		inPTOMode = on;
		if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.LOW)) System.out.println(((inPTOMode) ? "Engaging" : "Disengaging") + " PTO");
		PTO.set(inPTOMode ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
	}

}
