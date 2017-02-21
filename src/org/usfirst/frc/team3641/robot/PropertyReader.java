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
	
	/**
	 * Initalizes a new property reader.
	 * 
	 * @param name The name of the file.
	 */
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
	
	/**
	 * Reloads the config file.
	 */
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
	
	/**
	 * Reads a double from the config file.
	 * 
	 * @param key The key you want to read.
	 * @param backup The backup value to use if it can't read the specified key.
	 * @return The value of the key in the config file.
	 */
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
	
	/**
	 * Reads a double from the config file.
	 * If the key is not found/the file can't be read it defaults to 0.
	 * 
	 * @param key The key you want to read.
	 * @return The value of the specified key.
	 */
	public double readDouble(String key)
	{
		return readDouble(key, 0);
	}
	
	/**
	 * Reads a integer from the config file.
	 * 
	 * @param key The key you want to read.
	 * @param backup The backup value to use if it can't read the specified key.
	 * @return The value of the key in the config file.
	 */
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
	
	/**
	 * Reads a int from the config file.
	 * If the key is not found/the file can't be read it defaults to 0.
	 * 
	 * @param key The key you want to read.
	 * @return The value of the specified key.
	 */
	public int readInt(String key)
	{
		return readInt(key, 0);
	}
	
	/**
	 * Reads a boolean from the config file.
	 * 
	 * @param key The key you want to read.
	 * @param backup The backup value to use if it can't read the specified key.
	 * @return The value of the key in the config file.
	 */
	public boolean readBoolean(String key, boolean backup)
	{
		boolean value;
		String raw = config.getProperty(key, String.valueOf(backup));
		
		if(raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")) value = Boolean.valueOf(raw);
		else value = backup;
		
		return value;
	}
	
	/**
	 * Reads a boolean from the config file.
	 * If the key is not found/the file can't be read it defaults to false.
	 * 
	 * @param key The key you want to read.
	 * @return The value of the specified key.
	 */
	public boolean readBoolean(String key)
	{
		return readBoolean(key, false);
	}
}
