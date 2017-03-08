package org.usfirst.frc.team3641.robot;
import edu.wpi.first.wpilibj.Timer;

public class MotionProfile
{
	private String name;
	private Timer timer;
	private PID pid;
	
	private double startDistance;
	private double M, D, T;
	private static double K1, K2, K3;
	
	public MotionProfile(String name)
	{
		this.name = name;
		pid = new PID(this.name);
	}
	
	public MotionProfile()
	{
		this(null);
	}
	
	public void setup(double distance, double maxAccel)
	{
		startDistance = Sensors.getDriveDistance();
		
		D = distance;
		M = maxAccel;
		T = Math.sqrt((2*Math.PI*D)/M);
		K1 = 2*Math.PI/T;
		K2 = M/K1;
		K3 = 1/K1;
		pid.reset();
		timer.reset();
		timer.start();
	}
	
	private double getCurrentTarget()
	{
		double t = timer.get();
		double target = K2*(t-K3*Math.sin(K1*t));
		return target;
	}
	
	public boolean run(double threshold)
	{
		double target = getCurrentTarget() + startDistance;
		double current = Sensors.getDriveDistance();
		double error = target - current;
		pid.run(error, target);
		return (Math.abs(error) < threshold);
	}

}