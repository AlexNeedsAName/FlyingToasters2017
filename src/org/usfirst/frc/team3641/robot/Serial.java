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
		if(Constants.VERBOSE >= Constants.MID) System.out.println("Sent \"" + data + "\" over Serial");
		serial.writeString(data + "\n");
	}

	public static String getData()
	{
		String data = serial.readString();
		if(Constants.VERBOSE >= Constants.MID) System.out.println("Recieved \"" + data + "\" over Serial");
		return data;
	}
	
	public static double[] parseData(String message, int expectedLength) //Expected length does not include checksum
	{
		String data[] = message.split(";");
		if(data.length != expectedLength + 1)
		{
			return null;
		}
		else
		{			
			long hash = Long.parseLong(data[expectedLength]);
			String content = "";
			double[] values = new double[3];
			for(int i=0; i<expectedLength; i++)
			{
				content += data[i];
				if(i+1 != expectedLength)
				{
					content += ";";
					values[i] = Double.parseDouble(content);
				}
			}
			
			if(Hash.verifyOneAtATime(content, hash))
			{
				return values;
			}
			else
			{
				return null;
			}
		}
	}
}
