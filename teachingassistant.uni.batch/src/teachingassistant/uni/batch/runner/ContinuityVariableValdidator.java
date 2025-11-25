package teachingassistant.uni.batch.runner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import metamodel.SessionOccurrence;
import metamodel.TeachingAssistant;
import teachingassistant.uni.batch.api.gips.BatchGipsAPI;
import teachingassistant.uni.utils.LoggingUtils;

public class ContinuityVariableValdidator {

	/**
	 * Continuity factor `beta`.
	 */
	private final int beta = 1;

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Creates a new object of the class and initializes logging.
	 */
	public ContinuityVariableValdidator() {
		LoggingUtils.configureLogging(logger);
	}

	/**
	 * Verifies the correct continuity of the given GIPS API object. Therefore, the
	 * method checks that every continuity mapping with value `1` correctly
	 * represents two selected TA assignment mappings in week `w` and `w+1`. It also
	 * verifies the other way around, i.e., two TA assignment mappings in week `w`
	 * and `w+1` must trigger the respective continuity mapping (and set its value
	 * to `1`).
	 * 
	 * @param gipsApi
	 */
	public void verifyContinuity(final BatchGipsAPI gipsApi) {
		Objects.requireNonNull(gipsApi);

		logger.info("=> Start continuity verification.");

		final Map<TeachingAssistant, Set<SessionOccurrence>> index = buildContinuityIndex(gipsApi);

		// Continuity
		for (final var m : gipsApi.getContinuity().getNonZeroVariableMappings()) {
			if (!(isTaSelected(index, m.getTa(), m.getOccurrenceA())
					&& isTaSelected(index, m.getTa(), m.getOccurrenceB()))) {
				logger.warning("\tError: Continuity variable for TA <" + m.getTa().getName() + "> and SOs <"
						+ m.getOccurrenceA().getName() + "> <" + m.getOccurrenceB().getName()
						+ "> was incorrectly set to 0.");
			}
		}

		// No continuity
		for (final var m : gipsApi.getContinuity().getZeroVariableMappings()) {
			if (isTaSelected(index, m.getTa(), m.getOccurrenceA())
					&& isTaSelected(index, m.getTa(), m.getOccurrenceB())) {
				logger.warning("\tError: Continuity variable for TA <" + m.getTa().getName() + "> and SOs <"
						+ m.getOccurrenceA().getName() + "> <" + m.getOccurrenceB().getName()
						+ "> was incorrectly set to 1.");
			}
		}

		// Objective statistics
		logger.info("\tContinuity value: " + gipsApi.getContinuity().getNonZeroVariableMappings().size() * beta);
	}

	/**
	 * Returns true if the given TA was assigned to the given session occurrence as
	 * represented by the given assignment index.
	 * 
	 * @param index Assignment index.
	 * @param ta    Teaching assistant.
	 * @param so    Session Occurrence.
	 * @return True if `ta` is assigned to `so` as represented in index `index`.
	 */
	private boolean isTaSelected(final Map<TeachingAssistant, Set<SessionOccurrence>> index, final TeachingAssistant ta,
			final SessionOccurrence so) {
		Objects.requireNonNull(index);
		Objects.requireNonNull(ta);
		Objects.requireNonNull(so);

		if (!index.containsKey(ta)) {
			return false;
		}

		return index.get(ta).contains(so);
	}

	/**
	 * Builds the TA to session occurrence index of a given GIPS API object.
	 * 
	 * @param gipsApi GIPS API to build index for.
	 * @return TA to session occurrence index of the given GIPS API object.
	 */
	private Map<TeachingAssistant, Set<SessionOccurrence>> buildContinuityIndex(final BatchGipsAPI gipsApi) {
		Objects.requireNonNull(gipsApi);

		final Map<TeachingAssistant, Set<SessionOccurrence>> index = new HashMap<TeachingAssistant, Set<SessionOccurrence>>();
		for (final var m : gipsApi.getTaToOccurrence().getNonZeroVariableMappings()) {
			if (!index.containsKey(m.getTa())) {
				index.put(m.getTa(), new HashSet<SessionOccurrence>());
			}
			index.get(m.getTa()).add(m.getOccurrence());
		}
		return index;
	}

}
