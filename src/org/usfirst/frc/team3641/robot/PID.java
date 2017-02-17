package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PID
{
	private double errorRefresh, lastError;
	private double KP, KI, KD, FF, MP;
	private double IRange = 0;
	private boolean deadbanding;
	private int OFF = 1, PROPORTIONAL = 1, CONSTANT = 2;
	private int feedForwardMode;
	private String name;

	public PID(double kp, double ki, double kd, String Name)
	{
		errorRefresh = 0;
		lastError = 0;
		KP = kp;
		KI = ki;
		KD = kd;
		name = Name;
		deadbanding = false;
		feedForwardMode = OFF;
	}
	
	public PID(double kp, double ki, double kd)
	{
		this(kp, ki, kd, null);
	}

	public double pid(double error, double target)
	{
		if(deadbanding)
		{
			if(Math.abs(error) <= IRange) errorRefresh += error;
			else errorRefresh = 0;
		}
		else errorRefresh += error;
				
		double output = (error * KP) + (errorRefresh * KI) + ((error-lastError) * KD);
		lastError = error;
		
		if(feedForwardMode == PROPORTIONAL) output += (target/FF);
		else if(feedForwardMode == CONSTANT)
		{
			if(output > 0) output += FF;
			else if(output < 0) output -= FF;
		}

		if(name != null)
		{
			if(Constants.VERBOSE >= Constants.HIGH) System.out.println(name + " PID: { P:" + error * KP + ", I:" + errorRefresh * KI + ", D:" + lastError * KD + "; Min Power:  " + MP + "; Output: " + output + " }");
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
	
	public void setIDeadband(double range)
	{
		if(range == 0) deadbanding = false;
		else
		{
			IRange = range;
			deadbanding = true;
		}
	}
	
	public void setProportionalFeedForward(double ff)
	{
		feedForwardMode = PROPORTIONAL;
		FF = ff;
	}
	
	public void setConstantFeedForward(double ff)
	{
		feedForwardMode = CONSTANT;
		FF = ff;
	}

	public void reset()
	{
		errorRefresh = 0;
		lastError = 0;
	}
}
