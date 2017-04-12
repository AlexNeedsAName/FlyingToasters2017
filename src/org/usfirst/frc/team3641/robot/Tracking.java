package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Tracking
{
	private static Tracking instance;
	
	public enum Mode
	{
		FUEL_MODE, GEAR_MODE, JUST_DISTANCE;
	}
	
	public enum State
	{
		SEND_REQUEST,
		GET_RESPONSE,
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
	 * Initializes the Tracking class.
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
			SubAuton.resetRotateBy();
			if(mode == Mode.GEAR_MODE) Serial.sendData("3");
			else Serial.sendData("1");
			Console.print("[Tracking] Sent Request for " + mode.toString().toLowerCase(), Constants.Verbosity.Level.MID);
			visionState = State.GET_RESPONSE;
			break;

		case GET_RESPONSE:
			response = Serial.getData();
			if(response != null)
			{
				if(response.contains("None"))
				{
					Console.print("[Tracking] Target not found", Constants.Verbosity.Level.MID);
					SmartDashboard.putString("Distance To Boiler", "Target Not Found");
					visionState = State.SEND_REQUEST;
				}
				else
				{
					String[] strings = response.split(";");
					try
					{
						angle = Double.parseDouble(strings[0]);
						distance = Double.parseDouble(strings[1]);
						
						Console.print("[Tracking] Angle is " + angle + "°", Constants.Verbosity.Level.MID);
						Console.print("[Tracking] Distance is " + distance + "m", Constants.Verbosity.Level.MID);
						if(mode == Mode.FUEL_MODE)
						{
							angle = -angle;
							visionState = State.ROTATE_DRIVEBASE;
							SmartDashboard.putString("Distance To Boiler", distance + "m");
						}
						else if(mode == Mode.GEAR_MODE)
						{
							double separation = Double.parseDouble(strings[1]);
							double dist = 0.1055 * Math.sin(Math.toRadians(180 - angle - Sensors.getAngle()))/Math.sin(Math.toRadians(separation));
							angle = Math.asin(dist * Math.sin(Math.toRadians(angle))) / Math.sqrt(Math.pow(0.3937,2) + Math.pow(dist, 2) - 2.0 * dist * 0.3937 * Math.cos(Math.toRadians(angle + 90)));
							visionState = State.ROTATE_DRIVEBASE;
						}
						else
						{
							SmartDashboard.putString("Distance To Boiler", distance + "m");
							visionState = State.SEND_REQUEST;
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
		
		case ROTATE_DRIVEBASE:
			Shooter.setRPM(Constants.Shooter.TARGET_RPM);
			tracked = Math.abs(SubAuton.rotateBy(angle)) < Constants.Thresholds.AUTON_DRIVE_ANGLE_ACCEPTABLE_ERROR;
			if(tracked)
			{
				Console.print("[Tracking] Done turning drivebase... Verifying angle.", Constants.Verbosity.Level.LOW);
				Turret.reset();
				visionState = State.VERIFY_REQUEST;
			}
			break;

			
		case VERIFY_REQUEST:
			SubAuton.resetRotateBy();
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
							angle = -angle;
							if(Math.abs(angle) < Constants.Thresholds.ACCEPTABLE_FUEL_ERROR)
							{
								Console.print("[Tracking] Final Error: " + angle);
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
				SubAuton.resetRotateBy();
				Shooter.fire(Constants.Shooter.TARGET_RPM);
			}
			break;
			
		case TRACKED_GEAR:
			break;
		}

		SmartDashboard.putString("Tracking State:", visionState.toString());
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
	
	public static State getState()
	{
		return visionState;
	}

	/**
	 * Reset tracking back to its inital state.
	 */
	public static void resetState()
	{
		SubAuton.resetRotateBy();
		visionState = State.SEND_REQUEST;
		Turret.reset();
		Shooter.set(0);
		Hopper.stopAgitating();
		SmartDashboard.putString("Tracking State:", "DISABLED");
	}	
}