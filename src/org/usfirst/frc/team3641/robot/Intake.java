package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

public class Intake
{
	private static Intake instance;
	private static Solenoid flapSolenoid;
	private static Spark leftSpark, rightSpark;
	
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
		leftSpark = new Spark(Constants.PWM.Sparks.INTAKE_LEFT);
		rightSpark = new Spark(Constants.PWM.Sparks.INTAKE_RIGHT);
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
		if(PDP.getCurrent(Constants.PDP.INTAKE_LEFT) > Constants.Intake.MAX_CURRENT_DRAW || PDP.getCurrent(Constants.PDP.INTAKE_RIGHT) > Constants.Intake.MAX_CURRENT_DRAW) speed = 0;
		else if(Gearbox.inPTOMode()) speed = 0;
		leftSpark.set(speed);
		rightSpark.set(speed);
		
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
