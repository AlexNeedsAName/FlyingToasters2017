package org.usfirst.frc.team3641.robot;

public class Constants
{
	public static final boolean disableTimeouts = false;
	
	public static final int OFF = 0;	//Nothing
	public static final int LOW = 1;    //Stuff we probably want to know about
	public static final int MID = 2;    //Stuff that's pretty routine, but only runs once.
	public static final int HIGH = 3;   //Anything that spams the console

	public static int VERBOSE = HIGH;
	public static boolean PRINT_PID = false;
	
	public static boolean runningAleksBot = false;
	
	public static final double ADJATATOR_SPEED = .5;

	public static final int REVERSE_MODE = 1;
	public static final int NORMAL_MODE = 2;

	public static final int PS4_PORT = 0;
	public static final int OPERATOR_PORT = 1;

	public static final int ULTRASONIC_PORT = 0;
	public static final int ULTRASONIC_ECHO = 0;
	public static final int ULTRASONIC_TRIGGER = 1;

	public static final int SERIAL_BAUDRATE  = 115200;
	
	public static final int NUMBER_OF_ENCODER_STILL_CHECKS = 10;

	//Talons
	public static final int DRIVEBASE_LEFT_TALON = 3;
	public static final int DRIVEBASE_LEFT_SLAVE_TALON = 4;
	public static final int DRIVEBASE_LEFT_SLAVE2_TALON = 0;
	public static final int DRIVEBASE_RIGHT_TALON = 1;
	public static final int DRIVEBASE_RIGHT_SLAVE_TALON = 2;
	public static final int DRIVEBASE_RIGHT_SLAVE2_TALON = 0;
	public static final int SHOOTER_LEFT_TALON = 0;
	public static final int SHOOTER_RIGHT_TALON = 0;
	public static final int TURRET_TALON = 6;
	
	//Sparks
	public static final int INTAKE_LEFT_SPARK = 1;
	public static final int INTAKE_RIGHT_SPARK = 2;
	public static final int HOPPER_ADJATATOR_SPARK = 3;
	public static final int SHOOTER_ELEVATOR_SPARK = 4;

	//Victors (for Alek)
	public static final int LEFT_VICTOR = 1;
	public static final int LEFT_SLAVE_VICTOR = 2;
	public static final int RIGHT_VICTOR = 3;
	public static final int RIGHT_SLAVE_VICTOR = 4;
	
	//Spikes
	public static final int RGB_SPIKE = 0;

	//Pnumatics
	public static final int SHIFTER_CHANNEL_FORWARD = 0;
	public static final int SHIFTER_CHANNEL_REVERSE = 1;
	public static final int PTO_CHANNEL_FORWARD = 2;
	public static final int PTO_CHANNEL_REVERSE = 3;
	public static final int INTAKE_CHANNEL_FORWARD = 4;
	public static final int INTAKE_CHANNEL_REVERSE = 5;

	//UDP Constants	
	public static final int DRIVER_PORT = 5800;
	public static final String DRIVER_IP_ADDR = "10.36.42.81";

	//PID Constants
	public static final double DRIVEBASE_ROTATION_KP = 0.025; //TODO: Build robot, then tune this value
	public static final double DRIVEBASE_ROTATION_KI = 0.003; //TODO: Build robot, then tune this value
	public static final double DRIVEBASE_ROTATION_KD = 0.0; //TODO: Build robot, then tune this value
	public static final double DRIVEBASE_ROTATION_FF = 0.0;
	public static final int NUMBER_OF_TURNING_CHECKS = 5; //Must be within error margin for this many loops

	public static final double DRIVEBASE_KP = 0.35; //TODO: Build robot, then tune this value
	public static final double DRIVEBASE_KI = 0.0; //TODO: Build robot, then tune this value
	public static final double DRIVEBASE_KD = 0.0; //TODO: Build robot, then tune this value
	public static final double DRIVEBASE_FF = 0.12;

	public static final double SHOOTER_KP = 0.00006;
	public static final double SHOOTER_KI = 0.000005;
	public static final double SHOOTER_KD = 0.00000025;
	public static final double SHOOTER_FF = 5800;

	public static final double TURRET_KP = 0.01;
	public static final double TURRET_KI = 0.001;
	public static final double TURRET_KD = 0;
	public static final double TURRET_DEADBAND = 10;
	public static final double ACCEPTABLE_TURRET_ERROR = 1;

	//Tracking Constants
	public static final int FUEL_MODE = 1;
	public static final int GEAR_MODE = 2;

	public static final int SEND_REQUEST = 1;
	public static final int GET_RESPONSE = 2;
	public static final int TURN_TO_TARGET = 3;
	public static final int SLIDE_GEAR_MECHANISM = 4;	
	public static final int TRACKED_GEAR = 5;
	public static final int TRACKED_FUEL = 6;
	public static final int VERIFY_REQUEST = 7;
	public static final int VERIFY = 8;


	public static final double ACCEPTABLE_FUEL_ERROR = 3; //Degrees
	public static final double ACCEPTABLE_GEAR_ERROR = 5; //Pixels

	//Conversions
	public static final double ENCODER_TO_RPM = -7.5; // ticks/s * 60s/min * 1rev/8ticks = 7.5 RPM 
	public static final double DISTANCE_TO_RPM = 750; //TODO: Build Robot and find Value
	public static final double VOLTAGE_TO_METERS = 1;
	public static final double ENCODER_TO_METERS = 0; //TODO: Build Robot and find Value
	public static final double DRIVE_ENCODER_TO_METERS = -0.000623409726; //TODO: Set after we pick encoder, wheel diameter, and gear ratios;
	public static final double TURRET_ENCODER_TO_ANGLE = 360.0 / 4096.0;
	
	public static final double AUTON_RPM = 60; //TODO: build robot, tune value (you get the drill)
	public static final double AUTON_DRIVE_DISTANCE_ACCEPTABLE_ERROR = .05; //Get within 5cm of the target.
}
