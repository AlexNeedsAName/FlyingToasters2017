package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;

public class Intake
{
	private static Intake instance;
	private static DoubleSolenoid intakeSolenoid;
	private static Spark left, right;
	private static boolean up = true;
	
	public static Intake getInstance()
	{
		if(instance == null) instance = new Intake();
		return instance;
	}
	
	/**
	 * Initialize the intake, its sparks and its solenoids.
	 */
	private Intake()
	{
		intakeSolenoid = new DoubleSolenoid(Constants.INTAKE_CHANNEL_FORWARD, Constants.INTAKE_CHANNEL_REVERSE);
		left = new Spark(Constants.INTAKE_LEFT_SPARK);
		right = new Spark(Constants.INTAKE_RIGHT_SPARK);
	}
	
	/**
	 * Raise the intake.
	 */
	public static void intakeUp()
	{
		if(!up)
		{
			if(Constants.VERBOSE >= Constants.MID) System.out.println("Intake up");
			up = true;
		}
		intakeSolenoid.set(DoubleSolenoid.Value.kForward);
	}
	
	/**
	 * Lower the intake.
	 */
	public static void intakeDown()
	{
		if(up)
		{
			if(Constants.VERBOSE >= Constants.MID) System.out.println("Intake down");
			up = false;
		}
		intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
	}
	
	/**
	 * Set the intake roller speed.
	 * @param speed The speed you want to set the intake to.
	 */
	public static void set(double speed)
	{
		left.set(speed);
		right.set(-speed);
	}

}
