package org.usfirst.frc.team3641.robot;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config
{
	private static final String BASE_DIR = "/home/lvuser/tuning/";
	private static final String EXTENSION = ".properties";
	private String file;
	private Properties config;
	private PID pid;
	private String name;
	private double kP, kI, kD, feedForward, deadband;
	private int feedForwardType;
	
	private double backupKP, backupKI, backupKD, backupDeadband, backupFeedForward;
	private int backupFeedForwardType;
	
	public Config(String name)
	{
		file = BASE_DIR + name + EXTENSION;
		config = new Properties();
		try
		{
			config.load(new FileReader(file));
		}
		catch (IOException e)
		{
			System.err.println("WARNING: Failed to open " + file);
		}
	}
	
	public void setBackupValues(double BackupKP, double BackupKI, double BackupKD, double BackupDeadband, double BackupFeedForward, int BackupFeedForwardType)
	{
		backupKP = BackupKP;
		backupKI = BackupKI;
		backupKD = BackupKD;
		backupDeadband = BackupDeadband;
		backupFeedForward = BackupFeedForward;
		backupFeedForwardType = BackupFeedForwardType;
	}
	
	public void setBackupValues(double BackupKP, double BackupKI, double BackupKD, double BackupFeedForward, int BackupFeedForwardType)
	{
		setBackupValues(BackupKP, BackupKI, BackupKD, 0, BackupFeedForward, BackupFeedForwardType);
	}
	
	public void setBackupValues(double BackupKP, double BackupKI, double BackupKD, double BackupDeadband)
	{
		setBackupValues(BackupKP, BackupKI, BackupKD, BackupDeadband, 0, 0);
	}
	
	public void setBackupValues(double BackupKP, double BackupKI, double BackupKD)
	{
		setBackupValues(BackupKP, BackupKI, BackupKD, 0, 0, 0);
	}
	
	public PID getPID()
	{
		if(pid == null) pid = new PID(name);
		return pid;
	}

	public void readConfig()
	{ 
		try
		{
			//We reload the file in case it has been changed. This way we can tune PID values without rebooting the rio
			config.load(new FileReader(file));
		}
		catch (IOException e)
		{
			System.err.println("WARNING: Failed to open " + file);
		}

		kP = Double.parseDouble(config.getProperty("kP", String.valueOf(backupKP)));
		kI = Double.parseDouble(config.getProperty("kI", String.valueOf(backupKI)));
		kD = Double.parseDouble(config.getProperty("kD", String.valueOf(backupKD)));
		feedForward = Double.parseDouble(config.getProperty("feedForward", String.valueOf(backupFeedForward)));
		feedForwardType = Integer.parseInt(config.getProperty("feedForwardType", String.valueOf(backupFeedForwardType)));
		deadband = Double.parseDouble(config.getProperty("deadband", String.valueOf(backupDeadband)));
		
		if(pid == null) pid = new PID(name);
		pid.setConstants(kP, kI, kD);
		if(feedForwardType == 1) pid.setProportionalFeedForward(feedForward);
		else if(feedForwardType == 2) pid.setConstantFeedForward(feedForward);
		pid.setIDeadband(deadband);
	}
}
