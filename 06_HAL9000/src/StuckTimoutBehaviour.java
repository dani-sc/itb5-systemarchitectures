import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.stream.DoubleStream;

/**
 * Effective when the robot is stuck on a wall.
 *
 * Detects that the robot is stuck and tries to back off.
 *
 * TODO: detecting being stuck and backing off are two different tasks.
 *       probably we should split this class, so that other behaviors can uses
 *       them separately.
 */
public class StuckTimoutBehaviour extends Behaviour {
	private static final int SIZE_VALUE_HISTORY = 250;
	private static final int STUCK_VARIANCE_THRESHOLD = 100000000;
	private static final int LIBERATION_DURATION = 750;	// [ms]

	private long startOfLiberationAction;

	private Queue<Double> valueHistory = new ArrayDeque<>();	// currently distance sensors + speed

	public StuckTimoutBehaviour(BaseRobot baseRobot) {
		super(baseRobot);
		_baseRobot.getCamera("camera").enable(_baseRobot.SENSOR_READING_INTERVALL);
	}

  /**
   * TODO: find way to implement this behavior without depending on the environment.
   *       currently, the approach using the camera depends on the ball's color.
   */
	@Override
	public boolean update() {
		// Version which uses distance sensor and wheel values instead of the camera:
//		double[] distanceSensorData = _baseRobot.getDistanceSensorDataSmoothed(Sensor.FRONT_R, Sensor.FRONT_RIGHT, Sensor.RIGHT, Sensor.BACK_RIGHT, Sensor.BACK_LEFT, Sensor.LEFT, Sensor.FRONT_LEFT, Sensor.FRONT_L);
//		double distanceSensorAvg = Arrays.stream(distanceSensorData).average().orElse(0d);
//
//		double speedAvg = (_baseRobot.getLeftSpeed() + _baseRobot.getRightSpeed()) / 4d;
//
//		valueHistory.add(distanceSensorAvg + speedAvg);

		// Version which uses the camera data as opposed to distance sensor and wheel values:
		valueHistory.add(Arrays.stream(_baseRobot.getCamera("camera").getImage()).average().orElse(0d));

		if (valueHistory.size() > SIZE_VALUE_HISTORY) {
			valueHistory.poll();
		} else {
			return false;
		}

		if (_baseRobot.getTimeMillis() - startOfLiberationAction < LIBERATION_DURATION) {
			return true;
		}

		if (getVariance(valueHistory) <= STUCK_VARIANCE_THRESHOLD) {
			// we're probably stuck somewhere; let's drive randomly for some time
			startOfLiberationAction = _baseRobot.getTimeMillis();
			setRandomBackwardsSpeed();
			return true;
		}

		return false;
	}

    /**
     * TODO: this method isn't specific to this behavior. move out from here
     */
    private double getVariance(Queue<Double> queue)
    {
    	DoubleStream dS = queue.stream().mapToDouble(d -> d);
        double mean = dS.average().orElse(0d);
        double temp = 0d;
        for(double a : queue)
            temp += (mean-a)*(mean-a);
        return temp/queue.size();
    }

	private void setRandomBackwardsSpeed() {
		double rnd = Math.random();
		if (rnd < 0.5d) {
			_baseRobot.setSpeed(-_baseRobot.MAX_SPEED, -_baseRobot.MAX_SPEED/2d);
		} else {
			_baseRobot.setSpeed(-_baseRobot.MAX_SPEED/2d, -_baseRobot.MAX_SPEED);
		}
	}
}
