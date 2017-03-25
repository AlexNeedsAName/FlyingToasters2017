
package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Relay;

public class Horn
{
	private static Horn instance;
	private static Relay hornSpike;
	
	public static Horn getInstance()
	{
		if(instance == null) instance = new Horn();
		return instance;
	}
	
	private Horn()
	{
		hornSpike = new Relay(0);
	}
	
	public static void setHorn(boolean on)
	{
		hornSpike.set((on) ? Relay.Value.kOn : Relay.Value.kOff);
	}

}
