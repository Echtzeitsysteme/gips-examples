package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.SelectExtendingShiftToFirstWorkloadRule;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the rule <code>selectExtendingShiftToFirstWorkload()</code>.
 */
public class SelectExtendingShiftToFirstWorkloadMatch extends GraphTransformationMatch<SelectExtendingShiftToFirstWorkloadMatch, SelectExtendingShiftToFirstWorkloadRule> {
	private VirtualShiftToWorkload varNextvsw;
	private VirtualShiftToWorkload varPrevvsw;

	/**
	 * Creates a new match for the rule <code>selectExtendingShiftToFirstWorkload()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public SelectExtendingShiftToFirstWorkloadMatch(final SelectExtendingShiftToFirstWorkloadRule pattern, final IMatch match) {
		super(pattern, match);
		varNextvsw = (VirtualShiftToWorkload) match.get("nextvsw");
		varPrevvsw = (VirtualShiftToWorkload) match.get("prevvsw");
	}

	/**
	 * Returns the nextvsw.
	 *
	 * @return the nextvsw
	 */
	public VirtualShiftToWorkload getNextvsw() {
		return varNextvsw;
	}

	/**
	 * Returns the prevvsw.
	 *
	 * @return the prevvsw
	 */
	public VirtualShiftToWorkload getPrevvsw() {
		return varPrevvsw;
	}

	@Override
	public String toString() {
		String s = "match {" + java.lang.System.lineSeparator();
		s += "	nextvsw --> " + varNextvsw + java.lang.System.lineSeparator();
		s += "	prevvsw --> " + varPrevvsw + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
