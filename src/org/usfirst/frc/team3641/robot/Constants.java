package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Preferences;

public class Constants
{
	private static Preferences Prefs = Preferences.getInstance();
	
	public static boolean runningAleksBot = false;
	
	public static boolean GUITAR_MODE;
		
	public static final boolean disableAutonTimeouts = true;

	public static class Auton
	{
		public static double gearOneDistanceToTurn = -1.15;
		public static double gearThreeDistanceToTurn = 0;
		
		public static double gearOneTurnAngle = 60;
		public static double gearThreeTurnAngle = -60;
		
		public static double gearOneDistanceAfterTurn = -1.7;
		public static double gearTwoDistance = -1.8;
		public static double gearThreeDistanceAfterTurn = -1.64;
		
		public static double gearThreeTurnToHopperDistance = 0;
		
		public static double baselineDistance = 0.95;
		
		public static double hopperDistanceToTurn = 1.9;
		public static double hopperDistanceAfterTurn = 1.0;
		public static double hopperTurnAngle = -90.0;
		
		public static void reloadConfig()
		{
			gearOneDistanceToTurn = Prefs.getDouble("gearOneDistanceToTurn", gearOneDistanceToTurn);
			gearThreeDistanceToTurn = Prefs.getDouble("gearThreeDistanceToTurn", gearThreeDistanceToTurn);

			gearOneTurnAngle = Prefs.getDouble("gearOneTurnAngle", gearOneTurnAngle);
			gearThreeTurnAngle = Prefs.getDouble("gearThreeTurnAngle", gearThreeTurnAngle);

			gearOneDistanceAfterTurn = Prefs.getDouble("gearOneDistanceAfterTurn", gearOneDistanceAfterTurn);
			gearTwoDistance = Prefs.getDouble("gearTwoDistance", gearTwoDistance);
			gearThreeDistanceAfterTurn = Prefs.getDouble("gearThreeDistanceAfterTurn", gearThreeDistanceAfterTurn);

			gearThreeTurnToHopperDistance = Prefs.getDouble("gearThreeTurnToHopperDistance", gearThreeTurnToHopperDistance);

			baselineDistance = Prefs.getDouble("baselineDistance", baselineDistance);

			hopperDistanceToTurn = Prefs.getDouble("hopperDistanceToTurn", hopperDistanceToTurn);
			hopperDistanceAfterTurn = Prefs.getDouble("hopperDistanceAfterTurn", hopperDistanceAfterTurn);
			hopperTurnAngle = Prefs.getDouble("hopperTurnAngle", hopperTurnAngle);
		}
	}
	
	public static class DriveBase
	{

	}
	
	public static class Shooter
	{
		public static final double RPM_THRESHOLD = 100;
		
		public static final double GRAVITY = -9.81; // m/s/s
		public static final double LIFT = 0;       // m/s/s
		public static final double HEIGHT = 0.57; //Meters
		public static final double ANGLE = 60;   //Degrees
		public static final double WHEEL_RADIUS = 0.053; //Meters
		public static final double TARGET_HEIGHT = 2.46; //Meters
		public static final double MAX_RPM = 4500; //TODO: Run at full speed and measure value.
		public static double TARGET_RPM = 2600;
		public static double BATTER_RPM = 3025;
		public static final double ADJUSTMENT_MULTIPLIER = 2;
	}
	
	public static class Hopper
	{
		public static double CENTER_AGITATOR_SPEED = -1;
	}
	
	public static class Gear
	{
		public static final double SHAKE_RATE = 3; //Times per second
	}
	
	public static class Verbosity
	{
		public static enum Level
		{
			OFF(0),      //Nothing
			LOW(1),      //Stuff we probably want to know about.
			MID(2),      //Anything we might not care about, but could be nice to know.
			HIGH(3),     //Anything that spams the console.
			INSANITY(4); //Always runs every code loop. Why would you want this.
			
			private int level;
			
			private Level(int level)
			{
				this.level = level;
			}
			
			public int getLevel()
			{
				return this.level;
			}
			
