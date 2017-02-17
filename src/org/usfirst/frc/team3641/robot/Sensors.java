package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Ultrasonic;
import com.kauailabs.navx.frc.AHRS;

public class Sensors
{
	private static Sensors instance;
	private static double ultrasonicDistance = 0, shooterRPM, driveDistance, angle, turretAngle;
	private static boolean isStill;

	private static AnalogInput ultrasonic;
	public static Ultrasonic ultra;
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
		if(Constants.runningAleksBot)
		{
			ultra = new Ultrasonic(Constants.ULTRASONIC_ECHO , Constants.ULTRASONIC_TRIGGER);
			ultra.setAutomaticMode(true);
		}
		else
		{
			ultrasonic = new AnalogInput(Constants.ULTRASONIC_PORT); 
		}
	}

	public static void poll()
	{
		if(Constants.runningAleksBot)
		{
			angle = SPIgyro.getAngle();
			ultrasonicDistance = ultra.getRangeMM() * 1000;
		}
		else
		{
			shooterRPM = Shooter.right.getEncVelocity() * Constants.ENCODER_TO_METERS;
			ultrasonicDistance = ultrasonic.getAverageVoltage() * Constants.VOLTAGE_TO_METERS;
			driveDistance = DriveBase.left.getAnalogInPosition() * Constants.DRIVE_ENCODER_TO_METERS;
			turretAngle = Turret.turretTalon.getEncPosition() * Constants.TURRET_ENCODER_TO_ANGLE;
			angle = gyro.getAngle();
			isStill = !gyro.isMoving();
		}
	}

	public static void resetGyro()
	{
		if(Constants.runningAleksBot) SPIgyro.reset();
		else gyro.reset();
	}

	public static void resetDriveDistance(double distance)
	{
		driveDistance = distance;
		DriveBase.left.setEncPosition((int) (distance / Constants.ENCODER_TO_METERS));
	}
	public static void resetDriveDistance()
	{
		resetDriveDistance(0);
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

	public static double getTurretAngle()
	{
		return turretAngle;
	}
	
	public static boolean isStill()
	{
		return isStill;
	}
}