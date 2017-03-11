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
		Console.getInstance();
		Constants.runningAleksBot = SmartDashboard.getBoolean("Running Alek's Bot?", false);
		Constants.readConfig();
		DriveBase.getInstance();
		Shooter.getInstance();
		Turret.getInstance();
		Hopper.getInstance();
		Intake.getInstance();
		GearThingy.getInstance();
		Gearbox.getInstance();
		PDP.getInstance();
		Horn.getInstance();
		Serial.getInstance();
		Tracking.getInstance();
		Teleop.getInstance();
		Auton.getInstance();
		SubAuton.getInstance();
		Sensors.getInstance(); //Must be last, it uses things initialized in other classes
		//underglow = new RGB(Constants.PWM.Spikes.RGB_SPIKE);
		lastMode = Auton.modes.fromInt(Prefs.getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
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
		Auton.modes mode = Auton.modes.fromInt(Prefs.getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
		Auton.setup(mode, redAlliance);
	}

	public void autonomousPeriodic()
	{
		Auton.run();
	}

	public void teleopInit()
	{
		DriveBase.setSquaredControls(true);
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
	}

	public void testPeriodic()
	{

	}
	
	public void disabledPeriodic()
	{
		Sensors.printAll();
		boolean redAlliance = (DS.getAlliance() == DriverStation.Alliance.Red);
		Auton.modes mode = Auton.modes.fromInt(Prefs.getInt("Auton Number", 0)); //TODO: add a dropdown that reads the modes enum
		if(mode != lastMode || redAlliance != lastAllianceIsRed)
		{
			Console.printWarning("Switched to Auton " + mode.toString() + " on the " + ((redAlliance) ? "Red" : "Blue") + " Alliance"); //Prints it as a warning so it is visible by default. We don't want to ever run the wrong auton.
			lastMode = mode;
			lastAllianceIsRed = redAlliance;
			
			//if(DS.getAlliance() == DriverStation.Alliance.Invalid) underglow.setColor(RGB.Color.OFF);
			//else underglow.setColor((redAlliance) ? RGB.Color.RED : RGB.Color.BLUE);
		}
		
		if(DriveBase.isLocked()) DriveBase.lockDrivebase();
	}

	public void disabledInit() //It runs this once the robot connects to the DriverStation too.
	{
		Horn.setHorn(false);
		Intake.intakeUp();
		Intake.setFlapDown();
		Console.print("Robot Disabled", Constants.Verbosity.Level.LOW);
	}

}
