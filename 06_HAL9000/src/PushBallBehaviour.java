import java.util.Arrays;

public class PushBallBehaviour extends Behaviour {
	public PushBallBehaviour(BaseRobot baseRobot) {
		super(baseRobot);
	}

	@Override
	public boolean update() {
		double x = Math.abs(_baseRobot.getAccelerometerDataSmoothed(Axis.X));
		double y = Math.abs(_baseRobot.getAccelerometerDataSmoothed(Axis.Y));
		
		if(x < 0.003 || Math.abs(y) < 0.003) {
			System.out.println("STUCK: " + x + ", " + y );
		}
		
		double leftValue = Arrays.stream(_baseRobot.getDistanceSensorDataRaw(Sensor.FRONT_L, Sensor.FRONT_LEFT, Sensor.LEFT)).sum() / 3d;
		double centerValue = Arrays.stream(_baseRobot.getDistanceSensorDataRaw(Sensor.FRONT_L, Sensor.FRONT_R)).sum() / 2d;
		double rightValue = Arrays.stream(_baseRobot.getDistanceSensorDataRaw(Sensor.FRONT_R, Sensor.FRONT_RIGHT, Sensor.RIGHT)).sum() / 3d;
		
//		System.out.println("leftValue: " + leftValue + "\n\tcenterValue: " + centerValue + "\n\trightValue: " + rightValue);
		
		if (leftValue > centerValue && leftValue > rightValue) {
			_baseRobot.driveLeftMaxSpeed();
		} else if (centerValue > rightValue) {
			_baseRobot.driveForwardMaxSpeed();
		} else {
			_baseRobot.driveRightMaxSpeed();
		}
		
		return false;
	}
}
