/**
 * If robot crashes into an object (like a wall or ball), it checks if it is
 * really an object that cannot be moved; if yes, the robot turns, so it doesn't
 * crash into the object again.
 */
public class AbandonObjectBehaviour extends Behaviour {
	private State state = State.NO_CRASH_YET;

	private long startOfAbandoningAction = 0;
	private static final long ABANDONING_DURATION_BACKOFF = 200;	// [ms] time for backwards driving
	private static final long ABANDONING_DURATION_RETURN = 300;		// [ms] time for forwards driving
	private static final long ABANDONING_DURATION_TURN = 750;		// [ms] time for turning

	public AbandonObjectBehaviour(BaseRobot baseRobot) {
		super(baseRobot);
	}

	@Override
	public boolean update() {
        final long millisSinceStartOfAbandoningAction = _baseRobot.getTimeMillis() - startOfAbandoningAction;
		if (millisSinceStartOfAbandoningAction < ABANDONING_DURATION_BACKOFF) {
			_baseRobot.driveBackwards();
			return true;
		}

		if (millisSinceStartOfAbandoningAction < ABANDONING_DURATION_BACKOFF + ABANDONING_DURATION_RETURN) {
			_baseRobot.driveForwardMaxSpeed();
			return true;
		}

		if (state == State.CRASH_CONFIRMED && millisSinceStartOfAbandoningAction < ABANDONING_DURATION_BACKOFF + ABANDONING_DURATION_RETURN + ABANDONING_DURATION_TURN) {
			this._baseRobot.turnAroundLeft();
			return true;
		}

		double accelerometerY = Math.abs(_baseRobot.getAccelerometerDataSmoothedAverage(Axis.Y));
		if (accelerometerY > 2) {	// impact
			if (state == State.NO_CRASH_YET) {			// first one
				startOfAbandoningAction = _baseRobot.getTimeMillis();
				_baseRobot.driveBackwards();
				state = State.CRASH_DETECTED;
			} else if (state == State.CRASH_DETECTED) {	// second one -> it's a (ball stuck at a) wall
				state = State.CRASH_CONFIRMED;
			}
			return true;
		}

		// we didn't crash into something or we crashed into a ball that now moved away
		state = State.NO_CRASH_YET;

		return false;
	}

	private enum State {
		NO_CRASH_YET, CRASH_DETECTED, CRASH_CONFIRMED
	}
}
