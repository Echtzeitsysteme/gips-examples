package teachingassistant.kcl.metamodelalt.comparator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import metamodel.TaAllocation;
import teachingassistant.kcl.gips.utils.LoggingUtils;
import teachingassistant.kcl.gips.utils.Tuple;

public class SolutionComparator {

	/**
	 * Logger for system outputs.
	 */
	protected final static Logger logger = Logger.getLogger(SolutionComparator.class.getName());
	
	static {
		LoggingUtils.configureLogging(logger);
	}

	/**
	 * Compares to given solutions regarding the number of identical mappings
	 * chosen.
	 * 
	 * @param first  First solution.
	 * @param second Second solution.
	 */
	public static void compareSolutions(final TaAllocation first, final TaAllocation second) {
		Objects.requireNonNull(first);
		Objects.requireNonNull(second);

		final Set<Tuple<String, String>> firstTuples = new HashSet<>();
		first.getModules().forEach(module -> module.getSessions()
				.forEach(session -> session.getOccurrences().forEach(occ -> occ.getTas().forEach(ta -> {
					firstTuples.add(new Tuple<String, String>(occ.getName(), ta.getName()));
				}))));

		final Set<Tuple<String, String>> secondTuples = new HashSet<>();
		second.getModules().forEach(module -> module.getSessions()
				.forEach(session -> session.getOccurrences().forEach(occ -> occ.getTas().forEach(ta -> {
					secondTuples.add(new Tuple<String, String>(occ.getName(), ta.getName()));
				}))));

		// Sanity check: both sets must be equal in size
		if (firstTuples.size() != secondTuples.size()) {
			throw new InternalError("Set sizes are different: " + firstTuples.size() + " vs. " + secondTuples.size());
		}

		// Count identical tuples
		int counter = 0;
		for (final Tuple<String, String> t : secondTuples) {
			for (final Tuple<String, String> tOrig : firstTuples) {
				if (t.equals(tOrig)) {
					counter++;
					break;
				}
			}
		}

		logger.info(counter + " out of " + firstTuples.size() + " mappings were identical.");
	}

}
