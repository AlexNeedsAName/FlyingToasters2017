package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Relay;

public class RGB
{
	private Relay spike;
	private DriverStation DS = DriverStation.getInstance();
	
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
		spike.setSafetyEnabled(false);
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
	
	public void setAllianceColor()
	{
		DriverStation.Alliance alliance = DS.getAlliance();
		switch(alliance)
		{
			case Red:
				setColor(Color.RED);
				break;
			case Blue:
				setColor(Color.BLUE);
				break;
			default:
				setColor(Color.OFF);
				break;
		}
	}
	
	public void setInverseAllianceColor()
	{
		DriverStation.Alliance alliance = DS.getAlliance();
		switch(alliance)
		{
			case Red:
				setColor(Color.BLUE);
				break;
			case Blue:
				setColor(Color.RED);
				break;
			default:
				setColor(Color.OFF);
				break;
		}
	}

}
