package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.SerialPort;

public class Serial
{
	private static Serial instance;
	private static SerialPort serial;

	public static Serial getInstance()
	{
		if(instance == null) instance = new Serial();
		return instance;
	}

	/**
	 * Initialize the serial port.
	 */
	private Serial()
	{
		try
		{
			serial = new SerialPort(Constants.Serial.SERIAL_BAUDRATE, SerialPort.Port.kOnboard);
		}
		catch(Exception e)
		{
			System.err.println("WARNING: Could not initalize Serial Port");
			System.out.println(e.toString());
		}
	}

	/**
	 * Send a string over serial.
	 * 
	 * @param data The string to send over serial.
	 * @return Success.
	 */
	public static boolean sendData(String data)
	{
		try
		{
			serial.writeString(data + "\n");
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/**
	 * Get a string over serial.
	 * 
	 * @return The string received over serial. Returns null if there is an error or the length is 0
	 */
	public static String getData()
	{
		try
		{
			String data = serial.readString();
			if(data.length() == 0) return null;
			return data;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	//public static double[] parseData(String message, int expectedLength) //Expected length does not include checksum
	//{
	//}
}
