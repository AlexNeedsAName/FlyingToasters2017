package org.usfirst.frc.team3641.robot;
import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Spark;

public class Shooter
{
	private static Shooter instance;
	public static CANTalon left, right;
	private static Spark elevator;
	private static PID flywheelPID;
	private static double error;

	public static Shooter getInstance()
	{
		if(instance == null) instance = new Shooter();
		return instance;
	}

	/**
	 * Initalize the shooter and its motor controllers.
	 */
	public Shooter()
	{
		left = new CANTalon(Constants.CAN.Talons.SHOOTER_LEFT);
		right = new CANTalon(Constants.CAN.Talons.SHOOTER_RIGHT);
		right.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
		elevator = new Spark(Constants.PWM.Sparks.SHOOTER_ELEVATOR);
		
		flywheelPID = new PID("ShooterFlywheel");
		flywheelPID.setBackupValues(Constants.PID.SHOOTER_KP, Constants.PID.SHOOTER_KI, Constants.PID.SHOOTER_KD, Constants.PID.SHOOTER_FF, PID.PROPORTIONAL);
		flywheelPID.readConfig();
	}

	/**
	 * Calculate the ideal speed of the shooter based off a given distance.
	 * 
	 * @param distance The distance to the goal in meters.
	 * @return The ideal speed of the shooter in RPM.
	 */
	public static double calcSpeed(double distance)
	{
		return 0; //TODO: Add kinematic equation based on distance
	}
	
	/**
	 * Target a specific speed for the shooter flywheels.
	 * 
	 * @param target The target speed in RPM.
	 * @return The current error in RPM.
	 */
	public static double setRPM(double target)
	{
		SmartDashboard.putNumber("Target RPM", target);
		double current = Sensors.getShooterRPM();
		error = target - current;
		double output = flywheelPID.run(error, target);
		set(output);
		return error;
	}

	/**
	 * Set the raw power of the shooter flywheels.
	 * 
	 * @param power The power of the shooter flywheel motors.
	 */
	public static void set(double power)
	{
		if(power > 1) power = 1;
		SmartDashboard.putNumber("Power Out", power);
		SmartDashboard.putNumber("RPM", Sensors.getShooterRPM());
		SmartDashboard.putNumber("RPM over Time", Sensors.getShooterRPM());
		right.set(power);
		left.set(-power);
	}

	/**
	 * Stop spinning the flywheels
	 */
	public static void reset()
	{
		if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.LOW)) System.out.println("Reset Shooter");
		SmartDashboard.putNumber("Target RPM", 0);
		set(0);
		error = 0;
		flywheelPID.reset();
	}

	/**
	 * If the flywheel speed is with the error threshold, shoot.
	 */
	public static void fire()
	{
		if(Math.abs(error) < Constants.Thresholds.SHOOTER_MAX_ERROR) forceFire();
		else elevator.set(0);

	}
	
	/**
	 * Shoot regardless of the current flywheel speed.
	 */
	public static void forceFire()
	{
		elevator.set(1);
		Hopper.adjatate();
	}
	
	/**
	 * Stop firing the shooter.
	 * 
	 * Stops running the hopper and the elevator, but not the flywheel. Intended for pausing
	 * while the PID corrects the RPM of the shooter.
	 */
	public static void stopFiring()
	{
		if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.MID)) System.out.println("Stopped Firing Shooter");
		elevator.set(0);
		Hopper.stopAdjatating();
	}
}
