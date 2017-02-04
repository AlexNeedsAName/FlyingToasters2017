package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Joystick;

public class PS4 extends Joystick
{
	private double leftX, leftY, rightX, rightY, leftTrigger, rightTrigger;
	private boolean X, circle, triangle, square, up, down, left, right, share, options, leftBumper, leftTriggerButton, rightBumper, rightTriggerButton, leftStickButton, rightStickButton, playstation;

	public PS4(int port)
	{
		super(port);
	}
	
	public void readValues()
	{
		leftX = getRawAxis(0);
		leftY = getRawAxis(1);
		rightX = getRawAxis(2);
		leftTrigger = (getRawAxis(3) + .5) / 2;
		rightTrigger = (getRawAxis(4) + .5) / 2;
		rightY = getRawAxis(5);
		
		square = getRawButton(1);
		X = getRawButton(2);
		circle = getRawButton(3);
		triangle = getRawButton(4);
		leftBumper = getRawButton(5);
		rightBumper = getRawButton(6);
		leftTriggerButton = getRawButton(7);
		rightTriggerButton = getRawButton(8);
		share = getRawButton(9);
		options = getRawButton(10);
		leftStickButton = getRawButton(11);
		rightStickButton = getRawButton(12);
		playstation = getRawButton(13);
		
		left = (getPOV(0) == 270);
		right = (getPOV(0) == 90);
		up = (getPOV(0) == 0);
		down = (getPOV(0) == 180);
	}
	
	public double getLeftX()
	{
		return leftX;
	}
	public double getLeftY()
	{
		return leftY;
	}
	public double getRightX()
	{
		return rightY;
	}
	public double getRightY()
	{
		return rightX;
	}
	public double getLeftTrigger()
	{
		return leftTrigger;
	}
	public double getRightTrigger()
	{
		return rightTrigger;
	}
	public boolean getSquareButton()
	{
		return square;
	}
	public boolean getXButton()
	{
		return X;
	}
	public boolean getTriangleButton()
	{
		return triangle;
	}
	public boolean getCircleButton()
	{
		return circle;
	}
	public boolean getShareButton()
	{
		return share;
	}
	public boolean getOptionsButton()
	{
		return options;
	}
	public boolean getPlayStationButton()
	{
		return playstation;
	}
	public boolean getLeftTriggerButton()
	{
		return leftTriggerButton;
	}
	public boolean getRightTriggerButton()
	{
		return rightTriggerButton;
	}
	public boolean getLeftBumper()
	{
		return leftBumper;
	}
	public boolean getRightBumper()
	{
		return rightBumper;
	}
	public boolean getDPadUp()
	{
		return up;
	}
	public boolean getDPadDown()
	{
		return down;
	}
	public boolean getDPadLeft()
	{
		return left;
	}
	public boolean getDPadRight()
	{
		return right;
	}
	public boolean getLeftStickButton()
	{
		return leftStickButton;
	}
	public boolean getRightStickButton()
	{
		return rightStickButton;
	}
}
