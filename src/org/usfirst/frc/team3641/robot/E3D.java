package org.usfirst.frc.team3641.robot;
import java.util.EnumMap;

import edu.wpi.first.wpilibj.Joystick;

public class E3D
{
	private EnumMap<Button, Boolean> current, last;
	private EnumMap<Axis, Double> axes;
	private Joystick rawJoystick;

	public E3D (int port)
	{
		rawJoystick= new Joystick(port);
		current = new EnumMap<Button, Boolean>(Button.class);
		last = new EnumMap<Button, Boolean>(Button.class);
		axes = new EnumMap<Axis, Double>(Axis.class);
	}
	
	public static enum Button
	{
		TRIGGER, THUMB,
		THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE,
		THUMB_POV_LEFT, THUMB_POV_RIGHT, THUMB_POV_UP, THUMB_POV_DOWN;
		private static final Button[] values = Button.values(); //We cache the value array because otherwise it would create a new array everytime we cast from an int (so 9 times every code loop). That adds up.
		public static Button fromInt(int i) { return values[i]; }
	}
	
	public enum Axis
	{
		X, Y, Z;
	}
	
	public double getAxis(Axis axis)
	{
		return axes.get(axis);
	}
	
	public double getAngle()
	{
		return rectToPolarAngle(getAxis(Axis.X), getAxis(Axis.Y));
	}
	
	public double getMagnitude()
	{
		return rectToPolarRadius(getAxis(Axis.X), getAxis(Axis.Y));
	}
	
	//Is it down at all
	public boolean isDown(Button button)
	{
		return current.get(button);
	}
	public boolean isDown(int button)
	{
		return isDown(Button.fromInt(button));
	}
	
	//Rising Edge only
	public boolean isPressed(Button button)
	{
		return (current.get(button) && !last.get(button));
	}
	public boolean isPressed(int button)
	{
		return isPressed(Button.fromInt(button));
	}

	//Falling Edge only
	public boolean isReleased(Button button)
	{
		return (!current.get(button) && last.get(button));
	}
	public boolean isReleased(int button)
	{
		return isReleased(Button.fromInt(button));
	}

	public void poll()
	{
		last = current;

		axes.put(Axis.X, rawJoystick.getRawAxis(0));
		axes.put(Axis.Y, rawJoystick.getRawAxis(1));
		axes.put(Axis.Z, rawJoystick.getRawAxis(2));
		
		current.put(Button.THUMB, rawJoystick.getRawButton(1));
		current.put(Button.THUMB, rawJoystick.getRawButton(2));
		for(int i = 3; i<=12; i++) current.put(Button.fromInt(i), rawJoystick.getRawButton(i)); //The rest of the buttons are just labeled by their number
		
		current.put(Button.THUMB_POV_LEFT, (rawJoystick.getPOV(0) == 270));
		current.put(Button.THUMB_POV_RIGHT, (rawJoystick.getPOV(0) == 90));
		current.put(Button.THUMB_POV_UP, (rawJoystick.getPOV(0) == 0));
		current.put(Button.THUMB_POV_DOWN, (rawJoystick.getPOV(0) == 180));
	}
	
	private static double rectToPolarAngle(double x, double y)
	{
		if(x == 0 && y == 0) return 0;
		if(x >  0 && y == 0) return 0;
		if(x == 0 && y >  0) return 90;
		if(x <  0 && y == 0) return 180;
		if(x == 0 && y <  0) return 270;
		
		double angle = Math.tan(y/x);
		
		if(x < 0) angle += 180;
		else if (y < 0) angle += 360;
		
		return angle;
	}
	
	private double rectToPolarRadius(double x, double y)
	{
		return Math.sqrt(x*x + y*y);
	}


}