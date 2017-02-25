package org.usfirst.frc.team3641.robot;
import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Spark;

public class Shooter
{
	private static Shooter instance;
	public static CANTalon left, right;
	private static Spark elevator;
	private static PID flywheelPID;
	private static double error;

	public static Shooter getInstance()
	{
		if(instance == null) instance = new Shooter();
		return instance;
	}

	/**
	 * Initalize the shooter and its motor controllers.
	 */
	public Shooter()
	{
		left = new CANTalon(Constants.CAN.Talons.SHOOTER_LEFT);
		right = new CANTalon(Constants.CAN.Talons.SHOOTER_RIGHT);
		right.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
		elevator = new Spark(Constants.PWM.Sparks.SHOOTER_ELEVATOR);
		
		flywheelPID = new PID("ShooterFlywheel");
		flywheelPID.setBackupValues(Constants.PID.SHOOTER_KP, Constants.PID.SHOOTER_KI, Constants.PID.SHOOTER_KD, Constants.PID.SHOOTER_FF, PID.PROPORTIONAL);
		flywheelPID.readConfig();
	}

	
	/**
	 * Set the shooter speed *automagically* based on the distance to the target
	 * 
	 * @param distance The distance to your target.
	 * @return The error in RPM.
	 */
	public static double setDistance(double distance)
	{
		double x = distance;
		double y = Constants.Shooter.TARGET_HEIGHT-Constants.Shooter.HEIGHT;
		double θ = Constants.Shooter.ANGLE;
		double g = Constants.Shooter.GRAVITY + Constants.Shooter.LIFT;
		double v = calcVelocity(x,y,θ,g);
		return setSpeed(v);
	}
	
	/**
	 * Calculate the ideal velocity of a projective given a distance, a height, an angle, and gravity.
	 * 
	 * @param x The distance to the target.
	 * @param y The height to the target.
	 * @param θ The launch angle.
	 * @param g The y acceleration. Normally gravity, but any lift from the backspin would be added here too.
	 * @return The ideal velocity to hit the target.
	 */
	public static double calcVelocity(double x, double y, double θ, double g)
	{
		θ = Math.toRadians(θ);
		return Math.sqrt( (g*x*x) / (2*Math.cos(θ)*Math.cos(θ) * (y-(x*Math.tan(θ))) ) );
		/* 
		 * https://www.desmos.com/calculator/zzrzc66pur
		 * This took way too long. It's derived from the kinematic equations. We solved
		 * x for time, then plugged it into the y equation and solved for velocity.
		 */
	}

	/**
	 * Calculate the ideal speed of the shooter based off a given distance.
	 * 
	 * @param speed The target speed in m/s.
	 * @return The current error in RPM.
	 */
	public static double setSpeed(double speed)
	{
		return setRPM(speed * Constants.Conversions.SPEED_TO_RPM);
	}
	
	/**
	 * Target a specific speed for the shooter flywheels.
	 * 
	 * @param target The target speed in RPM.
	 * @return The current error in RPM.
	 */
	public static double setRPM(double target)
	{
		SmartDashboard.putNumber("Target RPM", target);
		double current = Sensors.getShooterRPM();
		error = target - current;
		double output = flywheelPID.run(error, target);
		set(output);
		return error;
	}

	/**
	 * Set the raw power of the shooter flywheels.
	 * 
	 * @param power The power of the shooter flywheel motors.
	 */
	public static void set(double power)
	{
		if(power > 1) power = 1;
		SmartDashboard.putNumber("Power Out", power);
		SmartDashboard.putNumber("RPM", Sensors.getShooterRPM());
		SmartDashboard.putNumber("RPM over Time", Sensors.getShooterRPM());
		right.set(power);
		left.set(-power);
	}

	/**
	 * Stop spinning the flywheels
	 */
	public static void reset()
	{
		if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.LOW)) System.out.println("Reset Shooter");
		SmartDashboard.putNumber("Target RPM", 0);
		set(0);
		error = 0;
		flywheelPID.reset();
	}

	/**
	 * If the flywheel speed is with the error threshold, shoot.
	 */
	public static void fire()
	{
		if(Math.abs(error) < Constants.Thresholds.SHOOTER_MAX_ERROR) forceFire();
		else elevator.set(0);

	}
	
	/**
	 * Shoot regardless of the current flywheel speed.
	 */
	public static void forceFire()
	{
		elevator.set(1);
		Hopper.adjatate();
	}
	
	/**
	 * Stop firing the shooter.
	 * 
	 * Stops running the hopper and the elevator, but not the flywheel. Intended for pausing
	 * while the PID corrects the RPM of the shooter.
	 */
	public static void stopFiring()
	{
		if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.MID)) System.out.println("Stopped Firing Shooter");
		elevator.set(0);
		Hopper.stopAdjatating();
	}
}
