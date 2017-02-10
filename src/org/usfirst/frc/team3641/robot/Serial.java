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

	private Serial()
	{
		serial = new SerialPort(Constants.SERIAL_BAUDRATE, SerialPort.Port.kOnboard);
	}

	public static void sendData(String data)
	{
		serial.writeString(data);
	}

	public static String getData()
	{
		return serial.readString();
	}
}
