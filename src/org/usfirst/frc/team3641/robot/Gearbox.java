package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Gearbox
{
	private static Gearbox instance;
	private static DoubleSolenoid leftShifter, rightShifter, leftPTO, rightPTO;
	private static boolean inPTOMode = false;

	public static Gearbox getInstance()
	{
		if(instance == null) instance = new Gearbox();
		return instance;
	}

	private Gearbox()
	{
		if(!Constants.runningAleksBot)
		{
			leftShifter = new DoubleSolenoid(Constants.LEFT_SHIFTER_CHANNEL_FORWARD, Constants.LEFT_SHIFTER_CHANNEL_BACKWARDS);
			rightShifter = new DoubleSolenoid(Constants.RIGHT_SHIFTER_CHANNEL_FORWARD, Constants.RIGHT_SHIFTER_CHANNEL_BACKWARDS);
		}
	}

	public static void shiftHigh()
	{
		if(!Constants.runningAleksBot)
		{
			leftShifter.set(DoubleSolenoid.Value.kForward);
			rightShifter.set(DoubleSolenoid.Value.kForward);
		}
	}

	public static void shiftLow()
	{
		if(!Constants.runningAleksBot)
		{
			leftShifter.set(DoubleSolenoid.Value.kReverse);
			rightShifter.set(DoubleSolenoid.Value.kReverse);
		}
	}
	
	public static void togglePTO()
	{
		if(!Constants.runningAleksBot) setPTO(!inPTOMode);
	}
	
	public static void setPTO(boolean on)
	{
		inPTOMode = on;
		if(on)
		{
			leftPTO.set(DoubleSolenoid.Value.kForward);
			rightPTO.set(DoubleSolenoid.Value.kForward);
		}
		else
		{
			leftPTO.set(DoubleSolenoid.Value.kReverse);
			rightPTO.set(DoubleSolenoid.Value.kReverse);
		}
	}

}
