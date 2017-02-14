package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Preferences;

public class Robot extends IterativeRobot
{
	DriverStation DS = DriverStation.getInstance();
	
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
		Auton.getInstance();
		Sensors.getInstance(); //Must be last, it uses things initalized in other classes
	}

	public void autonomousInit()
	{
		//Gearbox.shiftLow();
		boolean redAlliance = false; //Defaults to blue if invalid because our field is set up like the blue side
		if(DS.getAlliance() == DriverStation.Alliance.Red) redAlliance = true;
		Auton.modes mode = Auton.modes.fromInt(Preferences.getInstance().getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
		Auton.setup(mode, redAlliance, true);
		System.out.println("Starting Auton " + mode.toString() + " on the " + ((redAlliance) ? "Red" : "Blue") + " Alliance");
	}

	public void autonomousPeriodic()
	{
		Sensors.poll();
		Auton.test();
	}

	public void teleopInit()
	{
		System.out.println("Running teleopInit()");
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