			private static final Level[] values = Level.values(); //We cache the value array for preformance

			public static Level fromInt(int i)
			{
				if(i >= values.length || i<0)
				{
					System.err.println("WARNING: Verbosity Level " + i + " out of range. Defaulting to " + values[0].toString());
					i = 0;
				}
				return values[i];
			}
		}

		public static Level CURRENT_LEVEL = Level.HIGH;
		public static boolean PRINT_PID;
		
		public static boolean isAbove(Level level)
		{
			return (CURRENT_LEVEL.getLevel() >= level.getLevel());
		}	
	}

	public static class Serial
	{
		public static final int SERIAL_BAUDRATE  = 115200;
	}

	public static class CAN
	{
		public static class Talons
		{			
			public static final int DRIVEBASE_LEFT_1 = 12;
			public static final int DRIVEBASE_LEFT_2 = 13;
			public static final int DRIVEBASE_LEFT_3 = 14;
			public static final int LEFT_ENCODER_TALON = DRIVEBASE_LEFT_3;
			
			public static final int DRIVEBASE_RIGHT_1 = 1;
			public static final int DRIVEBASE_RIGHT_2 = 2;
			public static final int DRIVEBASE_RIGHT_3 = 3;
			public static final int RIGHT_ENCODER_TALON = DRIVEBASE_RIGHT_3;
			
			public static final int SHOOTER_RIGHT = 9;
			public static final int SHOOTER_LEFT= 10;
			public static final int CENTER_AGITATOR = 11;
			public static final int TURRET = 0;
		}
	}
	
	public static class PWM
	{
		public static class Sparks
		{
			public static final int INTAKE = 8;
			public static final int CENTER_AGITATOR = 7;
			public static final int LEFT_AGITATOR = 6;
			public static final int RIGHT_AGITATOR = 5;
			public static final int GEAR_WHEELS = 4;
			public static final int SHOOTER_ELEVATOR = 9;
		}
		
		public static class Victors
		{
			public static final int LEFT = 1;
			public static final int LEFT_SLAVE = 2;
			public static final int RIGHT = 3;
			public static final int RIGHT_SLAVE = 4;
		}
		
		public static class Spikes
		{
			public static final int RGB_SPIKE = 0;
		}
	}
	
	public static class Pnumatics
	{
		public static final int SHIFTER_FORWARD = 0;
		public static final int SHIFTER_REVERSE = 1;
		public static final int PTO_FORWARD = 2;
		public static final int PTO_REVERSE = 3;
		public static final int INTAKE = 4;
		public static final int FLAP = 5;
		public static final int GEAR_THINGY = 6;
		
		public static final double WORRY_PRESSURE = 50;
		public static final double PANCAKE_SHIFT_COST = 1;
		public static final double INTAKE_COST = 2;
	}
	
	public static class PDP
	{
		public static final int INTAKE = 5;
	}

	public static class PID
	{
		public static final double DRIVEBASE_CORRECTION_KP = 2.6;
		public static final double DRIVEBASE_CORRECTION_KI = 0.0;
		public static final double DRIVEBASE_CORRECTION_KD = 0.04;	

		public static final double DRIVEBASE_ROTATION_KP = 0.009;
		public static final double DRIVEBASE_ROTATION_KI = 8.2E-4;
		public static final double DRIVEBASE_ROTATION_KD = 9.0E-5;
		public static final double DRIVEBASE_ROTATION_KFF = 0;
		public static final double DRIVEBASE_ROTATION_DEADBAND = 15.0;

		public static final double DRIVEBASE_KP = 0.475;
		public static final double DRIVEBASE_KI = 0.04;
		public static final double DRIVEBASE_KD = 0.0;
		public static final double DRIVEBASE_DEADBAND = 0.75;
		
	
		public static final double SHOOTER_KP = 0.0001;
		public static final double SHOOTER_KI = 0.000008;
		public static final double SHOOTER_KD = 0;
		public static final double SHOOTER_DEADBAND = 200;
		public static final double SHOOTER_FF = 4750;
	
