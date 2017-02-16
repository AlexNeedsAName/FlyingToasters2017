package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Relay;
public class RGB
{
	private static RGB instance;
	private static Relay red, blue; //We don't really need green.
	
	public static int OFF = 0, RED = 1, BLUE = 2;
	
	public static RGB getInstance()
	{
		if(instance == null) instance = new RGB();
		return instance;
	}

	private RGB()
	{
		red = new Relay(Constants.RED_SPIKE);
		blue = new Relay(Constants.BLUE_SPIKE);		
	}
	
	public static void setColor(int color)
	{
		if(color == RED)
		{
			red.set(Relay.Value.kOn);
			blue.set(Relay.Value.kOff);
		}
		else if(color == BLUE)
		{
			red.set(Relay.Value.kOff);
			blue.set(Relay.Value.kOn);
		}
		else
		{
			red.set(Relay.Value.kOff);
			blue.set(Relay.Value.kOff);
		}
		
	}
}
