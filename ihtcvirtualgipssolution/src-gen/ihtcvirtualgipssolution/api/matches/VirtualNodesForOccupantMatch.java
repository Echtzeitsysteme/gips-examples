package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.VirtualNodesForOccupantRule;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the rule <code>virtualNodesForOccupant()</code>.
 */
public class VirtualNodesForOccupantMatch extends GraphTransformationMatch<VirtualNodesForOccupantMatch, VirtualNodesForOccupantRule> {
	private VirtualShiftToWorkload varVsw;

	/**
	 * Creates a new match for the rule <code>virtualNodesForOccupant()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public VirtualNodesForOccupantMatch(final VirtualNodesForOccupantRule pattern, final IMatch match) {
		super(pattern, match);
		varVsw = (VirtualShiftToWorkload) match.get("vsw");
	}

	/**
	 * Returns the vsw.
	 *
	 * @return the vsw
	 */
	public VirtualShiftToWorkload getVsw() {
		return varVsw;
	}

	@Override
	public String toString() {
		String s = "match {" + java.lang.System.lineSeparator();
		s += "	vsw --> " + varVsw + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