		public static final double TURRET_KP = 0.01;
		public static final double TURRET_KI = 0.001;
		public static final double TURRET_KD = 0;
		public static final double TURRET_DEADBAND = 10;
	}

	public static class Controllers
	{
		public static final int DRIVER = 0;
		public static final int OPERATOR = 1;
		public static final int GUITAR = 2;
	}
	
	public static class AnalogIn
	{
		public static final int PRESSURE_SENSOR = 0;
		public static final int ULTRASONIC_SENSOR = 1;
	}
	
	public static class DigitalIO
	{
		public static final int DOES_WE_HAS_GEAR_SWITCH = 0;
		public static final int ULTRASONIC_TRIGGER = 1;
		public static final int ULTRASONIC_ECHO = 2;
	}

	public static class Thresholds
	{
		public static final int NUMBER_OF_TURNING_CHECKS = 5; //Must be within error margin for this many loops
		public static final int NUMBER_OF_ENCODER_STILL_CHECKS = 10;
		
		public static final double ACCEPTABLE_TURRET_ERROR = 1;
		public static final double SHOOTER_MAX_ERROR = 50; //RPM
		public static final double AUTON_DRIVE_DISTANCE_ACCEPTABLE_ERROR = .05; //Get within 5cm of the target.
		public static final double AUTON_DRIVE_ANGLE_ACCEPTABLE_ERROR = 1;
		
		public static final double ACCEPTABLE_FUEL_ERROR = 3; //Degrees
		public static final double ACCEPTABLE_GEAR_ERROR = 2; //Degrees
		
		public static final double ANGLE_THRESHOLD = 1;
		
		public static final double INTAKE_STALL_CURRENT = 0;
	}

	public class Conversions
	{
		public static final double ULTRASONIC_VOLTAGE_TO_M = 0.977; 
		
		public static final double DISTANCE_TO_GOAL = 3.66;
		public static final double DRIVE_WHEEL_DIAMETER = .1;
		public static final double DRIVE_WHEEL_CIRCUMFERENCE = Math.PI*DRIVE_WHEEL_DIAMETER;
		public static final double DRIVE_ENCODER_TICKS_PER_TURN = -4096.0;
		public static final double LOW_GEAR_RATIO = 20.0/50.0;
		public static final double HIGH_GEAR_RATIO = 30.0/40.0;
		
		public static final double TURRET_ENCODER_TICKS_PER_DEGREE = 4096.0 / 360.0;
		public static final double TURRET_GEAR_RATIO = 90.0 / 18.0;
		public static final double SPEED_TO_RPM = (60 / (2*Math.PI*Shooter.WHEEL_RADIUS/2)) * 2;
		public static final double SHOOTER_ENCODER_TICKS_PER_REV = -4096.0;
		public static final double SECONDS_PER_MINUTE = 600;
		public static final double SHOOTER_ENCODER_TO_RPM = SECONDS_PER_MINUTE / SHOOTER_ENCODER_TICKS_PER_REV; // ticks/s * 60s/min * 1rev/8ticks = 7.5 RPM 
		public static final double DISTANCE_TO_RPM = 750; //TODO: Build Robot and find Value
		public static final double VOLTAGE_TO_METERS = 1;
		public static final double DRIVE_ENCODER_TO_METERS = -0.000623409726; //TODO: Set after we pick encoder, wheel diameter, and gear ratios;
		public static final double TURRET_ENCODER_TO_ANGLE = 360.0 / 4096.0;
		
		public static final double PRESSURE_MULTIPLIER = 250.0;
		public static final double PRESSURE_ZERO_VALUE = 25.0;
		public static final double VCC = 5;
	}
		
	public static void reloadConfig()
	{
		org.usfirst.frc.team3641.robot.PID.reloadAllConfigs();
		GUITAR_MODE = Prefs.getBoolean("Guitar Mode?", false);
		Verbosity.CURRENT_LEVEL = Verbosity.Level.fromInt(Prefs.getInt("Verbosity", 3));
	}
}
