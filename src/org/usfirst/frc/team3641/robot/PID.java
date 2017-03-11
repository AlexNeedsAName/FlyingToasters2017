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

	/**
	 * Initializes a new instance of the PID class.
	 * 
	 * @param name The name of the config file to read from and the name to print for debugging
	 * purposes.
	 */
	public PID(String name)
	{
		properties = new PropertyReader(name);
		this.name = name;
		instances.add(this);
		reset();
	}
	
	/**
	 * Loads the current values from the config file. If it fails to read anything, it uses the
	 * backup value (0 unless manually set).
	 */
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
	
	/**
	 * Sets the backup values to use if they can't be read from the config file.
	 * 
	 * @param BkP THe backup kP.
	 * @param BkI The backup kI.
	 * @param BkD The backup kD.
	 * @param BkFF The backup feed forward.
	 * @param BfeedForwardMode The backup feed forward mode.
	 * @param Bdeadband The backup deadband range.
	 */
	public void setBackupValues(double BkP, double BkI, double BkD, double BkFF, int BfeedForwardMode, double Bdeadband)
	{
		this.BkP = BkP;
		this.BkI = BkI;
		this.BkD = BkD;
		
		this.BkFF = BkFF;
		this.BfeedForwardMode = BfeedForwardMode;
		
		this.Bdeadband = Bdeadband;
	}
	
	/**
	 * Sets the most of the backup values to use if they can't be read from the config file.
	 * The backup deadband will be set to 0.
	 * 
	 * @param BkP THe backup kP.
	 * @param BkI The backup kI.
	 * @param BkD The backup kD.
	 * @param BkFF The backup feed forward.
	 * @param BfeedForwardMode The backup feed forward mode.
	 */
	public void setBackupValues(double BkP, double BkI, double BkD, double BkFF, int BfeedForwardMode)
	{
		setBackupValues(BkP, BkI, BkD, BkFF, BfeedForwardMode, 0);
	}
	
	/**
	 * Sets the most of the backup values to use if they can't be read from the config file.
	 * The backup feed forward will be set to off.
	 * 
	 * @param BkP THe backup kP.
	 * @param BkI The backup kI.
	 * @param BkD The backup kD.
	 * @param Bdeadband The backup deadband.
	 */
	public void setBackupValues(double BkP, double BkI, double BkD, double Bdeadband)
	{
		setBackupValues(BkP, BkI, BkD, 0, 0, Bdeadband);
	}

	
	/**
	 * Runs the PID loop.
	 * 
	 * @param error The error to target.
	 * @param target The target value (used for proportional feed forward).
	 * @return Motor output power.
	 */
	public double run(double error, double target)
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

		if(output == 0) Console.print(name + " PID: { P:" + format(error * kP) + ", I:" + format(errorRefresh * kI) + ", D:" + format(lastError * kD) + "; Output: " + output + " }");

		SmartDashboard.putNumber(name + " P", error * kP);
		SmartDashboard.putNumber(name + " I", errorRefresh * kI);
		SmartDashboard.putNumber(name + " D", lastError * kD);
		
		return output;
	}

	/**
	 * Runs the PID loop.
	 * If using proportional feed forward, since there is no target it is ignored.
	 * 
	 * @param error The error to target.
	 * @return Motor output power.
	 */
	public double pid(double error)
	{
		return run(error, 0);
	}
			
	/**
	 * Resets the PID Loop.
	 * Sets the error over time and the last error to 0.
	 */
	public void reset()
	{
		errorRefresh = 0;
		lastError = 0;
	}
	
	/**
	 * Reloads the values of all the PID instances from their respective config files.
	 */
	public static void reloadAllConfigs()
	{
		for(PID instance : instances) if(instance.name != "ShooterFlywheel") instance.readConfig();
		Console.print("Finished Reading Config files", Constants.Verbosity.Level.LOW);
	}
	
	private static String format(double number)
	{
		return String.format("%.2f", number);
	}

}
