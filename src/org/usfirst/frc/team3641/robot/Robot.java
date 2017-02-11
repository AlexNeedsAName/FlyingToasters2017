package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Preferences;

public class Robot extends IterativeRobot
{
	public void robotInit()
	{	
		Constants.runningAleksBot = SmartDashboard.getBoolean("Running Alek's Bot?", false);
		DriveBase.getInstance();
		Shooter.getInstance();
		Turret.getInstance();
		Hopper.getInstance();
		Intake.getInstance();
		Gearbox.getInstance();
		PDP.getInstance();
		Serial.getInstance();
		Tracking.getInstance();
		Teleop.getInstance();
		Sensors.getInstance(); //Must be last, it uses things initalized in other classes
	}

	public void autonomousInit()
	{
		//Gearbox.shiftLow();
		int mode = Preferences.getInstance().getInt("Auton Number", Constants.DO_NOTHING);
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
		//Gearbox.shiftHigh();
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
