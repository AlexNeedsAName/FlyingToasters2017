package org.usfirst.frc.team3641.robot;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Victor;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

public class DriveBase
{
	private static DriveBase instance;
	private static CANTalon left, leftSlave, right, rightSlave;
	private static Victor PWMleft, PWMleftSlave, PWMright, PWMrightSlave;
	private static AHRS gyro;
	private static ADXRS450_Gyro SPIgyro;
	private static double angle;
	private static boolean highGear = false;
	private static boolean autoShift = false;
	
	private static boolean reverseMode;
	
	public static DriveBase getInstance()
	{
		if(instance == null) instance = new DriveBase();
		return instance;
	}
	
	private DriveBase()
	{
		gyro = new AHRS(SerialPort.Port.kMXP);
		if(Constants.runningAleksBot)
		{
			PWMleft = new Victor(Constants.LEFT_VICTOR);
			PWMleftSlave = new Victor(Constants.LEFT_SLAVE_VICTOR);
			PWMright = new Victor(Constants.RIGHT_VICTOR);
			PWMrightSlave = new Victor(Constants.RIGHT_SLAVE_VICTOR);
			
			SPIgyro = new ADXRS450_Gyro();
		}
		left = new CANTalon(Constants.DRIVEBASE_LEFT_TALON);
		leftSlave = new CANTalon(Constants.DRIVEBASE_LEFT_SLAVE_TALON);
		right = new CANTalon(Constants.DRIVEBASE_RIGHT_TALON);
		rightSlave = new CANTalon(Constants.DRIVEBASE_RIGHT_SLAVE_TALON);
	}
	
	public static void driveArcade(double power, double rotation)
	{
		if(reverseMode) power = -power;
		
		double leftPower = power + rotation;
		double rightPower = power - rotation;
		
		double maxPower;
		if(leftPower > rightPower) maxPower = leftPower;
		else maxPower = rightPower;
		
		if(maxPower > 1)
		{
			leftPower/= maxPower;
			rightPower/= maxPower;
		}
		
		setMotors(leftPower, rightPower);
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
		
		if(reverseMode) setMotors(-leftPower, -rightPower);
		else setMotors(leftPower, rightPower);
	}
	
	public static void setMotors(double leftPower, double rightPower)
	{
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
			right.set(rightPower);
			rightSlave.set(rightPower);
		}
	}
	
	public static void readGyro()
	{
		if(Constants.runningAleksBot)
		{
			angle = SPIgyro.getAngle();
		}
		else
		{
			angle = gyro.getAngle();
		}
	}
	
	public static double getAngle()
	{
		return angle;
	}
	
	public static void resetGyro()
	{
		if(Constants.runningAleksBot)
		{
			SPIgyro.reset();
		}
		else
		{
			gyro.reset();
		}
	}
	
	public static void setDriveMode(int mode)
	{
		if(mode == Constants.REVERSE_MODE) reverseMode = true;
		else reverseMode = false;
	}
	
	public static void setGear(int mode)
	{
		if(mode == Constants.HIGH) highGear = true;
		else highGear = false;
	}
	
	public static void setAutoShift(boolean setting)
	{
		autoShift = false;
	}

	public static double readEncoder()
	{
		//TODO: Add encoder
		return 0;
	}
	
	public static void turnDegrees(double inputDegrees, double errorMargin) 
	{
		resetGyro();
		readGyro();
		double gyroAngle = getAngle();
        
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
    		readGyro();
    		gyroAngle = getAngle();
        }
        driveArcade(0, 0);
	}
	
}
