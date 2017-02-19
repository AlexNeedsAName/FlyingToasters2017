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
	RGB underglow;
	boolean lastAllianceIsRed;
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
		Horn.getInstance();
		Serial.getInstance();
		Tracking.getInstance();
		Teleop.getInstance();
		Auton.getInstance();
		Sensors.getInstance(); //Must be last, it uses things initialized in other classes
		underglow = new RGB(Constants.RGB_SPIKE);
		lastMode = Auton.modes.fromInt(Prefs.getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
		lastAllianceIsRed = (DS.getAlliance() == DriverStation.Alliance.Red);
		connectedYet = false;
	}

	public void autonomousInit()
	{
		DriveBase.setBreakMode(true);
		boolean redAlliance = (DS.getAlliance() == DriverStation.Alliance.Red); //If Alliance is Invalid, returns blue because our half-field is blue.
		Auton.modes mode = Auton.modes.fromInt(Prefs.getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
		Auton.setup(mode, redAlliance);
		if(Constants.VERBOSE >= Constants.MID) System.out.println("Starting Auton " + mode.toString() + " on the " + ((redAlliance) ? "Red" : "Blue") + " Alliance");
	}

	public void autonomousPeriodic()
	{
		Sensors.poll();
		Auton.run();
	}

	public void teleopInit()
	{
		if(Constants.VERBOSE >= Constants.MID) System.out.println("Teleop Started");
		DriveBase.setBreakMode(false);
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
		boolean redAlliance = (DS.getAlliance() == DriverStation.Alliance.Red);
		Auton.modes mode = Auton.modes.fromInt(Prefs.getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
		if(mode != lastMode || redAlliance != lastAllianceIsRed)
		{
			System.err.println("WARNING: Switched to Auton " + mode.toString() + " on the " + ((redAlliance) ? "Red" : "Blue") + " Alliance"); //Prints it as a warning so it is visible by default. We don't want to ever run the wrong auton.
			lastMode = mode;
			lastAllianceIsRed = redAlliance;
			
			if(DS.getAlliance() == DriverStation.Alliance.Invalid) underglow.setColor(RGB.OFF);
			else underglow.setColor((redAlliance) ? RGB.RED : RGB.BLUE);
		}
	}

	public void disabledInit() //It runs this once the robot connects to the DriverStation too.
	{
		if(Constants.VERBOSE >= Constants.MID) System.out.println("Robot Disabled");
	}

}
