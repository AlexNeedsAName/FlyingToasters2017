package org.usfirst.frc.team3641.robot.util;

import javax.net.ssl.SSLEngineResult.Status;

import org.usfirst.frc.team3641.robot.Constants;
import org.usfirst.frc.team3641.robot.DriveBase;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

public class DriveMotionProfile {
	
	private int state = 0;
	
	private boolean startProfile = false;
	
	
	private double[] leftVelocities = PathReader.LeftVelocities;
	private double[] rightVelocities = PathReader.RightVelocities;
	private double[] sampleRate = PathReader.sampleRate;
	
	private CANTalon Left = DriveBase.left.getFeedbackTalon();
	private CANTalon Right = DriveBase.right.getFeedbackTalon();
	
	private CANTalon.SetValueMotionProfile val = CANTalon.SetValueMotionProfile.Disable;
	
	private CANTalon.MotionProfileStatus leftStatus = new CANTalon.MotionProfileStatus();
	private CANTalon.MotionProfileStatus rightStatus = new CANTalon.MotionProfileStatus();
	
	private final int kMinPointsInTalon = 5;
	
	private static final int kNumLoopsTimeout = 10;
	
	public void initProfile(){
		Left.changeMotionControlFramePeriod(5);
		Right.changeMotionControlFramePeriod(5);
	}
	
	public void reset(){
		Left.clearMotionProfileTrajectories();
		Right.clearMotionProfileTrajectories();
		
		val = CANTalon.SetValueMotionProfile.Disable;
	}
	
	public void fillTalonBuffer(){
		CANTalon.TrajectoryPoint currentPoint = new CANTalon.TrajectoryPoint();
		
		Left.clearMotionProfileTrajectories();
		Right.clearMotionProfileTrajectories();
		
		for(int i = 0; i < leftVelocities.length; i++){
			currentPoint.velocity = leftVelocities[i];
			currentPoint.timeDurMs = (int) sampleRate[i];
			currentPoint.profileSlotSelect = 0;
			currentPoint.velocityOnly = true;
			
			currentPoint.zeroPos = false;
			if(i == 0)currentPoint.zeroPos = true;
			
			if((i+1) == leftVelocities.length) currentPoint.isLastPoint = true;
			
			Left.pushMotionProfileTrajectory(currentPoint);
		}
		
		for(int i = 0; i < rightVelocities.length; i++){
			currentPoint.velocity = rightVelocities[i];
			currentPoint.timeDurMs = (int) sampleRate[i];
			currentPoint.profileSlotSelect = 0;
			currentPoint.velocityOnly = true;
			
			currentPoint.zeroPos = false;
			if(i == 0)currentPoint.zeroPos = true;
			
			if((i+1) == rightVelocities.length) currentPoint.isLastPoint = true;
			
			Right.pushMotionProfileTrajectory(currentPoint);
		}
	}
	
	public void control(){
		Left.getMotionProfileStatus(leftStatus);
		Right.getMotionProfileStatus(rightStatus);
		
		if(Left.getControlMode() != TalonControlMode.MotionProfile 
				&& Right.getControlMode() != TalonControlMode.MotionProfile){
			state = 0;
		}else{
			switch(state){
			case 0: if (startProfile){
					startProfile = false;
					
					val = CANTalon.SetValueMotionProfile.Disable;
					fillTalonBuffer();
					
					state = 1;
				}
				break;
			case 1: if (leftStatus.btmBufferCnt > kMinPointsInTalon || rightStatus.btmBufferCnt > kMinPointsInTalon){
					val = CANTalon.SetValueMotionProfile.Enable;
					state = 2;
				}	
				break;
			case 2: if(leftStatus.isUnderrun == false && rightStatus.isUnderrun == false){
					
				}
				if((leftStatus.activePointValid && leftStatus.activePoint.isLastPoint) || (rightStatus.activePointValid && rightStatus.activePoint.isLastPoint)){
					val = CANTalon.SetValueMotionProfile.Hold;
					state = 0;
				}
				break;
			}
		}
	}
	
	public void startMotionProfile(){
		startProfile = true;
	}
	
}
