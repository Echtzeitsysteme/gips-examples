package teachingassistant.uni.batch.runner;

import java.util.Objects;
import java.util.logging.Logger;

import teachingassistant.uni.batch.api.gips.BatchGipsAPI;
import teachingassistant.uni.utils.LoggingUtils;

public class TaApprovalObjectiveCalculator {

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Creates a new object of the class and initializes logging.
	 */
	public TaApprovalObjectiveCalculator() {
		LoggingUtils.configureLogging(logger);
	}

	/**
	 * Calculates and prints the sum of the overall employment rating of the given
	 * GIPS API object.
	 * 
	 * @param gipsApi GIPS API object.
	 * @return Overall employment rating.
	 */
	public int print(final BatchGipsAPI gipsApi) {
		Objects.requireNonNull(gipsApi);

		int sum = 0;
		for (final var m : gipsApi.getTaToOccurrence().getNonZeroVariableMappings()) {
			sum += m.getApproval().getRatingNumeric();
		}
		logger.info("\tEmployment rating value: " + sum);
		return sum;
	}

}
