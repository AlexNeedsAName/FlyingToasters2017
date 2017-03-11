package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation;

public class Console
{
	private static Console instance;
	private static Timer uptime;
	private static DriverStation DS;
	
	public static Console getInstance()
	{
		if(instance == null) instance = new Console();
		return instance;
	}
	
	private Console()
	{
		uptime = new Timer();
		uptime.reset();
		uptime.start();
				
		DS = DriverStation.getInstance();
	}
	
	public static void print(String msg)
	{
		String prefix = getPrefix();
		System.out.println(prefix + msg);
	}
	
	public static void print(String msg, Constants.Verbosity.Level level)
	{
		if(Constants.Verbosity.isAbove(level)) print(msg);
	}
	
	public static void printWarning(String msg)
	{
		print("WARNING: " + msg);
	}
	
	public static void printWarning(String msg, Constants.Verbosity.Level level)
	{
		if(Constants.Verbosity.isAbove(level)) printWarning(msg);
	}
	
	public static void printError(String msg)
	{
		print("ERROR: " + msg);
	}
	
	public static void printError(String msg, Constants.Verbosity.Level level)
	{
		if(Constants.Verbosity.isAbove(level)) printError(msg);
	}
	
	private static String getPrefix()
	{
		String mode =                "[Teleop] ";
		if(DS.isAutonomous()) mode = "[Auton]  ";
		else if(DS.isTest()) mode =  "[Test]   ";
		
		String time = String.format("%.2f", uptime.get());
		time = "[" + time + "] ";
		
		return mode + time;
	}
	
	public static void restartTimer()
	{
		print("Restarting Match Timer");
		uptime.reset();
		uptime.start();
		print("Match Started");
	}
	
}
