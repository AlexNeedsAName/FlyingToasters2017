package org.usfirst.frc.team3641.robot;
import org.usfirst.frc.team3641.robot.Constants.PWM;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveBase
{
	private static DriveBase instance;
	public static LinkedTalons left, right;
	private static Victor PWMleft, PWMleftSlave, PWMright, PWMrightSlave;
	private static PID encoderCorrectionPID, driveDistancePID, driveRotationPID, lockPID;
	private static double lockTarget;
	private static boolean locked;
	private static boolean inClimbingMode;
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
	 * A class for mechanically linked Talons.
	 */
	public class LinkedTalons
	{
		private int numberOfTalons;
		private CANTalon[] talons;
		private CANTalon feedbackTalon = null;
		
		/**
		 * Creates a new set of linked talons.
		 * 
		 * @param talonIDs Each of the IDs you want to control.
		 */
		public LinkedTalons(int... talonIDs)
		{
			numberOfTalons = talonIDs.length;
			talons = new CANTalon[numberOfTalons];

			for(int i = 0; i<numberOfTalons; i++) talons[i] = new CANTalon(talonIDs[i]);
		}
		
		/**
		 * Set output power of all of the talons.
		 * 
		 * @param power The power to set each of the talons to.
		 */
		public void set(double power)
		{
			for(CANTalon talon : talons) talon.set(power);
		}
		
		/**
		 * Add a feedback device.
		 * 
		 * @param TalonID The ID of the talon you connect the feedback device to.
		 * @param device The type of feedback device to use.
		 */
		public void setFeedbackDevice(int TalonID, FeedbackDevice device)
		{
			feedbackTalon = null;
			for(CANTalon talon : talons) if(talon.getDeviceID() == TalonID) feedbackTalon = talon;
			if(feedbackTalon == null) throw new IllegalArgumentException("There is not Talon with an ID of " + TalonID + " in this object.");
			else feedbackTalon.setFeedbackDevice(device);
		}
		
		/**
		 * Get the encoder position from the feedback talon.
		 * 
		 * @return The encoder position in ticks.
		 */
		public int getEncPosition()
		{
			return feedbackTalon.getEncPosition();
		}
		
		/**
		 * Set the current position.
		 * 
		 * @param position The number of ticks to set the encoder to.
		 */
		public void setEncPosition(int position)
		{
			feedbackTalon.setEncPosition(position);
		}
		
		/**
		 * Turns on or off break mode for all the talons.
		 * 
		 * @param on True for on, false for off.
		 */
		public void setBreakMode(boolean on)
		{
			for(CANTalon talon : talons) talon.enableBrakeMode(on);
		}
	}
		
	/**
	 * Initializes the Drive Base with its Talon and PID classes.
	 */
	private DriveBase()
	{
		inClimbingMode = false;
		
		if(Constants.runningAleksBot)
		{
			PWMleft = new Victor(PWM.Victors.LEFT);
			PWMleftSlave = new Victor(PWM.Victors.LEFT_SLAVE);
			PWMright = new Victor(Constants.PWM.Victors.RIGHT);
			PWMrightSlave = new Victor(Constants.PWM.Victors.RIGHT_SLAVE);
		}
		
		left = new LinkedTalons(Constants.CAN.Talons.DRIVEBASE_LEFT_1, Constants.CAN.Talons.DRIVEBASE_LEFT_2, Constants.CAN.Talons.DRIVEBASE_LEFT_3);
		left.setFeedbackDevice(Constants.CAN.Talons.LEFT_ENCODER_TALON, FeedbackDevice.CtreMagEncoder_Absolute);
		
		right = new LinkedTalons(Constants.CAN.Talons.DRIVEBASE_RIGHT_1, Constants.CAN.Talons.DRIVEBASE_RIGHT_2, Constants.CAN.Talons.DRIVEBASE_RIGHT_3);
		right.setFeedbackDevice(Constants.CAN.Talons.RIGHT_ENCODER_TALON, FeedbackDevice.CtreMagEncoder_Absolute);

		encoderCorrectionPID = new PID("DriveBase Correction");
		encoderCorrectionPID.setBackupValues(Constants.PID.DRIVEBASE_CORRECTION_KP, Constants.PID.DRIVEBASE_CORRECTION_KI, Constants.PID.DRIVEBASE_CORRECTION_KD);
		encoderCorrectionPID.readConfig();
		
		driveDistancePID = new PID("DriveBase");
		driveDistancePID.setBackupValues(Constants.PID.DRIVEBASE_KP, Constants.PID.DRIVEBASE_KI, Constants.PID.DRIVEBASE_KD, Constants.PID.DRIVEBASE_DEADBAND);
		driveDistancePID.readConfig();
		
		driveRotationPID = new PID("DriveBase Rotation");
		driveRotationPID.setBackupValues(Constants.PID.DRIVEBASE_ROTATION_KP, Constants.PID.DRIVEBASE_ROTATION_KI, Constants.PID.DRIVEBASE_ROTATION_KP, Constants.PID.DRIVEBASE_ROTATION_DEADBAND);
		driveRotationPID.readConfig();
		
		lockPID = new PID("LockPTO");
		lockPID.readConfig();
		
		squaredRotation = false;
		squaredPower = false;
	}

	/**
	 * Drives the robot with arcade drive.
	 * 
	 * @param power The speed forwards/backwards
	 * @param rotation The rotation clockwise/counterclockwise
	 */
	public static void driveArcade(double power, double rotation)
	{
		if(inClimbingMode)
		{
			if(Gearbox.inPTOMode())
			{
				if(Math.abs(rotation) >= .25 && Math.abs(power) <= .25) Gearbox.setPTO(false);
			}
			else if(Math.abs(power) >= .25) Gearbox.setPTO(true);
		}
		else if(Gearbox.inPTOMode()) Gearbox.setPTO(false);
	
		if(Gearbox.inPTOMode()) rotation = 0;
		else
		{
			if(mode == DriveMode.REVERSE) rotation = -rotation;
			if(squaredRotation && rotation != 0) rotation = rotation*rotation*Math.abs(rotation)/rotation;
		}
		
		if(squaredPower && power != 0) power = power*power*Math.abs(power)/power;
				
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
		
		Console.print("Left Power: " + leftPower + "; Right Power: " + rightPower, Constants.Verbosity.Level.INSANITY);

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
			right.set(-rightPower);
		}
	}

	/**
	 * Turns break mode on or off.
	 * 
	 * @param on True if you want it on, false if you want it off.
	 */
	public static void setBreakMode(boolean on)
	{
		left.setBreakMode(on);
		right.setBreakMode(on);
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
	 * Toggle squared rotation.
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
	 * Switch between normal and reverse mode.
	 * 
	 * @param Mode DriveMode you want to switch to.
	 */
	public static void setDriveMode(DriveMode Mode)
	{
		if(mode != Mode)
		{
			mode = Mode;
			Console.print("Switching to " + mode.toString() + " mode.");
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
	public static double turnTo(double targetAngle)
	{
		double error = Coords.calcAngleError(targetAngle, Sensors.getAngle());
		driveArcade(0, driveRotationPID.run(error));
		return error;
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
		double error = distance - Sensors.getLeftDriveDistance();
		double output = driveDistancePID.run(error);
		driveArcade(output, 0);
		return error;
	}
	
	public static double driveStraightTo(double distance, double initialDifference)
	{
		double distanceError = distance - Sensors.getLeftDriveDistance();
		double speed = driveDistancePID.run(distanceError);
		
		double difference = Sensors.getLeftDriveDistance() - Sensors.getRightDriveDistance();
		double rotationError = initialDifference - difference;
		double rotation = encoderCorrectionPID.run(rotationError);
		
		driveArcade(speed, rotation);
		
		return distanceError;
	}
			
	/**
	 * Resets all of the drivebase PIDs
	 */
	public static void resetPID()
	{
		driveDistancePID.reset();
		driveRotationPID.reset();
	}
	
	/**
	 * Check if the PTO is locked.
	 * 
	 * @return True if locked.
	 */
	public static boolean isLocked()
	{
		return locked;
	}
	
	/**
	 * Run the lock PID loop.
	 */
	public static void runLock()
	{
		double error = lockTarget - Sensors.getLeftDriveDistance();
		double power = lockPID.run(error, lockTarget);
		driveArcade(power,0);
	}
	
	/**
	 * Lock the PTO.
	 */
	public static void lockDrivebase()
	{
		locked = true;
		lockTarget = Sensors.getLeftDriveDistance();
		Console.print("Lock Target: " + lockTarget, Constants.Verbosity.Level.LOW);
	}
	
	/**
	 * Unlock the PTO lock.
	 */
	public static void unlockDrivebase()
	{
		locked = false;
	}
	
	/**
	 * Toggle the PTO Lock
	 */
	public static void toggleLock()
	{
		if(!locked) lockDrivebase();
		else unlockDrivebase();
	}
	
	/**
	 * Turns on climbing mode.
	 */
	public static void enableClimbingMode()
	{
		inClimbingMode = true;
	}
	
	/**
	 * Turns off climbing mode.
	 */
	public static void disableClimbingMode()
	{
		inClimbingMode = false;
	}
	
}
