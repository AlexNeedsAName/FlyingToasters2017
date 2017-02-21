package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Tracking
{
	private static Tracking instance;
	
	public enum Mode
	{
		FUEL_MODE, GEAR_MODE;
	}
	
	public enum State
	{
		SEND_REQUEST,
		GET_RESPONSE,
		TURN_TURRET_TO_TARGET,
		ROTATE_DRIVEBASE,
		TRACKED_GEAR,
		TRACKED_FUEL,
		VERIFY_REQUEST,
		VERIFY;
	}

	private static double angle, distance;

	private static State visionState;

	public static Tracking getInstance()
	{
		if(instance == null) instance = new Tracking();
		return instance;
	}

	/**
	 * Initalizes the Tracking class.
	 */
	private Tracking()
	{
		visionState = State.SEND_REQUEST;
	}

	/**
	 * Runs full auto targeting. Tracks, targets, and fires.
	 * 
	 * @param mode Gear mode or Fuel mode.
	 * @param autoFire True if you want it to fire when tracked. (In fuel mode)
	 * @return The current tracking state.
	 */
	public static State target(Mode mode, boolean autoFire) //TODO: Split up the contents of the switch/case to functions
	{
		String response;
		boolean tracked;
		switch(visionState)
		{
		case SEND_REQUEST:
			Turret.reset();
			if(mode == Mode.FUEL_MODE) Serial.sendData("1");
			else Serial.sendData("3");
			if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.MID)) System.out.println("Tracking: Sent Request");
			visionState = State.GET_RESPONSE;
			break;

		case GET_RESPONSE:
			response = Serial.getData();
			if(response != null)
			{
				if(response.contains("None"))
				{
					System.out.println("Targeting: Goal not found");
					visionState = State.SEND_REQUEST;
				}
				else
				{
					String[] strings = response.split(";");
					try
					{
						angle = Double.parseDouble(strings[0]);
						if(mode == Mode.GEAR_MODE) angle += Sensors.getAngle();
						SmartDashboard.putNumber("Angle", angle);
						if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.MID)) System.out.println("Tracking: Angle is " + angle + "°");
						if(mode == Mode.FUEL_MODE) visionState = State.TURN_TURRET_TO_TARGET;
						else if(mode == Mode.GEAR_MODE) visionState = State.ROTATE_DRIVEBASE;
					}
					catch(NumberFormatException e)
					{
						System.out.println("Targeting: Invalid String: " + strings[0]);
						visionState = State.SEND_REQUEST;
					}
				}
			}
			break;

		case TURN_TURRET_TO_TARGET:
			tracked = Turret.turnBy(angle, Constants.Thresholds.ACCEPTABLE_FUEL_ERROR);
			SmartDashboard.putBoolean("Turret Tracked", tracked);
			if(tracked)
			{
				if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.LOW)) System.out.println("Tracking: Done turning... Verifying angle.");
				Turret.reset();
				visionState = State.VERIFY_REQUEST;
			}
			break;
		
		case ROTATE_DRIVEBASE:
			tracked = DriveBase.turnTo(angle, Constants.Thresholds.ACCEPTABLE_TURRET_ERROR);
			SmartDashboard.putBoolean("Gear Tracked", tracked);
			if(tracked)
			{
				if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.LOW)) System.out.println("Tracking: Done turning drivebase... Verifying angle.");
				Turret.reset();
				visionState = State.VERIFY_REQUEST;
			}
			break;

			
		case VERIFY_REQUEST:
			if(mode == Mode.FUEL_MODE) Serial.sendData("1");
			else Serial.sendData("3");
			visionState = State.VERIFY;
			break;
			
		case VERIFY:
			response = Serial.getData();
			if(response != null)
			{
				if(response.contains("None"))
				{
					System.out.println("Targeting: Goal not found");
					visionState = State.VERIFY_REQUEST;
				}
				else
				{
					String[] strings = response.split(";");
					try
					{
						angle = Double.parseDouble(strings[0]);
						if(mode == Mode.FUEL_MODE)
						{
							if(Math.abs(angle) < Constants.Thresholds.ACCEPTABLE_FUEL_ERROR)
							{
								if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.LOW)) System.out.println("Tracking: Tracked. FIRE!");
								visionState = State.TRACKED_FUEL;
							}
							else resetState();
						}
						else
						{
							if(Math.abs(angle) < Constants.Thresholds.ACCEPTABLE_GEAR_ERROR + 2)
							{
								if(Constants.Verbosity.isAbove(Constants.Verbosity.Level.LOW)) System.out.println("Tracking: Tracked Gear!");
								visionState = State.TRACKED_GEAR;
							}
							else resetState();
						}
					}
					catch(NumberFormatException e)
					{
						System.out.println("Targeting: Invalid String: " + strings[0]);
						visionState = State.SEND_REQUEST;
					}
				}
			}
			break;
			
		case TRACKED_FUEL:
			if(autoFire)
			{
				double targetRPM = Shooter.calcSpeed(distance);
				double error = Shooter.setRPM(targetRPM);
				if(error < 50) Shooter.fire();
			}
			break;
			
		case TRACKED_GEAR:
			break;
		}

		return visionState;

	}
	
	/**
	 * Runs auto targeting. Tracks and targets.
	 * 
	 * @param mode Gear mode or Fuel mode.
	 * @return The current tracking state.
	 */
	public static State target(Mode mode)
	{
		return target(mode, true);
	}

	/**
	 * Reset tracking back to its inital state.
	 */
	public static void resetState()
	{
		visionState = State.SEND_REQUEST;
		Turret.reset();
		Shooter.stopFiring();
	}	
}