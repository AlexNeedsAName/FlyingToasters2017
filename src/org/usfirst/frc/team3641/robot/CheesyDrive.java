package org.usfirst.frc.team3641.robot;
	/*
	 * This is pretty much a line for line copy of CheesyDriveHelper developed by FRC team 254 in 2016
	 * https://github.com/Team254/FRC-2016-Public/blob/master/src/com/team254/frc2016/CheesyDriveHelper.java
	 * 
	 * The purpose of this class is to take stick controls and use them to change the robot's path curve 
	 * rather than the robot's turning rate.
	 */
public class CheesyDrive {
	double mQuickStopAccumulator;
    public static final double kThrottleDeadband = 0.02;
    private static final double kTurnDeadband = 0.02;
    private static final double kTurnSensitivity = 1.0;
    
	public void chezyDrive(double throttle, double turning, boolean turnInPlace){
		
		turning = handleDeadband(turning, kTurnDeadband);
		throttle = handleDeadband(throttle, kThrottleDeadband);
		
		turning = Teleop.squareInput(turning, 1.5);//Math.E);
		throttle = Teleop.squareInput(throttle, 1.5);//Math.E);
		
		if(throttle == 0)
		{
			turnInPlace = true;
		}
		
		double overPower;
		double angularPower;
		
		if(turnInPlace){
			if(Math.abs(throttle) < 0.2){
				double alpha = 0.1;
				mQuickStopAccumulator = (1 - alpha) * mQuickStopAccumulator + alpha * limit(turning, 1.0) * 2;
			}
			overPower = 1.0;
			angularPower = turning;
			
		}else{
			overPower = 0.0;
			angularPower = Math.abs(throttle) * turning * kTurnSensitivity - mQuickStopAccumulator;
			
			if (mQuickStopAccumulator > 1) {
				mQuickStopAccumulator -= 1;
			} else if (mQuickStopAccumulator < -1) {
            mQuickStopAccumulator += 1;
			} else {
				mQuickStopAccumulator = 0.0;
			}
		}
		
		double leftPower = throttle + angularPower;
		double rightPower = throttle - angularPower;
		
		if (leftPower > 1.0) {
			rightPower -= overPower * (leftPower - 1.0);
            leftPower = 1.0;
        } else if (rightPower > 1.0) {
        	leftPower -= overPower * (rightPower - 1.0);
        	rightPower = 1.0;
        } else if (leftPower < -1.0) {
        	rightPower += overPower * (-1.0 - leftPower);
            leftPower = -1.0;
        } else if (rightPower < -1.0) {
        	leftPower += overPower * (-1.0 - rightPower);
        	rightPower = -1.0;
        }
		
		DriveBase.driveTank(leftPower, rightPower);
	}
	
	public double handleDeadband(double val, double deadband) {
        return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
    }
	
	public static double limit(double v, double limit) {
        return (Math.abs(v) < limit) ? v : limit * (v < 0 ? -1 : 1);
    }
}
