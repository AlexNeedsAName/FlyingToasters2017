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

	/**
	 * Initialize the PS4 controller with the port.
	 * 
	 * @param port The port it uses on the driver station.
	 */
	public PS4(int port)
	{
		rawJoystick= new Joystick(port);
		current = new EnumMap<Button, Boolean>(Button.class);
		last = new EnumMap<Button, Boolean>(Button.class);
		axes = new EnumMap<Axis, Double>(Axis.class);
		poll(); //Populate the current EnumMap so the last EnumMap won't be null when the user polls for the first time.
	}
	
	/**
	 * The buttons it supports
	 */
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
	
	/**
	 * The axes it supports
	 */
	public enum Axis
	{
		LEFT_X, LEFT_Y, LEFT_TRIGGER,
		RIGHT_X, RIGHT_Y, RIGHT_TRIGGER;
	}
	
	/**
	 * Set the rumble on the controller.
	 * 
	 * @param rumble The amount of rumble you want.
	 * @param balance The balance left to right. -1 is left, 0 is centered, 1 is right.
	 */
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
	
	/**
	 * Returns the value of the specified axis.
	 * 
	 * @param axis The axis to read.
	 * @return The value of said axis.
	 */
	public double getAxis(Axis axis)
	{
		return axes.get(axis);
	}
	
	/**
	 * Get the polar angle of the left stick.
	 * 
	 * @return The polar angle of the left stick.
	 */
	public double getLeftAngle()
	{
		return leftAngle;
	}
	
	/**
	 * Get the polar magnitude of the left stick.
	 * 
	 * @return the polar magnitude of the left stick.
	 */
	public double getLeftMagnitude()
	{
		return leftMagnitude;
	}
	
	/**
	 * Get the polar angle of the right stick.
	 * 
	 * @return The polar angle of the right stick.
	 */
	public double getRightAngle()
	{
		return rightAngle;
	}
	
	/**
	 * Get the polar magnitude of the right stick.
	 * 
	 * @return the polar magnitude of the right stick.
	 */
	public double getRightMagnitude()
	{
		return rightMagnitude;
	}
	
	/**
	 * Checks if the specified button is down at all.
	 * 
	 * @param button The button to read.
	 * @return True if the button is down.
	 */
	public boolean isDown(Button button)
	{
		return current.get(button);
	}
	
	/**
	 * Checks if the specified button has just been pressed.
	 * 
	 * @param button The button to read.
	 * @return True on the rising edge of the button.
	 */
	public boolean isPressed(Button button)
	{
		return (current.get(button) && !last.get(button));
	}
	
	/**
	 * Checks if the specified button has just been released.
	 * 
	 * @param button The button to read.
	 * @return True on the falling edge of the button.
	 */
	public boolean isReleased(Button button)
	{
		return (!current.get(button) && last.get(button));
	}
	
	/**
	 * Read the current state of each button and axis.
	 */
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
