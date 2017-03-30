package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.CameraServer;

public class Robot extends IterativeRobot
{
	DriverStation DS = DriverStation.getInstance();
	Preferences Prefs = Preferences.getInstance();
	CameraServer CS = CameraServer.getInstance();
	Auton.Routines lastMode;
	public static RGB underglow;
	boolean lastAllianceIsRed;
	boolean connectedYet;
	
	public void robotInit()
	{	
		CS.startAutomaticCapture();
		Console.getInstance();
		Constants.runningAleksBot = SmartDashboard.getBoolean("Running Alek's Bot?", false);
		Constants.reloadConfig();
		DriveBase.getInstance();
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
		connectedYet = false;
	}

	public void autonomousInit()
	{
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
		DriveBase.setSquaredControls(true);
		GearThingy.setState(GearThingy.State.RESTING);
		Gearbox.shift(Gearbox.Gear.HIGH);
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
		if(Teleop.driver.isPressed(PS4.Button.TOUCHPAD_BUTTON)) Sensors.resetSensors();
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
		Intake.intakeUp();
		Intake.setFlapDown();
		Console.print("Robot Disabled", Constants.Verbosity.Level.LOW);
		
//		DriverStation.Alliance alliance = DS.getAlliance();
//		if(alliance == DriverStation.Alliance.Invalid) underglow.setColor(RGB.Color.BLUE);
//		else underglow.setColor((alliance == DriverStation.Alliance.Red) ? RGB.Color.RED : RGB.Color.BLUE);
	}

}
