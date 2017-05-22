package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

public class Robot extends IterativeRobot
{
	DriverStation DS = DriverStation.getInstance();
	Preferences Prefs = Preferences.getInstance();
	UsbCamera Cam1 = CameraServer.getInstance().startAutomaticCapture(0);
	UsbCamera Cam2 = CameraServer.getInstance().startAutomaticCapture(1);
	Auton.Routines lastMode;
	public static RGB underglow;
	boolean lastAllianceIsRed;
	boolean connectedYet;
	
	public void robotInit()
	{	
		//NetworkTable.initialize();
		Console.getInstance();
		Constants.runningAleksBot = SmartDashboard.getBoolean("Running Alek's Bot?", false);
		Constants.reloadConfig();
		DriveBase.getInstance();
		Climber.getInstance();
		Shooter.getInstance();
		Turret.getInstance();
		Hopper.getInstance();
		Intake.getInstance();
		GearThingy.getInstance();
		Gearbox.getInstance();
		PDP.getInstance();
		Serial.getInstance();
		Tracking.getInstance();
		Teleop.getInstance();
		Auton.getInstance();
		SubAuton.getInstance();
		Sensors.getInstance(); //Must be last, it uses things initialized in other classes
		underglow = new RGB(Constants.PWM.Spikes.RGB_SPIKE);
		lastMode = Auton.Routines.fromInt(Prefs.getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
		lastAllianceIsRed = (DS.getAlliance() == DriverStation.Alliance.Red);
		connectedYet = false;	//TODO: Find a way to get this working.
	}

	public void autonomousInit()
	{
		underglow.setInverseAllianceColor();
		Console.restartTimer();
		DriveBase.setSquaredControls(false);
		Constants.reloadConfig();
		DriveBase.setBreakMode(true);
		DriveBase.shift(Gearbox.Gear.LOW);
		boolean redAlliance = (DS.getAlliance() == DriverStation.Alliance.Red); //If Alliance is Invalid, returns blue because our half-field is blue.
		Auton.Routines mode = Auton.Routines.fromInt(Prefs.getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
		Auton.setup(mode, redAlliance);
	}

	public void autonomousPeriodic()
	{
		Auton.run();
	}

	public void teleopInit()
	{
		Tracking.resetState();
		underglow.setInverseAllianceColor();
		DriveBase.setSquaredControls(true);
		GearThingy.setState(GearThingy.State.RESTING);
		Gearbox.shift(Gearbox.Gear.LOW);
		Constants.reloadConfig();
		Console.print("Teleop Started", Constants.Verbosity.Level.LOW);
		DriveBase.setBreakMode(true);
	}
	
	public void teleopPeriodic()
	{
		if(Constants.GUITAR_MODE) Teleop.runGuitar();
		else Teleop.run();
	}
	
	public void robotPeriodic()
	{
		Sensors.poll();
		Sensors.printAll();
	}

	public void testPeriodic()
	{

	}
	
	public void disabledPeriodic()
	{
		boolean redAlliance = (DS.getAlliance() == DriverStation.Alliance.Red);
		Auton.Routines mode = Auton.Routines.fromInt(Prefs.getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
		if(mode != lastMode || redAlliance != lastAllianceIsRed)
		{
			Console.printWarning("Switched to Auton " + mode.toString() + " on the " + ((redAlliance) ? "Red" : "Blue") + " Alliance"); //Prints it as a warning so it is visible by default. We don't want to ever run the wrong auton.
			lastMode = mode;
			lastAllianceIsRed = redAlliance;
		}
		
		if(DriveBase.isLocked()) DriveBase.lockDrivebase();
	}

	public void disabledInit() //It runs this once the robot connects to the DriverStation too.
	{
		GearThingy.setState(GearThingy.State.RESTING);
		Intake.setFlapDown();
		Console.print("Robot Disabled", Constants.Verbosity.Level.LOW);
	}
}
