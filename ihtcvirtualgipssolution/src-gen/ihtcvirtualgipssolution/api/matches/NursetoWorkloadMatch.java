package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.NursetoWorkloadPattern;
import ihtcvirtualmetamodel.Shift;
import ihtcvirtualmetamodel.VirtualShiftToRoster;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>nursetoWorkload()</code>.
 */
public class NursetoWorkloadMatch extends GraphTransformationMatch<NursetoWorkloadMatch, NursetoWorkloadPattern> {
	private Shift varS;
	private VirtualShiftToRoster varVsr;
	private VirtualShiftToWorkload varVsw;

	/**
	 * Creates a new match for the pattern <code>nursetoWorkload()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public NursetoWorkloadMatch(final NursetoWorkloadPattern pattern, final IMatch match) {
		super(pattern, match);
		varS = (Shift) match.get("s");
		varVsr = (VirtualShiftToRoster) match.get("vsr");
		varVsw = (VirtualShiftToWorkload) match.get("vsw");
	}

	/**
	 * Returns the s.
	 *
	 * @return the s
	 */
	public Shift getS() {
		return varS;
	}

	/**
	 * Returns the vsr.
	 *
	 * @return the vsr
	 */
	public VirtualShiftToRoster getVsr() {
		return varVsr;
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
		s += "	s --> " + varS + java.lang.System.lineSeparator();
		s += "	vsr --> " + varVsr + java.lang.System.lineSeparator();
		s += "	vsw --> " + varVsw + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
