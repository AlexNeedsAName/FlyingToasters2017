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

	private PDP()
	{
		pdp = new PowerDistributionPanel();
	}

	public static double getVoltage()
	{
		return pdp.getVoltage();
	}

}
