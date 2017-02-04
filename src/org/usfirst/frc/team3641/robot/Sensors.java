package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.AnalogInput;
public class Sensors
{
	private static Sensors instance;
	private static double ultrasonicDistance, shooterRPM;
	private static AnalogInput ultrasonic;
	
	private static Sensors getInstance()
	{
		if(instance == null) instance = new Sensors();
		return instance;
	}
	
	private Sensors()
	{
		ultrasonic = new AnalogInput(Constants.ULTRASONIC_PORT); 
	}
	
	public static void poll()
	{
		shooterRPM = Shooter.right.getEncVelocity() * Constants.ENCODER_RATE_MULTIPLIER;
		ultrasonicDistance = ultrasonic.getAverageVoltage() * Constants.VOLTAGE_TO_METERS;
	}
	
	public static double getDistance()
	{
		return ultrasonicDistance;
	}
	
	public static double getRPM()
	{
		return shooterRPM;
	}
}
