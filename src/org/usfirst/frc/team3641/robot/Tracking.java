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
			Console.print("[Tracking] Sent Request", Constants.Verbosity.Level.MID);
			visionState = State.GET_RESPONSE;
			break;

		case GET_RESPONSE:
			response = Serial.getData();
			if(response != null)
			{
				if(response.contains("None"))
				{
					Console.print("[Tracking] Target not found", Constants.Verbosity.Level.MID);
					visionState = State.SEND_REQUEST;
				}
				else
				{
					String[] strings = response.split(";");
					try
					{
						angle = Double.parseDouble(strings[0]);
						if(mode == Mode.GEAR_MODE) angle += Sensors.getAngle();
						Console.print("[Tracking] Angle is " + angle + "°", Constants.Verbosity.Level.MID);
						if(mode == Mode.FUEL_MODE) visionState = State.TURN_TURRET_TO_TARGET;
						else if(mode == Mode.GEAR_MODE)
						{
							double separation = Double.parseDouble(strings[1]);
							double dist = 0.1055 * Math.sin(Math.toRadians(180 - angle - Sensors.getAngle()))/Math.sin(Math.toRadians(separation));
							angle = Math.asin(dist * Math.sin(Math.toRadians(angle))) / Math.sqrt(Math.pow(0.3937,2) + Math.pow(dist, 2) - 2.0 * dist * 0.3937 * Math.cos(Math.toRadians(angle + 90)));
							visionState = State.ROTATE_DRIVEBASE;
						}
					}
					catch(NumberFormatException e)
					{
						Console.print("[Tracking] Invalid String: " + strings[0], Constants.Verbosity.Level.LOW);
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
				Console.print("[Tracking] Done turning... Verifying angle.", Constants.Verbosity.Level.LOW);
				Turret.reset();
				visionState = State.VERIFY_REQUEST;
			}
			break;
		
		case ROTATE_DRIVEBASE:
			tracked = DriveBase.turnTo(angle, Constants.Thresholds.ACCEPTABLE_TURRET_ERROR);
			SmartDashboard.putBoolean("Gear Tracked", tracked);
			if(tracked)
			{
				Console.print("[Tracking] Done turning drivebase... Verifying angle.", Constants.Verbosity.Level.LOW);
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
					Console.print("[Tracking] Goal not found", Constants.Verbosity.Level.MID);
					visionState = State.VERIFY_REQUEST;
				}
				else
				{
					String[] strings = response.split(";");
					try
					{
						angle = Double.parseDouble(strings[0]);
						distance = Double.parseDouble(strings[1]);
						if(mode == Mode.FUEL_MODE)
						{
							if(Math.abs(angle) < Constants.Thresholds.ACCEPTABLE_FUEL_ERROR)
							{
								Console.print("[Tracking] Tracked. FIRE!", Constants.Verbosity.Level.LOW);
								visionState = State.TRACKED_FUEL;
							}
							else resetState();
						}
						else
						{
							if(Math.abs(angle) < Constants.Thresholds.ACCEPTABLE_GEAR_ERROR + 2)
							{
								Console.print("[Tracking] Tracked Gear!", Constants.Verbosity.Level.LOW);
								visionState = State.TRACKED_GEAR;
							}
							else resetState();
						}
					}
					catch(NumberFormatException e)
					{
						Console.print("[Tracking]: Invalid String: " + strings[0], Constants.Verbosity.Level.MID);
						visionState = State.SEND_REQUEST;
					}
				}
			}
			break;
			
		case TRACKED_FUEL:
			if(autoFire)
			{
				Shooter.setDistance(distance);
				Shooter.fire();
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