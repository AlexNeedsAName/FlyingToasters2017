package org.usfirst.frc.team3641.robot.util;

import java.io.*;

public class PathReader {
	
	public static double[] LeftVelocities;
	public static double[] RightVelocities;
	public static double[] sampleRate;
	
	public static double[][] readFile(){
		String csvFile = "Velocities.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		try{
			br = new BufferedReader(new FileReader(csvFile));
			int i = 0;
			while ((line = br.readLine()) != null){
				String[] velocities = line.split(cvsSplitBy);
				
				LeftVelocities[i] = Double.parseDouble(velocities[0]);
				RightVelocities[i] = Double.parseDouble(velocities[1]);
				sampleRate[i] = Double.parseDouble(velocities[2]);
				i++;
			}
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			if (br != null){
				try{
					br.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public double[] getLeftVelocities(){
		return LeftVelocities;
	}
	
	public double[] getRightVelocities(){
		return RightVelocities;
	}
	
	public double[] getSampleRate(){
		return sampleRate;
	}
}
