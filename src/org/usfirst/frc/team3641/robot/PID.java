package org.usfirst.frc.team3641.robot;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PID
{
	public static final int OFF = 0, PROPORTIONAL = 1, CONSTANT = 2;
	
	private static ArrayList<PID> instances = new ArrayList<PID>();

	private PropertyReader properties;
	private double errorRefresh, lastError;
	private double BkP = 0, BkI = 0, BkD = 0, BkFF = 0, Bdeadband = 0;
	private double kP, kI, kD, kFF, deadband;
	private int BfeedForwardMode = 0;
	private int feedForwardMode;
	private String name;

	public PID(String name)
	{
		properties = new PropertyReader(name);
		this.name = name;
		instances.add(this);
		reset();
	}
	
	public void readConfig()
	{
		properties.reloadFile();
		this.kP = properties.readDouble("kP", BkP);
		this.kI = properties.readDouble("kI", BkI);
		this.kD = properties.readDouble("kD", BkD);
		
		this.kFF = properties.readDouble("kFF", BkFF);
		this.feedForwardMode = properties.readInt("feedForwardMode", BfeedForwardMode);
		
		this.deadband = properties.readDouble("deadband", Bdeadband);
	}
	
	public void setBackupValues(double BkP, double BkI, double BkD, double BkFF, int BfeedForwardMode, double Bdeadband)
	{
		this.BkP = BkP;
		this.BkI = BkI;
		this.BkD = BkD;
		
		this.BkFF = BkFF;
		this.BfeedForwardMode = BfeedForwardMode;
		
		this.Bdeadband = Bdeadband;
	}
	
	public void setBackupValues(double BkP, double BkI, double BkD, double BkFF, int BfeedForwardMode)
	{
		setBackupValues(BkP, BkI, BkD, BkFF, BfeedForwardMode, 0);
	}
	
	public void setBackupValues(double BkP, double BkI, double BkD, double Bdeadband)
	{
		setBackupValues(BkP, BkI, BkD, 0, 0, Bdeadband);
	}

			
	public double pid(double error, double target)
	{
		if(deadband != 0)
		{
			if(Math.abs(error) <= deadband) errorRefresh += error;
			else errorRefresh = 0;
		}
		else errorRefresh += error;
				
		double output = (error * kP) + (errorRefresh * kI) + ((error-lastError) * kD);
		lastError = error;
		
		if(feedForwardMode == PROPORTIONAL) output += (target/kFF);
		else if(feedForwardMode == CONSTANT)
		{
			if(output > 0) output += kFF;
			else if(output < 0) output -= kFF;
		}

		if(name != null && Constants.PRINT_PID)
		{
			if(Constants.VERBOSE >= Constants.HIGH) System.out.println(name + " PID: { P:" + error * kP + ", I:" + errorRefresh * kI + ", D:" + lastError * kD + "; Output: " + output + " }");
			SmartDashboard.putNumber(name + " P", error * kP);
			SmartDashboard.putNumber(name + " I", errorRefresh * kI);
			SmartDashboard.putNumber(name + " D", lastError * kD);
		}
		return output;
	}

	public double pid(double error)
	{
		return pid(error, 0);
	}
			
	public void reset()
	{
		errorRefresh = 0;
		lastError = 0;
	}
	
	public static void reloadAllConfigs()
	{
		for(PID instance : instances) instance.readConfig();
		System.out.println("Finished Reading Config files");
	}

}
