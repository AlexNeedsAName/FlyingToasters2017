package org.usfirst.frc.team3641.robot;
import org.usfirst.frc.team3641.robot.Constants.PWM;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveBase
{
	private static DriveBase instance;
	public static CANTalon left1, left2, left3, right1, right2, right3;
	private static Victor PWMleft, PWMleftSlave, PWMright, PWMrightSlave;
	private static PID rotationPID, drivePID;
	
	private static boolean squaredRotation, squaredPower;

	private static DriveMode mode;

	public static DriveBase getInstance()
	{
		if(instance == null) instance = new DriveBase();
		return instance;
	}

	/**
	 * Different drive modes.
	 */
	public enum DriveMode
	{
		NORMAL, REVERSE
	}
		
	/**
	 * Initalizes the Drive Base with its Talon and PID classes.
	 */
	private DriveBase()
	{
		if(Constants.runningAleksBot)
		{
			PWMleft = new Victor(PWM.Victors.LEFT);
			PWMleftSlave = new Victor(PWM.Victors.LEFT_SLAVE);
			PWMright = new Victor(Constants.PWM.Victors.RIGHT);
			PWMrightSlave = new Victor(Constants.PWM.Victors.RIGHT_SLAVE);
		}
		
		left1 = new CANTalon(Constants.CAN.Talons.DRIVEBASE_LEFT_1);
		left2 = new CANTalon(Constants.CAN.Talons.DRIVEBASE_LEFT_2);
		left3 = new CANTalon(Constants.CAN.Talons.DRIVEBASE_LEFT_3);
		right1 = new CANTalon(Constants.CAN.Talons.DRIVEBASE_RIGHT_1);
		right2 = new CANTalon(Constants.CAN.Talons.DRIVEBASE_RIGHT_2);
		right3 = new CANTalon(Constants.CAN.Talons.DRIVEBASE_RIGHT_3);
		left1.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
		
		rotationPID = new PID("DriveBaseRotation");
		rotationPID.setBackupValues(Constants.PID.DRIVEBASE_ROTATION_KP, Constants.PID.DRIVEBASE_ROTATION_KI, Constants.PID.DRIVEBASE_ROTATION_KD, Constants.PID.DRIVEBASE_ROTATION_DEADBAND);
		rotationPID.readConfig();
		
		drivePID = new PID("DriveBase");
		drivePID.setBackupValues(Constants.PID.DRIVEBASE_KP, Constants.PID.DRIVEBASE_KI, Constants.PID.DRIVEBASE_KD, Constants.PID.DRIVEBASE_FF, PID.CONSTANT);
		drivePID.readConfig();
		
		squaredRotation = false;
		squaredPower = false;
	}

	/**
	 * Turns break mode on or off.
	 * 
	 * @param on True if you want it on, false if you want it off.
	 */
	public static void setBreakMode(boolean on)
	{
		left1.enableBrakeMode(on);
		left2.enableBrakeMode(on);
		left3.enableBrakeMode(on);
		right1.enableBrakeMode(on);
		right2.enableBrakeMode(on);
		right3.enableBrakeMode(on);
	}

	/**
	 * Turn on or off squared rotation control.
	 * 
	 * @param on True for on, False for off.
	 */
	public static void setSquaredRotation(boolean on)
	{
		squaredRotation = on;
		SmartDashboard.putBoolean("Squared Rotation", on);
	}
			
	/**
	 * Turn on or off squared power control.
	 * 
	 * @param on True for on, False for off.
	 */
	public static void setSquaredPower(boolean on)
	{
		squaredPower = on;
		SmartDashboard.putBoolean("Squared Power", on);
	}
	
	/**
	 * Toggle squared power control.
	 * 
	 * @param on True for on, False for off.
	 */
	public static void toggleSquaredRotation()
	{
		setSquaredRotation(!squaredRotation);
	}
	
	/**
	* Toggle squared power control.
	* 
	* @param on True for on, False for off.
	*/
	public static void toggleSquaredPower()
	{
		setSquaredPower(!squaredPower);
	}
	
	/**
	 * Turn on or off squared controls.
	 * 
	 * @param on True for on, False for off.
	 */
	public static void setSquaredControls(boolean on)
	{
		setSquaredRotation(on);
		setSquaredPower(on);
	}
	
	/**
	* Toggle squared controls.
	* 
	* @param on True for on, False for off.
	*/
	public static void toggleSquaredControls()
	{
		toggleSquaredRotation();
		toggleSquaredPower();
	}

	/**
	 * Drives the robot with arcade drive.
	 * 
	 * @param power The speed forwards/backwards
	 * @param rotation The rotation clockwise/counterclockwise
	 */
	public static void driveArcade(double power, double rotation)
	{
		if(Gearbox.inPTOMode()) rotation = 0;
		if(mode == DriveMode.REVERSE) rotation = -rotation;
		
		if(squaredRotation) rotation = Math.signum(rotation) * (rotation*rotation);
		if(squaredPower) power = Math.signum(power) * (power*power);

		double leftPower = power + rotation;
		double rightPower = power - rotation;
		
		driveTank(leftPower, rightPower);
	}
	
	/**
	 * Drives The robot with tank drive.
	 * 
	 * @param leftPower Power for the left half of the drive train (your left stick)
	 * @param rightPower Power for the right half of the drive train (your right stick)
	 */
	public static void driveTank(double leftPower, double rightPower)
	{

		double maxPower;
		if(leftPower > rightPower) maxPower = leftPower;
		else maxPower = rightPower;

		if(maxPower > 1)
		{
			leftPower/= maxPower;
			rightPower/= maxPower;
		}

		if(mode == DriveMode.REVERSE)
		{
			leftPower*= -1;
			rightPower*= -1;
		}
		
		if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.INSANITY)) System.out.println("Left Power: " + leftPower + "; Right Power: " + rightPower);

		if(Constants.runningAleksBot)
		{
			PWMleft.set(leftPower);
			PWMleftSlave.set(leftPower);
			PWMright.set(-rightPower);
			PWMrightSlave.set(-rightPower);
		}
		else
		{
			left1.set(leftPower);
			left2.set(leftPower);
			left3.set(leftPower);
			right1.set(-rightPower);
			right2.set(-rightPower);
			right3.set(-rightPower);
		}
	}

	/**
	 * Switch between normal and reverse mode.
	 * 
	 * @param Mode DriveMode you want to switch to.
	 */
	public static void setDriveMode(DriveMode Mode)
	{
		if(mode != Mode)
		{
			mode = Mode;
			if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.LOW)) System.out.println("Switching to " + mode.toString() + " mode.");
		}
	}

	/**
	 * Shifts the gearbox to high or low gear.
	 * 
	 * @param gear The gearbox gear you want to shift to
	 */
	public static void shift(Gearbox.Gear gear)
	{
		Gearbox.shift(gear);
	}

	/**
	 * Turns to an absolute angle.
	 * 
	 * @param targetAngle The angle in degrees to turn to.
	 * @param threshold How accurate you want to be.
	 * @return True if you are within error is within the threshold.
	 */
	public static boolean turnTo(double targetAngle, double threshold)
	{
		double error = Coords.calcAngleError(targetAngle, Sensors.getAngle());
		driveArcade(0, rotationPID.pid(error));
		return (Math.abs(error) <= threshold);
	}

	/**
	 * Turns by an absolute angle.
	 * 
	 * @param inputDegrees The angle in degrees to turn to.
	 * @param errorMargin How accurate you want to be.
	 * 
	 * @deprecated Replaced by {@link #turnTo()}. To use with relative angles, just set the target
	 * to be your current angle when you start plus your target.
	 */
	@Deprecated
	public static void turnDegrees(double inputDegrees, double errorMargin) 
	{
		Sensors.resetGyro();
		Sensors.poll();
		double gyroAngle = Sensors.getAngle();

		while (gyroAngle < inputDegrees-errorMargin || gyroAngle > inputDegrees+errorMargin) 
		{
			if (gyroAngle < inputDegrees-errorMargin) 
			{
				//Turn right
				driveArcade(0, 0.8);
			}
			if (gyroAngle > inputDegrees+errorMargin) 
			{
				//Turn left
				driveArcade(0, -0.8);
			}
			Sensors.poll();
			gyroAngle = Sensors.getAngle();
		}
		driveArcade(0, 0);
	}

	/**
	 * Drives to an absolute distance based off encoders
	 * @param distance Distance in meters to drive to.
	 * @return Distance off.
	 */
	public static double driveTo(double distance)
	{
		double error = distance - Sensors.getDriveDistance();
		double output = drivePID.pid(error);
		driveArcade(output, 0);
		return error;
	}
	
}
