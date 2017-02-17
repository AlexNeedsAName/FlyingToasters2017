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
		try
		{
			serial = new SerialPort(Constants.SERIAL_BAUDRATE, SerialPort.Port.kOnboard);
		}
		catch(Exception e)
		{
			System.err.println("WARNING: Could not initalize Serial Port");
			System.out.println(e.toString());
		}
	}

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
	/*
	public static double[] parseData(String message, int expectedLength) //Expected length does not include checksum
	{
		expectedLength--;
		String data[] = message.split(";");
		if(data.length != expectedLength + 1)
		{
			return null;
		}
		else
		{			
			//long hash = Long.parseLong(data[expectedLength]);
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
			
			if(true)//Hash.verifyOneAtATime(content, hash))
			{
				return values;
			}
			else
			{
				return null;
			}
		}
	}*/
}
