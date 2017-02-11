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
	private static PID pid;
	private static double error;

	public static Shooter getInstance()
	{
		if(instance == null) instance = new Shooter();
		return instance;
	}

	public Shooter()
	{
		left = new CANTalon(Constants.SHOOTER_LEFT_TALON);
		right = new CANTalon(Constants.SHOOTER_RIGHT_TALON);
		right.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
		elevator = new Spark(Constants.SHOOTER_ELEVATOR_SPARK);
		pid = new PID(Constants.SHOOTER_KP, Constants.SHOOTER_KI, Constants.SHOOTER_KD, Constants.SHOOTER_FF, "Shooter");
	}

	public static double calcSpeed(double distance)
	{
		return Constants.AUTON_RPM; //TODO: Add kinematic equation based on distance
	}
	
	public static double setRPM(double target)
	{
		SmartDashboard.putNumber("Target RPM", target);
		double current = Sensors.getShooterRPM();
		error = target - current;
		double output = pid.pid(error, target);
		set(output);
		return error;
	}

	public static void set(double power)
	{
		if(power > 1) power = 1;
		SmartDashboard.putNumber("Power Out", power);
		SmartDashboard.putNumber("RPM", Sensors.getShooterRPM());
		SmartDashboard.putNumber("RPM over Time", Sensors.getShooterRPM());
		right.set(power);
		left.set(-power);
	}

	public static void reset()
	{
		SmartDashboard.putNumber("Target RPM", 0);
		set(0);
		error = 0;
		pid.reset();
	}

	public static void fire()
	{
		if(Math.abs(error) < 50) forceFire();
		else stopFiring();

	}
	public static void forceFire()
	{
		elevator.set(1);
		Hopper.adjatate();
	}
	
	public static void stopFiring()
	{
		elevator.set(0);
		Hopper.stopAdjatating();
	}
}
