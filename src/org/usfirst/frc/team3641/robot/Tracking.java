package org.usfirst.frc.team3641.robot;

public class Tracking
{
	private static Tracking instance;
	private static UDP pi;
	private static PID GearTrackingPID;
	
	private static int center;
	private static double distance, error, targetAngle;
	
	private static int visionState;
	
	public static Tracking getInstance()
	{
		if(instance == null) instance = new Tracking();
		return instance;
	}
	
	private Tracking()
	{
    	GearTrackingPID = new PID(Constants.GEAR_TRACKING_KP, Constants.GEAR_TRACKING_KI, Constants.GEAR_TRACKING_KD);
	}
	
	public static int target(int mode)
	{
		switch(visionState)
		{
			case Constants.SEND_REQUEST:
				Serial.sendData("Request");
				visionState = Constants.GET_RESPONSE;
				break;
				
			case Constants.GET_RESPONSE:
				String response = Serial.getData();
				if(response != null && response.contains(";"))
				{
					String data[] = response.split(";");
					center = Integer.parseInt(data[0]); //Pi should give us "xcord,distance" for now. We can change this though
					distance = Integer.parseInt(data[1]);
					if(mode == Constants.FUEL_MODE)
					{
						double angleOff = (center - Constants.CAMERA_CENTER) * Constants.DEGREES_PER_PIXEL;
						targetAngle = Sensors.getAngle() + angleOff;
						visionState = Constants.TURN_TO_TARGET;
					}
				}
				break;
				
			case Constants.TURN_TO_TARGET:
				
				boolean tracked = DriveBase.turnTo(targetAngle, Constants.ACCEPTABLE_FUEL_ERROR);
				Shooter.setDistance(distance);
				if(tracked) Shooter.fire();
				break;
		}
		
		return visionState;
		
	}
		
	public static void resetState()
	{
		visionState = Constants.SEND_REQUEST;
	}
	
}
