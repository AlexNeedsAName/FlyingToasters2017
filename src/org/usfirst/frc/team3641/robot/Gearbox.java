package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Gearbox
{
	private static Gearbox instance;
	private static DoubleSolenoid leftShifter, rightShifter;
	
	public static Gearbox getInstance()
	{
		if(instance == null) instance = new Gearbox();
		return instance;
	}
	
	private Gearbox()
	{
		leftShifter = new DoubleSolenoid(Constants.LEFT_SHIFTER_CHANNEL_FORWARD, Constants.LEFT_SHIFTER_CHANNEL_BACKWARDS);
		rightShifter = new DoubleSolenoid(Constants.RIGHT_SHIFTER_CHANNEL_FORWARD, Constants.RIGHT_SHIFTER_CHANNEL_BACKWARDS);
	}
	
	public static void shiftHigh()
	{
		leftShifter.set(DoubleSolenoid.Value.kForward);
		rightShifter.set(DoubleSolenoid.Value.kForward);
	}
	
	public static void shiftLow()
	{
		leftShifter.set(DoubleSolenoid.Value.kReverse);
		rightShifter.set(DoubleSolenoid.Value.kReverse);
	}
	
}
