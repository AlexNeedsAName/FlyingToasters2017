package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Spark;

public class Climber
{
	private static Climber instance;
	private static Spark leftSpark, rightSpark;
	
	public static Climber getInstance()
	{
		if(instance == null) instance = new Climber();
		return instance;
	}
	
	private Climber()
	{
		leftSpark = new Spark(Constants.PWM.Sparks.CLIMBER_LEFT);
		rightSpark = new Spark(Constants.PWM.Sparks.CLIMBER_RIGHT);
	}
	
	public static void runClimber(double speed)
	{
		//if(speed < 0) speed = 0;
//		else if(PDP.getCurrent(Constants.PDP.INTAKE_LEFT) >= Constants.Climber.MAX_CURRENT_DRAW || PDP.getCurrent(Constants.PDP.INTAKE_RIGHT) >= Constants.Climber.MAX_CURRENT_DRAW) speed = 0;
		
		leftSpark.set(speed);
		rightSpark.set(-speed);
	}
	
}
