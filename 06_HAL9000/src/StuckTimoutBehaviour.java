import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.stream.DoubleStream;

public class StuckTimoutBehaviour extends Behaviour {
	private static final int SIZE_VALUE_HISTORY = 250;
	private static final int STUCK_VARIANCE_THRESHOLD = 100000000;
	private static final int LIBERATION_DURATION = 1500;	// [ms]
	
	private long startOfLiberationAction;
	
	private Queue<Double> valueHistory = new ArrayDeque<>();	// currently distance sensors + speed

	public StuckTimoutBehaviour(BaseRobot baseRobot) {
		super(baseRobot);
		_baseRobot.getCamera("camera").enable(_baseRobot.SENSOR_READING_INTERVALL);
	}

	@Override
	public boolean update() {
//		double[] distanceSensorData = _baseRobot.getDistanceSensorDataSmoothed(Sensor.FRONT_R, Sensor.FRONT_RIGHT, Sensor.RIGHT, Sensor.BACK_RIGHT, Sensor.BACK_LEFT, Sensor.LEFT, Sensor.FRONT_LEFT, Sensor.FRONT_L);
//		double distanceSensorAvg = Arrays.stream(distanceSensorData).average().orElse(0d);
//		
//		double speedAvg = (_baseRobot.getLeftSpeed() + _baseRobot.getRightSpeed()) / 4d;
//		
//		valueHistory.add(distanceSensorAvg + speedAvg);
		
		valueHistory.add(Arrays.stream(_baseRobot.getCamera("camera").getImage()).average().orElse(0d));
		
		if (valueHistory.size() > SIZE_VALUE_HISTORY) {
			valueHistory.poll();
		} else {
			return false;
		}
		
		if (_baseRobot.getTimeMillis() - startOfLiberationAction < LIBERATION_DURATION) {
//			changeSpeedRandomly();
			return true;
		}
		
		System.out.println(getVariance(valueHistory));
		
		if (getVariance(valueHistory) <= STUCK_VARIANCE_THRESHOLD) {
			// we're probably stuck somewhere; let's drive randomly for some time
			startOfLiberationAction = _baseRobot.getTimeMillis();
			setRandomSpeed();
			return true;
		}
		
		return false;
	}
	
    private double getVariance(Queue<Double> queue)
    {
    	DoubleStream dS = queue.stream().mapToDouble(d -> d);
        double mean = dS.average().orElse(0d);
        double temp = 0d;
        for(double a : queue)
            temp += (mean-a)*(mean-a);
        return temp/queue.size();
    }

	private void setRandomSpeed() {
		double rnd = Math.random();
		if (rnd < 0.5d) {
			_baseRobot.setSpeedReal(-_baseRobot.MAX_SPEED, -_baseRobot.MAX_SPEED/2d);
		} else {
			_baseRobot.setSpeedReal(-_baseRobot.MAX_SPEED/2d, -_baseRobot.MAX_SPEED);
		}
//		_baseRobot.setSpeedReal((Math.random() * 2 - 1) * _baseRobot.MAX_SPEED, (Math.random() * 2 - 1) * _baseRobot.MAX_SPEED);
//		System.out.println("speed set to " + _baseRobot.getRightSpeed() + ", " + _baseRobot.getLeftSpeed());
	}
	
	private void changeSpeedRandomly() {
		double left = Math.max(-_baseRobot.MAX_SPEED, Math.min(_baseRobot.MAX_SPEED, _baseRobot.getLeftSpeed() + (Math.random() * 2 - 1) * 50d));
		double right = Math.max(-_baseRobot.MAX_SPEED, Math.min(_baseRobot.MAX_SPEED, _baseRobot.getRightSpeed() + (Math.random() * 2 - 1) * 50d));
		
//		System.out.println("new speed: " + right + ", " + left);
		_baseRobot.setSpeedReal(left, right);
	}
}
