package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Spark;

public class Intake
{
	private static Intake instance;
	private static DoubleSolenoid intakeSolenoid;
	private static Spark left, right;
	
	public static Intake getInstance()
	{
		if(instance == null) instance = new Intake();
		return instance;
	}
	
	private Intake()
	{
		intakeSolenoid = new DoubleSolenoid(Constants.INTAKE_CHANNEL_FORWARD, Constants.INTAKE_CHANNEL_BACKWARDS);
		left = new Spark(Constants.INTAKE_LEFT_SPARK);
		right = new Spark(Constants.INTAKE_RIGHT_SPARK);
	}
	
	public static void intakeUp()
	{
		intakeSolenoid.set(DoubleSolenoid.Value.kForward);
	}
	
	public static void intakeDown()
	{
		intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
	}
	
	public static void set(double speed)
	{
		left.set(speed);
		right.set(-speed);
	}

}
