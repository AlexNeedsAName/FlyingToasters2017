package org.usfirst.frc.team3641.robot;
import java.util.EnumMap;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class PS4
{
	private EnumMap<Button, Boolean> current, last;
	private EnumMap<Axis, Double> axes;
	private Joystick rawJoystick;
	private double leftAngle, leftMagnitude, rightAngle, rightMagnitude;

	public PS4(int port)
	{
		rawJoystick= new Joystick(port);
		current = new EnumMap<Button, Boolean>(Button.class);
		last = new EnumMap<Button, Boolean>(Button.class);
		axes = new EnumMap<Axis, Double>(Axis.class);
		poll(); //Populate the current EnumMap so the last EnumMap won't be null when the user polls for the first time.
	}
	
	public static enum Button
	{
		X, CIRCLE, TRIANGLE, SQUARE,
		LEFT_BUMPER, RIGHT_BUMPER,
		LEFT_TRIGGER_BUTTON, RIGHT_TRIGGER_BUTTON,
		SHARE, OPTIONS, PLAYSTATION_BUTTON,
		LEFT_STICK_BUTTON, RIGHT_STICK_BUTTON,
		DPAD_LEFT, DPAD_RIGHT, DPAD_UP, DPAD_DOWN,
		TOUCHPAD_BUTTON;
	}
	
	public enum Axis
	{
		LEFT_X, LEFT_Y, LEFT_TRIGGER,
		RIGHT_X, RIGHT_Y, RIGHT_TRIGGER;
	}

	public void setRumble(Double rumble, Double balance) //Balance goes from -1 (left) to 1 (right), with 0 being centered
	{
		double left = rumble - balance*rumble;
		double right = rumble + balance*rumble;
		
		if(left > 1 || right > 1)
		{
			double max;
			if(left > right) max = left;
			else max = right;
			
			left/= max;
			right/=max;
		}
		
		rawJoystick.setRumble(RumbleType.kLeftRumble, left);
		rawJoystick.setRumble(RumbleType.kRightRumble, right);
	}
	
	public double getAxis(Axis axis)
	{
		return axes.get(axis);
	}
	
	//Left Stick Stuff
	public double getLeftAngle()
	{
		return leftAngle;
	}
	
	public double getLeftMagnitude()
	{
		return leftMagnitude;
	}
	
	//Right Stick Stuff
	public double getRightAngle()
	{
		return rightAngle;
	}
	
	public double getRightMagnitude()
	{
		return rightMagnitude;
	}
	
	//Is it down at all
	public boolean isDown(Button button)
	{
		return current.get(button);
	}
	
	//Rising Edge only
	public boolean isPressed(Button button)
	{
		return (current.get(button) && !last.get(button));
	}
	
	//Falling Edge only
	public boolean isReleased(Button button)
	{
		return (!current.get(button) && last.get(button));
	}
	
	public void poll()
	{
		last = current.clone();

		axes.put(Axis.LEFT_X, rawJoystick.getRawAxis(0));
		axes.put(Axis.LEFT_Y, -rawJoystick.getRawAxis(1));
		axes.put(Axis.RIGHT_X, rawJoystick.getRawAxis(2));
		axes.put(Axis.LEFT_TRIGGER, (rawJoystick.getRawAxis(3) + .5) / 2);
		axes.put(Axis.RIGHT_TRIGGER, (rawJoystick.getRawAxis(4) + .5) / 2);
		axes.put(Axis.RIGHT_Y, -rawJoystick.getRawAxis(5));

		current.put(Button.SQUARE, rawJoystick.getRawButton(1));
		current.put(Button.X, rawJoystick.getRawButton(2));
		current.put(Button.CIRCLE, rawJoystick.getRawButton(3));
		current.put(Button.TRIANGLE, rawJoystick.getRawButton(4));
		current.put(Button.LEFT_BUMPER, rawJoystick.getRawButton(5));
		current.put(Button.RIGHT_BUMPER, rawJoystick.getRawButton(6));
		current.put(Button.LEFT_TRIGGER_BUTTON, rawJoystick.getRawButton(7));
		current.put(Button.RIGHT_TRIGGER_BUTTON, rawJoystick.getRawButton(8));
		current.put(Button.SHARE, rawJoystick.getRawButton(9));
		current.put(Button.OPTIONS, rawJoystick.getRawButton(10));
		current.put(Button.LEFT_STICK_BUTTON, rawJoystick.getRawButton(11));
		current.put(Button.RIGHT_STICK_BUTTON, rawJoystick.getRawButton(12));
		current.put(Button.PLAYSTATION_BUTTON, rawJoystick.getRawButton(13));
		current.put(Button.TOUCHPAD_BUTTON, rawJoystick.getRawButton(14));

		current.put(Button.DPAD_LEFT, (rawJoystick.getPOV(0) == 270));
		current.put(Button.DPAD_RIGHT, (rawJoystick.getPOV(0) == 90));
		current.put(Button.DPAD_UP, (rawJoystick.getPOV(0) == 0));
		current.put(Button.DPAD_DOWN, (rawJoystick.getPOV(0) == 180));
		
		leftMagnitude = Coords.rectToPolarRadius(getAxis(Axis.LEFT_X), getAxis(Axis.LEFT_Y));
		leftAngle = Coords.rectToPolarAngle(getAxis(Axis.LEFT_X), getAxis(Axis.LEFT_Y));
		rightMagnitude = Coords.rectToPolarRadius(getAxis(Axis.RIGHT_X), getAxis(Axis.RIGHT_Y));
		rightAngle = Coords.rectToPolarAngle(getAxis(Axis.RIGHT_X), getAxis(Axis.RIGHT_Y));

	}
}
