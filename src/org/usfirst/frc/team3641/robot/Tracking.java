package org.usfirst.frc.team3641.robot;

public class Tracking
{
	private static Tracking instance;
	private static UDP pi;
	private static PID GearTrackingPID, FuelTrackingPID;
	
	private static int center;
	private static double distance, error;
	
	private static int visionState;
	
	public static Tracking getInstance()
	{
		if(instance == null) instance = new Tracking();
		return instance;
	}
	
	private Tracking()
	{
    	GearTrackingPID = new PID(Constants.GEAR_TRACKING_KP, Constants.GEAR_TRACKING_KI, Constants.GEAR_TRACKING_KD);
    	FuelTrackingPID = new PID(Constants.FUEL_TRACKING_KP, Constants.FUEL_TRACKING_KI, Constants.FUEL_TRACKING_KD);
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
					center = Integer.parseInt(data[0]);
					distance = Integer.parseInt(data[1]) * Constants.RADIUS_TO_CM;
					if(mode == Constants.FUEL_MODE) visionState = Constants.TURN_TO_TARGET;
					else visionState = Constants.SLIDE_GEAR_MECHANISM;
				}
				break;
				
			case Constants.TURN_TO_TARGET:
				double angleOff = (center - Constants.CAMERA_CENTER) * Constants.DEGREES_PER_PIXEL;
				double targetAngle = DriveBase.getAngle() + angleOff;
				error = calcError(targetAngle, DriveBase.getAngle());
				if(Math.abs(error) < Constants.ACCEPTABLE_FUEL_ERROR) visionState = Constants.TRACKED_FUEL;
				else DriveBase.driveArcade(0, FuelTrackingPID.pid(error));
				//Shooter.Prep(distance);
				break;
			case Constants.TRACKED_FUEL:
				//Shooter.Fire(distance);
				break;
				
			case Constants.SLIDE_GEAR_MECHANISM:
				error = center - Constants.CAMERA_CENTER;
				if(Math.abs(error) < Constants.ACCEPTABLE_GEAR_ERROR) visionState = Constants.TRACKED_GEAR;
				//else GearMechanism.set(GearTrackingPID.pid(error));
				break;
			case Constants.TRACKED_GEAR:
				//Light up a square on the smart dashboard or something
				break;
		}
		
		return visionState;
		
	}
		
	public static void resetState()
	{
		visionState = Constants.SEND_REQUEST;
	}
	
	private static double calcError(double target, double current)
	{
		double counterClockwiseDistance, clockwiseDistance;
		
		if(target == current) return 0;
		else
		{
			counterClockwiseDistance = fixDegrees(target - current);
			clockwiseDistance = fixDegrees(360 - (target - current));
			
			if(counterClockwiseDistance > clockwiseDistance) return counterClockwiseDistance;
			else return -clockwiseDistance;
		}
		
	}
	
	private static double fixDegrees(double degrees)
	{
		while(degrees >= 360) degrees -= 360;
		while(degrees < 0) degrees += 360;
		return degrees;
	}

}
