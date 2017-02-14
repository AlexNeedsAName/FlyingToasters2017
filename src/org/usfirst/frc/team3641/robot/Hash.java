package org.usfirst.frc.team3641.robot;
import java.nio.charset.StandardCharsets;

public class Hash
{
	public static boolean verifyOneAtATime(String msg, long hash)
	{
		return (oneAtATime(msg) == hash);
	}
	
	public static long oneAtATime(String msg)
	{
		byte[] key = msg.getBytes(StandardCharsets.UTF_8);
		
		long hash = 0;
		for (byte b : key)
		{
			System.out.println((b & 0xFF) + "");
			hash += (b & 0xFF);
			hash += (hash << 10);
			System.out.println(hash);
			hash ^= (hash >>> 6);
			System.out.println(hash);
			System.out.println("loop");
		}
		hash += (hash << 3);
		hash ^= (hash >>> 11);
		hash += (hash << 15);
		System.out.println("Msg: " + hash);
		return hash;
	}

}
