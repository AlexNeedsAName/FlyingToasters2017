package org.usfirst.frc.team3641.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;

public class Turret
{
	public static CANTalon turretTalon;
	private static Turret instance;
	private static PID turretPID;
	private static boolean alreadyRotating = false;
	private static double initalAngle, finalAngle;
	
	public static Turret getInstance()
	{
		if(instance == null) instance = new Turret();
		return instance;
	}
	
	private Turret()
	{
		turretTalon = new CANTalon(Constants.TURRET_TALON);
		turretTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
		turretPID = new PID(Constants.TURRET_KP, Constants.TURRET_KI, Constants.TURRET_KD, 0, "Turret");
	}
	
	public static boolean turnBy(double angle, double threshold)
	{
		if(!alreadyRotating)
		{
			double initalAngle = Sensors.getTurretAngle();
			finalAngle = initalAngle + angle;
		}
		
		double error = finalAngle - Sensors.getTurretAngle();
		turretPID.pid(error);

		return (error < threshold);
	}
	
}
