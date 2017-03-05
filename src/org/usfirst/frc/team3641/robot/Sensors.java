package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.kauailabs.navx.frc.AHRS;

public class Sensors
{
	private static Sensors instance;
	private static double ultrasonicDistance = 0, shooterRPM, totalDriveDistance, currentDriveDistance, angle, turretAngle;
	private static boolean isStill;
	private static double pressure;
	private static boolean weHasGear;
	private static boolean weHasPressure;

	private static DigitalInput doesWeHasGearSwitch;
	public static Ultrasonic ultra;
	private static AHRS gyro;
	private static ADXRS450_Gyro SPIgyro;
	private static AnalogInput pressureSensor;
	
	public static Sensors getInstance()
	{
		if(instance == null) instance = new Sensors();
		return instance;
	}

	/**
	 * Initialize the sensors class and all the sensors it uses.
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
			gyro = new AHRS(SerialPort.Port.kMXP);
			pressureSensor = new AnalogInput(Constants.AnalogIn.PRESSURE_SENSOR); 
			doesWeHasGearSwitch = new DigitalInput(Constants.DigitalIO.DOES_WE_HAS_GEAR_SWITCH);
		}
	}

	/**
	 * Prints the sensors we care about to the SmartDashboard.
	 */
	public static void printAll()
	{
		SmartDashboard.putBoolean("Does We Has Gear?", doesWeHasGear());
		SmartDashboard.putBoolean("Enough Pressure?", doesWeHasEnoughPressure());
		SmartDashboard.putNumber("Current Pressure", getPressure());
		SmartDashboard.putNumber("Angle", getAngle());
	}
	
	/**
	 * Get the current value of all the sensors on the robot so values don't change within a single code loop.
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
			//Shooter Stuff
			shooterRPM = Shooter.right.getEncVelocity() * Constants.Conversions.ENCODER_TO_METERS;
			turretAngle = Turret.turretTalon.getEncPosition() * Constants.Conversions.TURRET_ENCODER_TO_ANGLE;

			//DriveBase Stuff
			angle = gyro.getAngle();
			isStill = !gyro.isMoving();
			currentDriveDistance = (double) DriveBase.left.getEncPosition() / Constants.Conversions.DRIVE_ENCODER_TICKS_PER_TURN * Constants.Conversions.DRIVE_WHEEL_CIRCUMFERENCE;
			if(Gearbox.getGear() == Gearbox.Gear.LOW) currentDriveDistance *= Constants.Conversions.LOW_GEAR_RATIO;
			else currentDriveDistance *= Constants.Conversions.HIGH_GEAR_RATIO;
	
			//Gear Sensor
			weHasGear = !doesWeHasGearSwitch.get();
			
			//Pnumatics
			pressure = Constants.Conversions.PRESSURE_MULTIPLIER * pressureSensor.getAverageVoltage()/Constants.Conversions.VCC - Constants.Conversions.PRESSURE_ZERO_VALUE;
			weHasPressure = (getPressure() >= Constants.Pnumatics.WORRY_PRESSURE);
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
	 * Set the total encoder distance.
	 * 
	 * @param distance The distance in meters you want to set.
	 */
	public static void setDriveDistance(double distance)
	{
		totalDriveDistance = distance;
		resetCurrentDriveDistance();
	}

	/**
	 * Reset the drive distance to 0.
	 */
	public static void resetDriveDistance()
	{
		setDriveDistance(0);
	}
	
	/**
	 * Resets the distance driven in the current gear.
	 */
	private static void resetCurrentDriveDistance()
	{
		currentDriveDistance = 0;
		DriveBase.left.setEncPosition(0);
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
		return currentDriveDistance + totalDriveDistance;
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
	
	/**
	 * Call this when you shift gears to account for the different gear ratio.
	 */
	public static void shiftEncoderGear()
	{
		totalDriveDistance += currentDriveDistance;
		resetCurrentDriveDistance();
	}
	
	/**
	 * Gets the current pressure in the tank.
	 * 
	 * @return The pressure in PSI.
	 */
	public static double getPressure()
	{
		return pressure;
	}
	
	/**
	 * Do we have a gear in the robot?
	 * 
	 * @return True if we have a gear.
	 */
	public static boolean doesWeHasGear()
	{
		return weHasGear;
	}
	
	/**
	 * Do we need to start worrying about the pressure in the robot?
	 * 
	 * @return True if we have enough pressure.
	 */
	public static boolean doesWeHasEnoughPressure()
	{
		return weHasPressure;
	}
}