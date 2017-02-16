package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Relay;
public class RGB
{
	private Relay spike;
	
	public static int OFF = 0, RED = 1, BLUE = 2;
	
	public RGB(int port)
	{
		spike = new Relay(port);
	}
	
	public void setColor(int color)
	{
		if(color == RED) spike.setDirection(Relay.Direction.kForward);
		else if(color == BLUE) spike.setDirection(Relay.Direction.kReverse);
		else spike.set(Relay.Value.kOff);
		
	}
}
