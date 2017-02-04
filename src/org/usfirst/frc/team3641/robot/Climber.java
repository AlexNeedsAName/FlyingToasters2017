package org.usfirst.frc.team3641.robot;
import com.ctre.CANTalon;

public class Climber
{
	private static Climber instance;
	
	public static Climber getInstance()
	{
		if(instance == null) instance = new Climber();
		return instance;
	}
	
	private Climber()
	{
		
	}
}
