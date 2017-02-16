package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Relay;
public class RGB
{
	private static RGB instance;
	private static Relay spike;
	
	public static int OFF = 0, RED = 1, BLUE = 2;
	
	public static RGB getInstance()
	{
		if(instance == null) instance = new RGB();
		return instance;
	}

	private RGB()
	{
		spike = new Relay(Constants.RGB_SPIKE);
	}
	
	public static void setColor(int color)
	{
		if(color == RED) spike.setDirection(Relay.Direction.kForward);
		else if(color == BLUE) spike.setDirection(Relay.Direction.kReverse);
		else spike.set(Relay.Value.kOff);
		
	}
}
