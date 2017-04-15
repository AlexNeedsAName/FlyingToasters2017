package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

public class Intake
{
	private static Intake instance;
	private static Solenoid flapSolenoid;
	private static Spark intakeSpark;
	
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
		flapSolenoid = new Solenoid(Constants.Pnumatics.FLAP);
		intakeSpark = new Spark(Constants.PWM.Sparks.INTAKE);
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
	
	public static void runClimber()
	{
		setSpeed(.25);
	}

}
