package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Preferences;

public class Robot extends IterativeRobot
{
	public void robotInit()
	{	
		Constants.runningAleksBot = SmartDashboard.getBoolean("Running Alek's Bot?", true);
		Dashboard.getInstance();
    	Teleop.getInstance();
    	DriveBase.getInstance();
    	Tracking.getInstance();
    	PDP.getInstance();
    	Shooter.getInstance();
    	Sensors.getInstance();
    	Gearbox.getInstance();
	}
    
    public void autonomousInit()
    {
    	Gearbox.shiftLow();
    	int mode = Preferences.getInstance().getInt("Auton Number", 4);
    	boolean red = Preferences.getInstance().getBoolean("Red Alliance", true);
    	Auton.getInstance(mode, red);
    }

    public void autonomousPeriodic()
    {
    	Sensors.poll();
    	Auton.run();
    }

    public void teleopInit()
    {
    	Gearbox.shiftHigh();
    }
    
    public void teleopPeriodic()
    {
    	Sensors.poll();
        Teleop.run();
    }
    
    public void testPeriodic()
    {
    
    }
    
}
