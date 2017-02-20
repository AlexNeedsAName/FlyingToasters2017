package org.usfirst.frc.team3641.robot;
import java.util.EnumMap;
import edu.wpi.first.wpilibj.Joystick;

public class Harmonix
{
	private EnumMap<Button, Boolean> current, last;
	private EnumMap<Axis, Double> axes;
	private Joystick rawJoystick;

	public Harmonix(int port)
	{
		rawJoystick= new Joystick(port);
		current = new EnumMap<Button, Boolean>(Button.class);
		last = new EnumMap<Button, Boolean>(Button.class);
		axes = new EnumMap<Axis, Double>(Axis.class);
		poll(); //Populate the current EnumMap so the last EnumMap won't be null when the user polls for the first time.
	}
	
	public static enum Button
	{
		BLUE, GREEN, RED, YELLOW, ORANGE,
		LOWER;
	}
	
	public enum Axis
	{
		STRUM, WHAMMY_BAR, BUTTONS;
	}

	public double getAxis(Axis axis)
	{
		return axes.get(axis);
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

		if(rawJoystick.getPOV(0) == 0) axes.put(Axis.STRUM, 1.0);
		else if(rawJoystick.getPOV(0) == 180) axes.put(Axis.STRUM, -1.0);
		else axes.put(Axis.STRUM, 0.0);

		double wb = rawJoystick.getRawAxis(2);
		if(wb == -0.0078125) wb = 0;
		else wb = (wb+1)/2;
		
		axes.put(Axis.WHAMMY_BAR, wb);
		
		System.out.println("Strum: " + axes.get(Axis.STRUM) + "; Whammy Bar: " + axes.get(Axis.WHAMMY_BAR));

		current.put(Button.GREEN, rawJoystick.getRawButton(2));
		current.put(Button.RED, rawJoystick.getRawButton(3));
		current.put(Button.YELLOW, rawJoystick.getRawButton(4));
		current.put(Button.BLUE, rawJoystick.getRawButton(1));
		current.put(Button.ORANGE, rawJoystick.getRawButton(5));

		current.put(Button.LOWER, rawJoystick.getRawButton(7));
		
		axes.put(Axis.BUTTONS, buttonsToAxis());
	}

	public double buttonsToAxis()
	{
		double rotation = 0;
		if(isDown(Button.RED) && !isDown(Button.BLUE)) rotation = -.5;
		else if(isDown(Button.BLUE) && !isDown(Button.RED)) rotation = .5;
		if(rotation != 0)
		{
			if(isDown(Button.GREEN) || isDown(Button.ORANGE)) rotation *= 2;
			if(isDown(Button.YELLOW)) rotation /= 2;
		}
		else
		{
			if(isDown(Button.GREEN) && !isDown(Button.ORANGE)) rotation = -1;
			else if(isDown(Button.ORANGE) && !isDown(Button.GREEN)) rotation = 1;
		}
		return rotation;
	}
}
