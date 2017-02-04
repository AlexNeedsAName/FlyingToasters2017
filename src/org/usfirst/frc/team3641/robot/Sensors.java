package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.SerialPort;

import com.kauailabs.navx.frc.AHRS;
public class Sensors
{
	private static Sensors instance;
	private static double ultrasonicDistance, shooterRPM, driveDistance, angle;
	
	private static AnalogInput ultrasonic;
	private static AHRS gyro;
	private static ADXRS450_Gyro SPIgyro;
	
	public static Sensors getInstance()
	{
		gyro = new AHRS(SerialPort.Port.kMXP);
		if(instance == null) instance = new Sensors();
		return instance;
	}
	
	private Sensors()
	{
		ultrasonic = new AnalogInput(Constants.ULTRASONIC_PORT); 
	}
	
	public static void poll()
	{
		shooterRPM = Shooter.right.getEncVelocity() * Constants.ENCODER_TO_METERS;
		ultrasonicDistance = ultrasonic.getAverageVoltage() * Constants.VOLTAGE_TO_METERS;
		driveDistance = DriveBase.left.getEncPosition() * Constants.DRIVE_ENCODER_TO_METERS;
		if(Constants.runningAleksBot) SPIgyro = new ADXRS450_Gyro();
		else angle = gyro.getAngle();
	}
	
	public static void resetGyro()
	{
		if(Constants.runningAleksBot) SPIgyro = new ADXRS450_Gyro();
		else gyro.reset();
	}
	
	public static void resetDriveDistance()
	{
		DriveBase.left.setEncPosition(0);
	}
	
	public static double getDistance()
	{
		return ultrasonicDistance;
	}
	
	public static double getShooterRPM()
	{
		return shooterRPM;
	}
	
	public static double getDriveDistance()
	{
		return driveDistance;
	}
	
	public static double getAngle()
	{
		return angle;
	}
	
}
