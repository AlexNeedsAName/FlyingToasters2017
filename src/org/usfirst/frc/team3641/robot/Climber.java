package org.usfirst.frc.team3641.robot;
import com.ctre.CANTalon;

public class Climber
{
	private static Climber instance;
	private static CANTalon left, right;
	
	public static Climber getInstance()
	{
		if(instance == null) instance = new Climber();
		return instance;
	}
	
	private Climber()
	{
		left = new CANTalon(Constants.CLIMBER_LEFT_TALON);
		right = new CANTalon(Constants.CLIMBER_RIGHT_TALON);
	}
}
