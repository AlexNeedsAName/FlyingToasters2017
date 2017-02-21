package org.usfirst.frc.team3641.robot;
import java.nio.charset.StandardCharsets;

public class Hash
{
	/**
	 * Verify a one at a time hash.
	 * 
	 * @param msg The message.
	 * @param hash The hash to verify.
	 * @return True if the hash generated from the message matches the give hash.
	 */
	public static boolean verifyOneAtATime(String msg, short hash)
	{
		return (oneAtATime(msg) == hash);
	}
	
	/**
	 * Hash a message with the one at a time hash.
	 * 
	 * @param msg The message to hash.
	 * @return The hash of the message.
	 */
	public static short oneAtATime(String msg)
	{
		byte[] key = msg.getBytes(StandardCharsets.UTF_8);
		
		short hash = 0;
		for (byte b : key)
		{
			hash += (b & 0xFF);
			hash += (hash << 10);
			hash ^= (hash >>> 6);
		}
		hash += (hash << 3);
		hash ^= (hash >>> 11);
		hash += (hash << 15);
		return hash;
	}
}
