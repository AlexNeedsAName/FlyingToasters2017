package org.usfirst.frc.team3641.robot;

public class PID
{
	private double errorRefresh, lastError;
	private double KP, KI, KD, FF;

	public PID(double kp, double ki, double kd, double ff)
	{
		errorRefresh = 0;
		lastError = 0;
		KP = kp;
		KI = ki;
		KD = kd;
		FF = ff;
	}
	public PID(double kp, double ki, double kd)
	{
		this(kp, ki, kd, 1.0);
	}
	
	public double pid(double error, double target)
	{
		errorRefresh += error;
		double output = (target/FF) + (error * KP) + (errorRefresh * KI) + ((error-lastError) * KD);
		lastError = error;
		return output;
	}
	
	public double pid(double error)
	{
		return pid(error, 0);
	}
	
	public double getI()
	{
		return errorRefresh;
	}
	
	public void reset()
	{
		errorRefresh = 0;
		lastError = 0;
	}
}
