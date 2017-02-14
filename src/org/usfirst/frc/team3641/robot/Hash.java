package org.usfirst.frc.team3641.robot;
import java.nio.charset.StandardCharsets;

public class Hash
{
	public static boolean checkJOAATHash(String msg, int hash)
	{
		return (hashJOAAT(msg) == hash);
	}
	
	public static int hashJOAAT(String msg)
	{
		byte[] key = msg.getBytes(StandardCharsets.UTF_8);
		
		int hash = 0;
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
