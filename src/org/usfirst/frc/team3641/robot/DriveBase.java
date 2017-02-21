package org.usfirst.frc.team3641.robot;
import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.Victor;

public class DriveBase
{
	private static DriveBase instance;
	public static CANTalon left, leftSlave, right, rightSlave;
	private static Victor PWMleft, PWMleftSlave, PWMright, PWMrightSlave;
	private static PID rotationPID, drivePID;

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
			PWMleft = new Victor(Constants.LEFT_VICTOR);
			PWMleftSlave = new Victor(Constants.LEFT_SLAVE_VICTOR);
			PWMright = new Victor(Constants.RIGHT_VICTOR);
			PWMrightSlave = new Victor(Constants.RIGHT_SLAVE_VICTOR);
		}
		left = new CANTalon(Constants.DRIVEBASE_LEFT_TALON);
		leftSlave = new CANTalon(Constants.DRIVEBASE_LEFT_SLAVE_TALON);
		right = new CANTalon(Constants.DRIVEBASE_RIGHT_TALON);
		rightSlave = new CANTalon(Constants.DRIVEBASE_RIGHT_SLAVE_TALON);
		left.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
		
		rotationPID = new PID("DriveBaseRotation");
		rotationPID.setBackupValues(Constants.DRIVEBASE_ROTATION_KP, Constants.DRIVEBASE_ROTATION_KI, Constants.DRIVEBASE_ROTATION_KD, Constants.DRIVEBASE_ROTATION_DEADBAND);
		rotationPID.readConfig();
		
		drivePID = new PID("DriveBase");
		drivePID.setBackupValues(Constants.DRIVEBASE_KP, Constants.DRIVEBASE_KI, Constants.DRIVEBASE_KD, Constants.DRIVEBASE_FF, PID.CONSTANT);
		drivePID.readConfig();
	}

	/**
	 * Turns break mode on or off.
	 * 
	 * @param on True if you want it on, false if you want it off.
	 */
	public static void setBreakMode(boolean on)
	{
		left.enableBrakeMode(on);
		leftSlave.enableBrakeMode(on);
		right.enableBrakeMode(on);
		rightSlave.enableBrakeMode(on);
	}
	
	/**
	 * Drives the robot with arcade drive.
	 * 
	 * @param power The speed forwards/backwards
	 * @param rotation The rotation clockwise/counterclockwise
	 */
	public static void driveArcade(double power, double rotation)
	{
		if(Constants.VERBOSE >= 4) System.out.println("Power: " + power + "; Rotation: " + String.format("%.2f", rotation));
		if(mode == DriveMode.REVERSE) power = -power;

		double leftPower = power + rotation;
		double rightPower = power - rotation;
		
		if(Constants.VERBOSE >= 4) System.out.println("Left Power: " + leftPower + "; Right Power: " + rightPower);
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
		
		if(Constants.VERBOSE >= 4) System.out.println("Left Power: " + leftPower + "; Right Power: " + rightPower);

		if(Constants.runningAleksBot)
		{
			PWMleft.set(leftPower);
			PWMleftSlave.set(leftPower);
			PWMright.set(-rightPower);
			PWMrightSlave.set(-rightPower);
		}
		else
		{
			left.set(leftPower);
			leftSlave.set(leftPower);
			right.set(-rightPower);
			rightSlave.set(-rightPower);
		}
	}

	/**
	 * Switch between normal and reverse mode.
	 * 
	 * @param Mode DriveMode you want to switch to.
	 */
	public static void setDriveMode(DriveMode Mode)
	{
		mode = Mode;
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

	//Please don't use this. It blocks drive output and all other functions due to the while loop. I'm going to remove it next commit.
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
