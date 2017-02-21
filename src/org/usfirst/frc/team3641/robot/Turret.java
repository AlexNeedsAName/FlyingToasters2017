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

	/**
	 * Initalizes the turret, its Talon, and its PID.
	 */
	private Turret()
	{
		turretTalon = new CANTalon(Constants.TURRET_TALON);
		turretTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		
		turretPID = new PID("Turret");
		turretPID.setBackupValues(Constants.TURRET_KP, Constants.TURRET_KI, Constants.TURRET_KD, Constants.TURRET_DEADBAND);
		turretPID.readConfig();
		
		turretTalon.enableBrakeMode(true);
	}

	/**
	 * Turns by the specified number of degrees.
	 * 
	 * @param angle The angle in degrees to turn by.
	 * @param threshold The amount of acceptable error in degrees.
	 * @return True if error is within the threshold.
	 */
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
	
	/**
	 * Manually sets the power of the turret motor.
	 * 
	 * @param power The power to set the motor to.
	 */
	public static void set(double power)
	{
		turretTalon.set(power);
		SmartDashboard.putNumber("Turret Encoder", Sensors.getTurretAngle());
	}

	/**
	 * Reset the values turnBy uses.
	 */
	public static void reset()
	{
		alreadyRotating = false;
		turretTalon.setEncPosition(0);
		turretTalon.set(0);
	}
}
