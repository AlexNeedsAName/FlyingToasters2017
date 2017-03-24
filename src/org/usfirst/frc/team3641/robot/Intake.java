package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

public class Intake
{
	private static Intake instance;
	private static Solenoid intakeSolenoid, flapSolenoid;
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
		intakeSolenoid = new Solenoid(Constants.Pnumatics.INTAKE);
		flapSolenoid = new Solenoid(Constants.Pnumatics.FLAP);
		intakeSpark = new Spark(Constants.PWM.Sparks.INTAKE);
	}
	
	/**
	 * Raise the intake.
	 */
	public static void intakeUp()
	{
		if(!up)
		{
			Console.print("Intake up", Constants.Verbosity.Level.LOW);
			up = true;
		}
		intakeSolenoid.set(false);
	}
	
	/**
	 * Lower the intake.
	 */
	public static void intakeDown()
	{
		if(up)
		{
			Console.print("Intake down", Constants.Verbosity.Level.LOW);
			up = false;
		}
		intakeSolenoid.set(true);
	}
	
	public static void toggleIntake()
	{
		if(up) intakeDown();
		else intakeUp();
	}
	
	/**
	 * Put down.
	 */
	public static void setFlapDown()
	{
		flapSolenoid.set(false);
	}
	
	/**
	 * Put up.
	 */
	public static void setFlapUp()
	{
		flapSolenoid.set(true);
	}

	/**
	 * Set the intake roller speed.
	 * @param speed The speed you want to set the intake to.
	 */
	public static void setSpeed(double speed)
	{
		if(Gearbox.inPTOMode()) speed = 0;
		intakeSpark.set(speed);
	}
	
	public static void eject()
	{
		setSpeed(-1);
	}

}
