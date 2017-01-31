package org.usfirst.frc.team3641.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;

public class Shooter
{
	private static Shooter instance;
	private static CANTalon left, right;
	private static PID pid;
	
	public static Shooter getInstance()
	{
		if(instance == null) instance = new Shooter();
		return instance;
	}
	
	public Shooter()
	{
		left = new CANTalon(Constants.SHOOTER_LEFT);
		right = new CANTalon(Constants.SHOOTER_RIGHT);
		right.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		right.configEncoderCodesPerRev(20);
		pid = new PID(Constants.SHOOTER_KP, Constants.SHOOTER_KI, Constants.SHOOTER_KD, Constants.SHOOTER_FF);
	}
	
	public static double getEncoderRate()
	{
		return right.getEncVelocity() * Constants.ENCODER_RATE_MULTIPLIER;
	}
	
	public static void setRPM(double target)
	{
		double current = Shooter.getEncoderRate();
		double error = target - current;
		double output = pid.pid(error, target);
		left.set(-output);
		right.set(output);
	}
	
	public static void set(double power)
	{
		right.set(power);
		left.set(-power);
	}
	
	public static void resetPID()
	{
		pid.reset();
	}
}
