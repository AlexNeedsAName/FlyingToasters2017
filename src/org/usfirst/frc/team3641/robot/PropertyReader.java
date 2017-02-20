package org.usfirst.frc.team3641.robot;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader
{
	
	private static final String BASE_DIR = "/home/lvuser/config/";
	private static final String EXTENSION = ".properties";
	private String file;
	private Properties config;
	
	public PropertyReader(String name)
	{
		file = BASE_DIR + name + EXTENSION;
		config = new Properties();
		try
		{
			config.load(new FileReader(file));
		}
		catch (IOException e)
		{
			System.err.println("WARNING: Failed to open " + file);
		}
	}
	
	public void reloadFile()
	{
		try
		{
			//We reload the file in case it has been changed. This way we can tune values without rebooting the rio
			config.load(new FileReader(file));
		}
		catch (IOException e)
		{
			System.err.println("WARNING: Failed to open " + file);
		}
	}
	
	public double readDouble(String key, double backup)
	{ 

		double value;
		try
		{
			value = Double.parseDouble(config.getProperty(key, String.valueOf(backup)));
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			value = backup;
		}
		return value;
	}
	
	public double readDouble(String key)
	{
		return readDouble(key, 0);
	}
	
	public int readInt(String key, int backup)
	{ 

		int value;
		try
		{
			value = Integer.parseInt(config.getProperty(key, String.valueOf(backup)));
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			value = backup;
		}
		return value;
	}
	
	public int readInt(String key)
	{
		return readInt(key, 0);
	}
	
	public boolean readBoolean(String key, boolean backup)
	{
		boolean value;
		String raw = config.getProperty(key, String.valueOf(backup));
		
		if(raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")) value = Boolean.valueOf(raw);
		else value = backup;
		
		return value;
	}
	public boolean readBoolean(String key)
	{
		return readBoolean(key, false);
	}
}
