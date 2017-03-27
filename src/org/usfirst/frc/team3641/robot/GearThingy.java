package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;

public class GearThingy
{
	private static GearThingy instance;
	private static Solenoid actuator;
	private static Spark wheelSpark;
	
	private static boolean alreadyRunningState;
	private static State currentState;
	private static Timer stateTimer;
		
	public static GearThingy getInstance()
	{
		if(instance == null) instance = new GearThingy();
		return instance;
	}
	
	public static enum State
	{
		RESTING,
		INTAKING,
		DONE_INTAKING,
		PLACING,
		EJECT_GEAR,
		BACK_AWAY;
	}
	
	private GearThingy()
	{
		actuator = new Solenoid(Constants.Pnumatics.GEAR_THINGY);
		wheelSpark = new Spark(Constants.PWM.Sparks.GEAR_WHEELS);
		stateTimer = new Timer();
		alreadyRunningState = false;
		currentState = State.RESTING;
	}
	
	/**
	 * Extends the gear placer/intake with pneumatics.
	 */
	private static void setDown()
	{
		Console.print("Extending Gear Thingy", Constants.Verbosity.Level.LOW);
		actuator.set(true);
	}
	
	/**
	 * Retracts the gear placer/intake with pneumatics.
	 */
	private static void setUp()
	{
		Console.print("Retracting Gear Thingy", Constants.Verbosity.Level.LOW);
		actuator.set(false);
	}
	
	private static void intake()
	{
		wheelSpark.set(1);
	}
	
	private static void stopWheels()
	{
		wheelSpark.set(0);
	}
	
	private static void eject()
	{
		wheelSpark.set(-1);
	}
	
	public static State runCurrentState()
	{
		switch(currentState)
		{
		case RESTING:
			if(!alreadyRunningState)
			{
				setUp();
				stopWheels();
			}
			break;
			
		case INTAKING:
			if(!alreadyRunningState)
			{
				setDown();
				intake();
			}
			break;
			
		case DONE_INTAKING:
			if(!alreadyRunningState)
			{
				setUp();
				intake();
			}
			if(stateTimer.get() >= 0.5) setState(State.RESTING);
			break;
			
		case PLACING:
			if(!alreadyRunningState)
			{
				setDown();
				stopWheels();
			}
			if(stateTimer.get() >= 0.5) setState(State.EJECT_GEAR);
			break;
			
		case EJECT_GEAR:
			if(!alreadyRunningState)
			{
				setDown();
				eject();
			}
			if(stateTimer.get() >= 0.5) setState(State.BACK_AWAY);
			break;
			
		case BACK_AWAY:
			if(!alreadyRunningState)
			{
				SubAuton.resetDriveBy();
			}
			SubAuton.driveBy(0.5);
			break;
		}
		return currentState;
	}
	
	public static void setState(State newState)
	{
		currentState = newState;
		alreadyRunningState = false;
		SubAuton.resetDriveBy();
		stateTimer.reset();
		stateTimer.start();
	}

}
