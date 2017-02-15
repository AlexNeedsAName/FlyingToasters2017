package org.usfirst.frc.team3641.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Preferences;

public class Robot extends IterativeRobot
{
	DriverStation DS = DriverStation.getInstance();
	Preferences Prefs = Preferences.getInstance();
	Auton.modes lastMode;
	boolean lastAlliance;
	boolean connectedYet;
	
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
		lastMode = Auton.modes.fromInt(Prefs.getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
		lastAlliance = (DS.getAlliance() == DriverStation.Alliance.Red);
		connectedYet = false;
	}

	public void autonomousInit()
	{
		//Gearbox.shiftLow();
		boolean redAlliance = (DS.getAlliance() == DriverStation.Alliance.Red); //Default to because our half field is setup like the blue alliance
		Auton.modes mode = Auton.modes.fromInt(Prefs.getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
		Auton.setup(mode, redAlliance, 1);
		System.out.println("Starting Auton " + mode.toString() + " on the " + ((redAlliance) ? "Red" : "Blue") + " Alliance");
	}

	public void autonomousPeriodic()
	{
		Sensors.poll();
		Auton.run();
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
	
	public void disabledPeriodic()
	{
		boolean alliance = (DS.getAlliance() == DriverStation.Alliance.Red);
		Auton.modes mode = Auton.modes.fromInt(Prefs.getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
		if(mode != lastMode || alliance != lastAlliance)
		{
			System.err.println("WARNING: Switched to Auton " + mode.toString() + " on the " + ((alliance) ? "Red" : "Blue") + " Alliance");
			lastMode = mode;
			lastAlliance = alliance;
		}
	}

	public void disabledInit() //It runs this once the robot connects to the DriverStation too.
	{

	}

}
