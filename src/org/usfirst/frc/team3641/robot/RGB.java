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
	 * Initialize the spike to control the RGB strip
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
		if(color == Color.RED) spike.set(Relay.Value.kForward);
		else if(color == Color.BLUE) spike.set(Relay.Value.kReverse);
		else spike.set(Relay.Value.kOn);
	}
}
