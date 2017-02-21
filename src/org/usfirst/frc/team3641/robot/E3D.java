package org.usfirst.frc.team3641.robot;
import java.util.EnumMap;
import edu.wpi.first.wpilibj.Joystick;

public class E3D
{
	private EnumMap<Button, Boolean> current, last;
	private EnumMap<Axis, Double> axes;
	private Joystick rawJoystick;
	private double angle, magnitude;

	/**
	 * Initalize the Logitech Extreme 3D Pro with the port.
	 * 
	 * @param port The port it uses on the driver station.
	 */
	public E3D (int port)
	{
		rawJoystick= new Joystick(port);
		current = new EnumMap<Button, Boolean>(Button.class);
		last = new EnumMap<Button, Boolean>(Button.class);
		axes = new EnumMap<Axis, Double>(Axis.class);
		poll();
	}
	
	/**
	 * The buttons it supports
	 */
	public static enum Button
	{
		TRIGGER, THUMB,
		THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE,
		THUMB_POV_LEFT, THUMB_POV_RIGHT, THUMB_POV_UP, THUMB_POV_DOWN;
		
		private static final Button[] values = Button.values(); //We cache the value array because otherwise it would create a new array everytime we cast from an int (so 9 times every code loop). That adds up.
		public static Button fromInt(int i)
		{
			i-=1; //Start at 0, not 1
			if(i >= values.length || i<0)
			{
				System.err.println("WARNING: Button " + i + " out of range. Defaulting to " + values[0].toString());
				i = 0;
			}
			return values[i];
		}
	}
	
	/**
	 * The axes it supports
	 */
	public enum Axis
	{
		X, Y, Z;
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
	 * Get the polar angle of the stick.
	 * 
	 * @return The polar angle of the stick.
	 */
	public double getAngle()
	{
		return angle;
	}
	
	/**
	 * Get the polar magnitude of the stick.
	 * 
	 * @return the polar magnitude of the stick.
	 */
	public double getMagnitude()
	{
		return magnitude;
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
	 * Checks if the specified button is down at all.
	 * 
	 * @param button The button to read. (Just an int)
	 * @return True if the button is down.
	 */
	public boolean isDown(int button)
	{
		return isDown(Button.fromInt(button));
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
	 * Checks if the specified button has just been pressed.
	 * 
	 * @param button The button to read.
	 * @return True on the rising edge of the button.
	 */
	public boolean isPressed(int button)
	{
		return isPressed(Button.fromInt(button));
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
	 * Checks if the specified button has just been released.
	 * 
	 * @param button The button to read.
	 * @return True on the falling edge of the button.
	 */
	public boolean isReleased(int button)
	{
		return isReleased(Button.fromInt(button));
	}
	
	/**
	 * Read the current state of each button and axis.
	 */
	public void poll()
	{
		last = current.clone();

		axes.put(Axis.X, rawJoystick.getRawAxis(0));
		axes.put(Axis.Y, -rawJoystick.getRawAxis(1));
		axes.put(Axis.Z, rawJoystick.getRawAxis(2));
		
		current.put(Button.TRIGGER, rawJoystick.getRawButton(1));
		current.put(Button.THUMB, rawJoystick.getRawButton(2));
		for(int i = 3; i<=12; i++) current.put(Button.fromInt(i), rawJoystick.getRawButton(i)); //The rest of the buttons are just labeled by their number
		
		current.put(Button.THUMB_POV_LEFT, (rawJoystick.getPOV(0) == 270));
		current.put(Button.THUMB_POV_RIGHT, (rawJoystick.getPOV(0) == 90));
		current.put(Button.THUMB_POV_UP, (rawJoystick.getPOV(0) == 0));
		current.put(Button.THUMB_POV_DOWN, (rawJoystick.getPOV(0) == 180));
		
		magnitude = Coords.rectToPolarRadius(getAxis(Axis.X), getAxis(Axis.Y));
		angle = Coords.rectToPolarAngle(getAxis(Axis.X), getAxis(Axis.Y));
	}
}