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
	private static double initalAngle, finalAngle;

	public static Turret getInstance()
	{
		if(instance == null) instance = new Turret();
		return instance;
	}

	private Turret()
	{
		turretTalon = new CANTalon(1);
		turretTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		turretPID = new PID(Constants.TURRET_KP, Constants.TURRET_KI, Constants.TURRET_KD, 1, "Turret");
	}

	public static boolean turnBy(double angle, double threshold)
	{
		if(!alreadyRotating)
		{
			double initalAngle = Sensors.getTurretAngle();
			finalAngle = initalAngle + angle;
			alreadyRotating = true;
		}

		SmartDashboard.putNumber("Target", finalAngle);
		
		double error = finalAngle - Sensors.getTurretAngle();
		double output = turretPID.pid(error);

		turretTalon.set(output);
		SmartDashboard.putNumber("Turret Output", output);

		return (error < threshold);
	}

	public static void reset()
	{
		alreadyRotating = false;
		turretTalon.setEncPosition(0);
	}
}
