package org.gips.examples.incrementalp2p.common;

public final class TimeAggregator {

	/**
	 * Aggregated ILP runtime in nanoseconds.
	 */
	private static long ilpTime = 0;

	/**
	 * Aggregated GT runtime in nanoseconds.
	 */
	private static long gtTime = 0;

	/**
	 * Last recorded ILP tick time stamp.
	 */
	private static long ilpTick = 0;

	/**
	 * Last recorded GT tick time stamp.
	 */
	private static long gtTick = 0;

	private TimeAggregator() {
	}

	/**
	 * Resets all values.
	 */
	public static void reset() {
		ilpTime = 0;
		gtTime = 0;
		ilpTick = 0;
		gtTick = 0;
	}

	/**
	 * Register the current time stamp as ILP start.
	 */
	public static void ilpTick() {
		if (ilpTick != 0) {
			throw new UnsupportedOperationException();
		}

		ilpTick = now();
	}

	/**
	 * Register the current time stamp as ILP end.
	 */
	public static void ilpTock() {
		if (ilpTick == 0) {
			throw new UnsupportedOperationException();
		}
		ilpTime += (now() - ilpTick);
		ilpTick = 0;
	}

	/**
	 * Register the current time stamp as GT start.
	 */
	public static void gtTick() {
		if (gtTick != 0) {
			throw new UnsupportedOperationException();
		}

		gtTick = now();
	}

	/**
	 * Register the current time stamp as GT end.
	 */
	public static void gtTock() {
		if (gtTick == 0) {
			throw new UnsupportedOperationException();
		}
		gtTime += (now() - gtTick);
		gtTick = 0;
	}

	/**
	 * Return the aggregated ILP time in milliseconds.
	 * 
	 * @return Aggregated ILP time in milliseconds.
	 */
	public static double getIlpTimeMillis() {
		return 1.0 * ilpTime / 1_000_000;
	}

	/**
	 * Return the aggregated GT time in milliseconds.
	 * 
	 * @return Aggregated GT time in milliseconds.
	 */
	public static double getGtTimeMillis() {
		return 1.0 * gtTime / 1_000_000;
	}

	/**
	 * Returns the current time stamp as nanoseconds.
	 * 
	 * @return Current time stamp as nanoseconds.
	 */
	private static long now() {
		return System.nanoTime();
	}

}
