package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class PDP
{
	private static PDP instance;
	private static PowerDistributionPanel pdp;

	public static PDP getInstance()
	{
		if(instance == null) instance = new PDP();
		return instance;
	}

	/**
	 * Create the pdp object
	 */
	private PDP()
	{
		pdp = new PowerDistributionPanel();
	}

	/**
	 * Gets the voltage of the battery.
	 * 
	 * @return Returns voltage of the battery.
	 */
	public static double getVoltage()
	{
		return pdp.getVoltage();
	}
	
	/**
	 * Gets the total current draw.
	 * 
	 * @return Returns the total current draw.
	 */
	public static double getCurrent()
	{
		return pdp.getTotalCurrent();
	}

}
