package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;

public class Auton
{
	private static Auton instance;
	private static int autonState;
	
	private static boolean alreadyDriving = false;
	private static boolean lineFound = false;
	private static boolean endOfLine = false;
	
	
	private static double initalDist;
	private static double finalDist;
	private static boolean runOnce;
	
	public static UDP udp;
	public static Ultrasonic ultra;
	
	public static Auton getInstance()
	{
		runOnce = false;
		if(instance == null) instance = new Auton();
		return instance;
	}
	
	private Auton()
	{
		autonState = Constants.START;
	}
	
	public static void run(int autonMode)
	{
		String receivedData;
		Boolean continueMouseControl = true;
		switch(autonMode)
		{
			case Constants.DO_NOTHING:
				
				break;
				
				
			case Constants.CROSS_BASELINE:
				
				crossBaseline();
				
				break;
				
				
			case Constants.LINE_ALIGN:
				
					if(udp == null) udp = new UDP("beaglebone.local", 3641);
					
					//Request info about line position
					if (runOnce == false) 
					{
						udp.sendData("1");
						runOnce = true;
					}
					
					receivedData = udp.getData();
					
			        if (receivedData != null) 
			        {
						//This code allows for the incoming data to split up into parts by spaces
				        String[] parts = receivedData.split(" ");
				        String part1 = parts[0];
				        double part1_double = Double.parseDouble(part1);
				        System.out.println("RECEIVED: " + part1);
						
				        DriveBase.turnDegrees(part1_double, 2);
					}
			        
				break;
				
				
			case Constants.DEFAULT_AUTO:
				if(udp == null) udp = new UDP("beaglebone.local", 3641);
				if(ultra == null) ultra = new Ultrasonic(0, 1);
				Auton.ultra.setAutomaticMode(true);

				//Find the line first!
				if (lineFound == false) 
				{
					udp.sendData("2");
					receivedData = udp.getData();
					if (receivedData != null) 
			        {
						//This code allows for the incoming data to split up into parts by spaces
				        String[] parts = receivedData.split(" ");
				        String part1 = parts[0];
				        double part1_double = Double.parseDouble(part1);
				        System.out.println("RECEIVED: " + part1);
				        
				        if (part1_double == 999) 
				        {
				        	DriveBase.driveArcade(-0.6, 0);
				        }
				        else
				        {
				        	lineFound = true;
				        	DriveBase.driveArcade(-0.6, 0);
				        	Timer.delay(1);
				        	DriveBase.driveArcade(0, 0);
				        	DriveBase.turnDegrees(55, 2);
				        }
					}
				}
				
				//Follow the line until you loose it
				if (lineFound == true && endOfLine == false) 
				{
					udp.sendData("2");
					receivedData = udp.getData();
					if (receivedData != null) 
			        {
						//This code allows for the incoming data to split up into parts by spaces
				        String[] parts = receivedData.split(" ");
				        String part1 = parts[0];
				        double part1_double = Double.parseDouble(part1);
				        System.out.println("RECEIVED: " + part1);
						
				        if (part1_double != 999)
				        {
				        	if (part1_double > -30 && part1_double < 30)
				        	{
				        		DriveBase.driveArcade(-0.4, 0);
				        	}
				        	if (part1_double <= -30)
				        	{
				        		DriveBase.driveArcade(-0.4, -0.3);
				        	}
				        	if (part1_double >= 30)
				        	{
				        		DriveBase.driveArcade(-0.4, 0.3);
				        	}
				        }
				        else
				        {
				        	DriveBase.driveArcade(0, 0);
				        }
				        
				        double range = Auton.ultra.getRangeInches();
						System.out.println(range);
				        
						if (range < 20) 
						{
							DriveBase.driveArcade(0, 0);
							endOfLine = true;
						}
					}
				}

			break;
			
			
			case Constants.MOUSE_CONTROL:
				
				if(udp == null) udp = new UDP("beaglebone.local", 3641);
				
				//Request info about line position
				if (runOnce == false) 
				{
					udp.sendData("1");
					runOnce = true;
				}
				
				while (continueMouseControl == true) 
				{
					receivedData = udp.getData();
					
					if (receivedData != null) 
			        {
						//This code allows for the incoming data to split up into parts by spaces
				        String[] parts = receivedData.split(" ");
				        String xQue = parts[0];
				        String yQue = parts[1];
				        String wState = parts[2];
				        String sState = parts[3];
				        double xQue_double = Double.parseDouble(xQue);
				        System.out.println("RECEIVED: " + xQue);
				        
				        if (wState == "0" && sState == "0")
				        {
				        	DriveBase.driveArcade(0, 0);
				        }
				        
				        if (wState == "1")
				        {
				        	DriveBase.driveArcade(0.8, 0);
				        }
				        
				        if (sState == "1")
				        {
				        	DriveBase.driveArcade(-0.8, 0);
				        }
						
				        if (xQue_double < 0) 
				        {
				        	DriveBase.driveArcade(0.0, -0.8);
				        }
				        if (xQue_double > 0) 
				        {
				        	DriveBase.driveArcade(0.0, 0.8);
				        }
				        if (xQue_double == 0) {
				        	DriveBase.driveArcade(0, 0);
				        }
				        //xQue_double = xQue_double*0.25;
				        //DriveBase.turnDegrees(xQue_double, 2);
					}
				}
				
				break;

		}
	}
	
	private static void crossBaseline()
	{
		if(autonState == Constants.START)
		{
			autonState = Constants.DRIVE_FORWARDS;
			alreadyDriving = false;
		}
		
		if(autonState == Constants.DRIVE_FORWARDS)
		{
			if(driveForwards(284, .5))
			{
				autonState = Constants.DONE;
				alreadyDriving = false;
			}
		}
		
	}
	
	private static boolean driveForwards(double distance, double speed)
	{
		if(!alreadyDriving)
		{
			initalDist = Sensors.getDriveDistance();
			finalDist = initalDist + distance;
			alreadyDriving = true;
		}
		
		if(Sensors.getDriveDistance() <= finalDist)
		{
			DriveBase.driveArcade(speed, 0);
			return false;
		}
		else return true;
		
	}

}
