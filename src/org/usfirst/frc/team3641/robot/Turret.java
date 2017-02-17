package org.usfirst.frc.team3641.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret
{
	public static CANTalon turretTalon;
	private static Turret instance;
	private static PID turretPID;
	public static boolean alreadyRotating = false;
	private static double finalAngle;

	public static Turret getInstance()
	{
		if(instance == null) instance = new Turret();
		return instance;
	}

	private Turret()
	{
		turretTalon = new CANTalon(Constants.TURRET_TALON);
		turretTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		
		turretPID = new PID(Constants.TURRET_KP, Constants.TURRET_KI, Constants.TURRET_KD, "Turret");
		turretPID.setIDeadband(Constants.TURRET_DEADBAND);
		turretTalon.enableBrakeMode(true);
	}

	public static boolean turnBy(double angle, double threshold)
	{
		if(!alreadyRotating)
		{
			double initalAngle = Sensors.getTurretAngle();
			finalAngle = initalAngle + angle;
			alreadyRotating = true;
			if(Constants.VERBOSE >= Constants.LOW) System.out.println("Turret Rotating " + angle + "°");
		}

		SmartDashboard.putNumber("Target", finalAngle);
		SmartDashboard.putNumber("Raw Encoder", turretTalon.getPosition());
		
		double error = finalAngle - Sensors.getTurretAngle();
		double output = turretPID.pid(error);

		set(output);

		return (Math.abs(error) < threshold);
	}
	
	public static void set(double power)
	{
		turretTalon.set(power);
		SmartDashboard.putNumber("Turret Encoder", Sensors.getTurretAngle());
	}

	public static void reset()
	{
		alreadyRotating = false;
		turretTalon.setEncPosition(0);
		turretTalon.set(0);
	}
}
