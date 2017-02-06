package org.usfirst.frc.team3641.robot;

public class Constants
{
	public static boolean runningAleksBot;
	
	public static final int HIGH = 1;
	public static final int LOW = 0;
	
	public static final int REVERSE_MODE = 1;
	public static final int NORMAL_MODE = 2;
	
	public static final int PS4_PORT = 1;
	public static final int OPERATOR_PORT = 0;
	
	public static final int ULTRASONIC_PORT = 0;
	public static final int ULTRASONIC_ECHO = 0;
	public static final int ULTRASONIC_TRIGGER = 1;
			
	public static final int SERIAL_BAUDRATE  = 115200;

	//Talons
	public static final int DRIVEBASE_LEFT_TALON = 1;
	public static final int DRIVEBASE_LEFT_SLAVE_TALON = 2;
	public static final int DRIVEBASE_RIGHT_TALON = 3;
	public static final int DRIVEBASE_RIGHT_SLAVE_TALON = 4;
	public static final int SHOOTER_LEFT_TALON = 5;
	public static final int SHOOTER_RIGHT_TALON = 6;
	public static final int SHOOTER_ELEVATOR_TALON = 7;	
	
	//Victors (for Alek)
	public static final int LEFT_VICTOR = 1;
	public static final int LEFT_SLAVE_VICTOR = 2;
	public static final int RIGHT_VICTOR = 3;
	public static final int RIGHT_SLAVE_VICTOR = 4;
	
	//Pnumatics
	public static final int LEFT_SHIFTER_CHANNEL_FORWARD = 1;
	public static final int LEFT_SHIFTER_CHANNEL_BACKWARDS = 2;
	public static final int RIGHT_SHIFTER_CHANNEL_FORWARD = 3;
	public static final int RIGHT_SHIFTER_CHANNEL_BACKWARDS = 4;
	
	//UDP Constants	
	public static final int DRIVER_PORT = 5800;
	public static final String DRIVER_IP_ADDR = "10.36.42.81";

	//PID Constants
	public static final double GEAR_TRACKING_KP = 0.0; //TODO: Build robot, then tune this value
	public static final double GEAR_TRACKING_KI = 0.0; //TODO: Build robot, then tune this value
	public static final double GEAR_TRACKING_KD = 0.0; //TODO: Build robot, then tune this value
	
	public static final double DRIVEBASE_TRACKING_KP = 0.0; //TODO: Build robot, then tune this value
	public static final double DRIVEBASE_TRACKING_KI = 0.0; //TODO: Build robot, then tune this value
	public static final double DRIVEBASE_TRACKING_KD = 0.0; //TODO: Build robot, then tune this value
	
	public static final double SHOOTER_KP = 0.00006;
	public static final double SHOOTER_KI = 0.000005;
	public static final double SHOOTER_KD = 0.00000025;
	public static final double SHOOTER_FF = 5800;

	
	public static final int FUEL_MODE = 1;
	public static final int GEAR_MODE = 2;
	
	public static final int SEND_REQUEST = 1;
	public static final int GET_RESPONSE = 2;
	public static final int TURN_TO_TARGET = 3;
	public static final int SLIDE_GEAR_MECHANISM = 4;	
	public static final int TRACKED_GEAR = 5;
	public static final int TRACKED_FUEL = 6;
	
	public static final double ACCEPTABLE_FUEL_ERROR = 1; //Degrees
	public static final double ACCEPTABLE_GEAR_ERROR = 5; //Pixels
	
	public static final int CAMERA_CENTER = 160;
	public static final double DEGREES_PER_PIXEL = 0.2140625;
	
	//Conversions
	public static final double ENCODER_TO_RPM = -7.5; // ticks/s * 60s/min * 1rev/8ticks = 7.5 RPM 
	public static final double DISTANCE_TO_RPM = 750; //TODO: Build Robot and find Value
	public static final double VOLTAGE_TO_METERS = 1;
	public static final double ENCODER_TO_METERS = 0; //TODO: Build Robot and find Value
	public static final double DRIVE_ENCODER_TO_METERS = 1; //TODO: Set after we pick encoder, wheel diameter, and gear ratios;
	//Auton Modes
	public static final int START = 0;
	public static final int DRIVE_FORWARDS = 1;
	public static final int DONE = 2;
	public static final int DRIVE_TO_HOPPER_LINE = 3;
	public static final int TURN_TO_HOPPER = 4;
	public static final int DRIVE_TO_HOPPER = 5;
	public static final int SCORE_RANKING_POINT = 6;
	
	public static final int DO_NOTHING = 0;
	public static final int CROSS_BASELINE = 1;
	public static final int LINE_ALIGN = 2;
	public static final int MOUSE_CONTROL = 3;
	public static final int DEFAULT_AUTO = 4;
	public static final int HOPPER_AUTON = 5;

	
	
}
