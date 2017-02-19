package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Tracking
{
	private static Tracking instance;

	private static double angle, distance;

	private static int visionState;

	public static Tracking getInstance()
	{
		if(instance == null) instance = new Tracking();
		return instance;
	}

	private Tracking()
	{
		visionState = Constants.SEND_REQUEST;
	}

	public static int target(int mode, boolean autoFire)
	{
		String response;
		boolean tracked;
		switch(visionState)
		{
		case Constants.SEND_REQUEST:
			Turret.reset();
			if(mode == Constants.FUEL_MODE) Serial.sendData("1");
			else Serial.sendData("3");
			if(Constants.VERBOSE >= Constants.MID) System.out.println("Tracking: Sent Request");
			visionState = Constants.GET_RESPONSE;
			break;

		case Constants.GET_RESPONSE:
			response = Serial.getData();
			if(response != null)
			{
				if(response.contains("None"))
				{
					System.out.println("Targeting: Goal not found");
					visionState = Constants.SEND_REQUEST;
				}
				else
				{
					String[] strings = response.split(";");
					angle = Double.parseDouble(strings[0]);
					if(mode == Constants.GEAR_MODE) angle += Sensors.getAngle();
					SmartDashboard.putNumber("Angle", angle);
					if(Constants.VERBOSE >= Constants.MID) System.out.println("Tracking: Angle is " + angle + "°");
					if(mode == Constants.FUEL_MODE) visionState = Constants.TURN_TURRET_TO_TARGET;
					else if(mode == Constants.GEAR_MODE) visionState = Constants.ROTATE_DRIVEBASE;
				}
			}
			break;

		case Constants.TURN_TURRET_TO_TARGET:
			tracked = Turret.turnBy(angle, Constants.ACCEPTABLE_TURRET_ERROR);
			SmartDashboard.putBoolean("Turret Tracked", tracked);
			if(tracked)
			{
				if(Constants.VERBOSE >= Constants.LOW) System.out.println("Tracking: Done turning... Verifying angle.");
				Turret.reset();
				visionState = Constants.VERIFY_REQUEST;
			}
			break;
		
		case Constants.ROTATE_DRIVEBASE:
			tracked = DriveBase.turnTo(angle, Constants.ACCEPTABLE_GEAR_ERROR);
			SmartDashboard.putBoolean("Gear Tracked", tracked);
			if(tracked)
			{
				if(Constants.VERBOSE >= Constants.LOW) System.out.println("Tracking: Done turning drivebase... Verifying angle.");
				Turret.reset();
				visionState = Constants.VERIFY_REQUEST;
			}
			break;

			
		case Constants.VERIFY_REQUEST:
			if(mode == Constants.FUEL_MODE) Serial.sendData("1");
			else Serial.sendData("3");
			visionState = Constants.VERIFY;
			break;
			
		case Constants.VERIFY:
			response = Serial.getData();
			if(response != null)
			{
				if(response.contains("None"))
				{
					System.out.println("Targeting: Goal not found");
					visionState = Constants.VERIFY_REQUEST;
				}
				else
				{
					String[] strings = response.split(";");
					angle = Double.parseDouble(strings[0]);
					if(mode == Constants.FUEL_MODE)
					{
						if(Math.abs(angle) < Constants.ACCEPTABLE_FUEL_ERROR)
						{
							if(Constants.VERBOSE >= Constants.LOW) System.out.println("Tracking: Tracked. FIRE!");
							visionState = Constants.TRACKED_FUEL;
						}
						else resetState();
					}
					else
					{
						if(Math.abs(angle) < Constants.ACCEPTABLE_GEAR_ERROR + 2)
						{
							if(Constants.VERBOSE >= Constants.LOW) System.out.println("Tracking: Tracked Gear!");
							visionState = Constants.TRACKED_GEAR;
						}
						else resetState();
					}
				}
			}
			break;
			
		case Constants.TRACKED_FUEL:
			if(autoFire)
			{
				double targetRPM = Shooter.calcSpeed(distance);
				double error = Shooter.setRPM(targetRPM);
				if(error < 50) Shooter.fire();
			}
			break;
		}

		return visionState;

	}
	
	public static int target(int mode)
	{
		return target(mode, true);
	}
	

	public static void resetState()
	{
		visionState = Constants.SEND_REQUEST;
		Turret.reset();
		Shooter.stopFiring();
	}
	public static int getState()
	{
		return visionState;
	}
}
