package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
		BACK_AWAY,
		JUST_DOWN,
		JUST_INTAKE,
		JUST_EJECT;
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
		actuator.set(true);
	}
	
	/**
	 * Retracts the gear placer/intake with pneumatics.
	 */
	private static void setUp()
	{
		actuator.set(false);
	}
	
	private static void intake()
	{
		wheelSpark.set(.625);
	}
	
	private static void stopWheels()
	{
		wheelSpark.set(0);
	}
	
	private static void eject()
	{
		wheelSpark.set(-.75);
	}
	
	public static State runCurrentState()
	{
		switch(currentState)
		{
		case RESTING:
			if(!alreadyRunningState)
			{
				SubAuton.resetDriveBy();
				alreadyRunningState = true;
			}
			setUp();
			stopWheels();
			break;
			
		case INTAKING:
			setDown();
			intake();
			break;
			
		case DONE_INTAKING:
			setUp();
			intake();
			if(stateTimer.get() >= .75) setState(State.RESTING);
			break;
			
		case PLACING:
			setDown();
			stopWheels();
			if(stateTimer.get() >= 0.5) setState(State.EJECT_GEAR);
			break;
			
		case EJECT_GEAR:
			setDown();
			eject();
			if(stateTimer.get() >= 0.25) setState(State.BACK_AWAY);
			break;
			
		case BACK_AWAY:
			if(!alreadyRunningState)
			{
				SubAuton.resetDriveBy();
				alreadyRunningState = true;
			}
			intake();
			double error = SubAuton.driveBy(.75);
			Console.print("Backup Error: " + error);
			if(Math.abs(error) < 0.1) setState(State.RESTING);
			break;
			
		case JUST_INTAKE:
			setUp();
			intake();
			break;
			
		case JUST_EJECT:
			setUp();
			eject();
			break;
			
		case JUST_DOWN:
			setDown();
			stopWheels();
			break;
		}
		SmartDashboard.putString("GearState", currentState.toString());
		return currentState;
	}
	
	public static void setState(State newState)
	{
		Console.print("Set gear mechanism to " + newState.toString());
		currentState = newState;
		alreadyRunningState = false;
		SubAuton.resetDriveBy();
		stateTimer.reset();
		stateTimer.start();
	}

}
