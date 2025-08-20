package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.SelectShiftToRosterRule;
import ihtcvirtualmetamodel.VirtualShiftToRoster;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the rule <code>selectShiftToRoster()</code>.
 */
public class SelectShiftToRosterMatch extends GraphTransformationMatch<SelectShiftToRosterMatch, SelectShiftToRosterRule> {
	private VirtualShiftToRoster varVsr;

	/**
	 * Creates a new match for the rule <code>selectShiftToRoster()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public SelectShiftToRosterMatch(final SelectShiftToRosterRule pattern, final IMatch match) {
		super(pattern, match);
		varVsr = (VirtualShiftToRoster) match.get("vsr");
	}

	/**
	 * Returns the vsr.
	 *
	 * @return the vsr
	 */
	public VirtualShiftToRoster getVsr() {
		return varVsr;
	}

	@Override
	public String toString() {
		String s = "match {" + java.lang.System.lineSeparator();
		s += "	vsr --> " + varVsr + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
