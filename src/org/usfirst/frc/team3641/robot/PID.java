package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PID
{
	private double errorRefresh, lastError;
	private double KP, KI, KD, FF;
	private String name;

	public PID(double kp, double ki, double kd, double ff, String Name)
	{
		errorRefresh = 0;
		lastError = 0;
		KP = kp;
		KI = ki;
		KD = kd;
		FF = ff;
		name = Name;
	}
	public PID(double kp, double ki, double kd, double ff)
	{
		this(kp, ki, kd, ff, null);
	}
	public PID(double kp, double ki, double kd)
	{
		this(kp, ki, kd, 1.0, null);
	}
	
	public double pid(double error, double target)
	{
		errorRefresh += error;
		double output = (target/FF) + (error * KP) + (errorRefresh * KI) + ((error-lastError) * KD);
		lastError = error;
		if(name != null)
		{
			SmartDashboard.putNumber(name + " P", error * KP);
			SmartDashboard.putNumber(name + " I", errorRefresh * KI);
			SmartDashboard.putNumber(name + " D", lastError * KD);
		}
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
