package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;

public class Intake
{
	private static Intake instance;
	private static DoubleSolenoid intakeSolenoid, flapSolenoid;
	private static Spark intakeSpark;
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
		intakeSolenoid = new DoubleSolenoid(Constants.Pnumatics.INTAKE_FORWARD, Constants.Pnumatics.INTAKE_REVERSE);
		flapSolenoid = new DoubleSolenoid(Constants.Pnumatics.FLAP_FORWARD, Constants.Pnumatics.FLAP_REVERSE);
		intakeSpark = new Spark(Constants.PWM.Sparks.INTAKE);
	}
	
	/**
	 * Raise the intake.
	 */
	public static void intakeUp()
	{
		if(!up)
		{
			if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.MID)) System.out.println("Intake up");
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
			if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.MID)) System.out.println("Intake down");
			up = false;
		}
		intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
	}
	
	/**
	 * Put down.
	 */
	public static void setFlapDown()
	{
		flapSolenoid.set(DoubleSolenoid.Value.kForward);
	}
	
	/**
	 * Put up.
	 */
	public static void setFlapUp()
	{
		flapSolenoid.set(DoubleSolenoid.Value.kReverse);
	}

	/**
	 * Set the intake roller speed.
	 * @param speed The speed you want to set the intake to.
	 */
	public static void setSpeed(double speed)
	{
		intakeSpark.set(speed);
	}

}
