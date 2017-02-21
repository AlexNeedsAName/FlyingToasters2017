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

	/**
	 * Initalize the sensors class and all the sensors it uses.
	 */
	private Sensors()
	{
		if(Constants.runningAleksBot)
		{
			ultra = new Ultrasonic(Constants.AnalogIn.ULTRASONIC_TRIGGER , Constants.AnalogIn.ULTRASONIC_TRIGGER);
			ultra.setAutomaticMode(true);
		}
		else
		{
			ultrasonic = new AnalogInput(Constants.AnalogIn.ULTRASONIC_PORT); 
		}
	}

	/**
	 * Poll all of the sensors on the robot.
	 */
	public static void poll()
	{
		if(Constants.runningAleksBot)
		{
			angle = SPIgyro.getAngle();
			ultrasonicDistance = ultra.getRangeMM() * 1000;
		}
		else
		{
			shooterRPM = Shooter.right.getEncVelocity() * Constants.Conversions.ENCODER_TO_METERS;
			ultrasonicDistance = ultrasonic.getAverageVoltage() * Constants.Conversions.VOLTAGE_TO_METERS;
			driveDistance = DriveBase.left1.getAnalogInPosition() * Constants.Conversions.DRIVE_ENCODER_TO_METERS;
			turretAngle = Turret.turretTalon.getEncPosition() * Constants.Conversions.TURRET_ENCODER_TO_ANGLE;
			angle = gyro.getAngle();
			isStill = !gyro.isMoving();
		}
	}

	/**
	 * Reset the gyro angle to 0.
	 */
	public static void resetGyro()
	{
		if(Constants.runningAleksBot) SPIgyro.reset();
		else gyro.reset();
	}

	/**
	 * Reset the drive distance.
	 * @param distance The distance in meters to set the encoder distance to.
	 */
	public static void resetDriveDistance(double distance)
	{
		DriveBase.left1.setEncPosition((int) (distance / Constants.Conversions.ENCODER_TO_METERS));
		driveDistance = DriveBase.left1.getAnalogInPosition() * Constants.Conversions.DRIVE_ENCODER_TO_METERS;
	}
	
	/**
	 * Reset the drive distance to 0.
	 */
	public static void resetDriveDistance()
	{
		resetDriveDistance(0);
	}

	/**
	 * Get the ultrasonic distance.
	 * 
	 * @return The ultrasonic distance in meter.
	 */
	public static double getDistance()
	{
		return ultrasonicDistance;
	}

	/**
	 * Get the current RPM of the shooter.
	 * 
	 * @return The current RPM of the shooter.
	 */
	public static double getShooterRPM()
	{
		return shooterRPM;
	}

	/**
	 * Get the drive distance in meters.
	 * 
	 * @return The drive distance in meters.
	 */
	public static double getDriveDistance()
	{
		return driveDistance;
	}

	/**
	 * Get the current angle of the gyro.
	 * 
	 * @return The angle in degrees of the gyro.
	 */
	public static double getAngle()
	{
		return angle;
	}

	/**
	 * Get the current angle of the turret.
	 * 
	 * @return The current angle in degrees of the turret.
	 */
	public static double getTurretAngle()
	{
		return turretAngle;
	}
	
	/**
	 * Is the robot still?
	 * 
	 * @return True if the robot is still.
	 */
	public static boolean isStill()
	{
		return isStill;
	}
}