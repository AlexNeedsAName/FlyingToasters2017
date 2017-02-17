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

	private static boolean reverseMode;

	public static DriveBase getInstance()
	{
		if(instance == null) instance = new DriveBase();
		return instance;
	}

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

		rotationPID = new PID(Constants.DRIVEBASE_ROTATION_KP, Constants.DRIVEBASE_ROTATION_KI, Constants.DRIVEBASE_ROTATION_KD, "Rotation");
		rotationPID.setIDeadband(5);
		
		drivePID = new PID(Constants.DRIVEBASE_KP, Constants.DRIVEBASE_KI, Constants.DRIVEBASE_KD);
		drivePID.setConstantFeedForward(Constants.DRIVEBASE_FF);
	}

	public static void driveArcade(double power, double rotation)
	{
		if(Constants.VERBOSE >= 4) System.out.println("Power: " + power + "; Rotation: " + String.format("%.2f", rotation));
		if(reverseMode) power = -power;

		double leftPower = power + rotation;
		double rightPower = power - rotation;
		
		if(Constants.VERBOSE >= 4) System.out.println("Left Power: " + leftPower + "; Right Power: " + rightPower);
		driveTank(leftPower, rightPower);
	}

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

		if(reverseMode)
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

	public static void setDriveMode(int mode)
	{
		if(mode == Constants.REVERSE_MODE) reverseMode = true;
		else reverseMode = false;
	}

	public static void shift(boolean high)
	{
		if(high)
		{
			//shift high
		}
		else
		{
			//shift low
		}
	}

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

	public static double driveTo(double distance)
	{
		double error = distance - Sensors.getDriveDistance();
		double output = drivePID.pid(error);
		driveArcade(output, 0);
		return error;
	}
	
}
