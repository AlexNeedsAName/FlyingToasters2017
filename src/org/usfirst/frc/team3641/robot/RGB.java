package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Relay;

public class RGB
{
	private Relay spike;
	
	public enum Color
	{
		OFF, RED, BLUE;
	}
	
	/**
	 * Initalize the spike to control the RGB strip
	 * 
	 * @param port
	 */
	public RGB(int port)
	{
		spike = new Relay(port);
	}
	
	/**
	 * Set the color to red or blue.
	 * 
	 * @param color The color you want to set it to
	 */
	public void setColor(Color color)
	{
		if(color == Color.RED) spike.setDirection(Relay.Direction.kForward);
		else if(color == Color.BLUE) spike.setDirection(Relay.Direction.kReverse);
		else spike.set(Relay.Value.kOff);
	}
}
