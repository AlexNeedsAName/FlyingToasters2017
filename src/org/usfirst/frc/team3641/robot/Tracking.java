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

	}

	public static int target(int mode, boolean autoFire)
	{
		switch(visionState)
		{
		case Constants.SEND_REQUEST:
			Turret.reset();
			if(mode == Constants.FUEL_MODE) Serial.sendData("1");
			else Serial.sendData("0");
			visionState = Constants.GET_RESPONSE;
			break;

		case Constants.GET_RESPONSE:
			String response = Serial.getData();
			if(response != null && response.contains(";"))
			{
				String data[] = response.split(";");
				angle = -Double.parseDouble(data[0]); //Pi should give us "Angle;Distance" for now. We can change this though
				distance = Double.parseDouble(data[1]);
				
				if(Hash.verifyOneAtATime(data[0] + ";" + data[1], Long.parseLong(data[2])))
				{
					SmartDashboard.putNumber("Angle", angle);
					if(mode == Constants.FUEL_MODE) visionState = Constants.TURN_TO_TARGET;
				}
				else
				{
					SmartDashboard.putString("Response", response);
					System.out.println("Failed hash check.");
					visionState = Constants.SEND_REQUEST;
				}
			}
			break;

		case Constants.TURN_TO_TARGET:
			boolean tracked = Turret.turnBy(angle, Constants.ACCEPTABLE_TURRET_ERROR);
			SmartDashboard.putBoolean("Turret Tracked", tracked);
			if(tracked)
			{
				Turret.reset();
				visionState = Constants.VERIFY_REQUEST;
			}
			break;
			
		case Constants.VERIFY_REQUEST:
			if(mode == Constants.FUEL_MODE) Serial.sendData("1");
			else Serial.sendData("0");
			visionState = Constants.VERIFY;
			break;
			
		case Constants.VERIFY:
			String response2 = Serial.getData();
			if(response2 != null && response2.contains(";"))
			{
				String data[] = response2.split(";");
				angle = -Double.parseDouble(data[0]); //Pi should give us "Angle;Distance" for now. We can change this though
				distance = Double.parseDouble(data[1]);
				if(mode == Constants.FUEL_MODE)
				{
					if(Math.abs(angle) < Constants.ACCEPTABLE_FUEL_ERROR) visionState = Constants.TRACKED_FUEL;
					else resetState();
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
