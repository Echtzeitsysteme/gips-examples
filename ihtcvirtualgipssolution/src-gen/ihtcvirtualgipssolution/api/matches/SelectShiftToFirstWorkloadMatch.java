package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.SelectShiftToFirstWorkloadRule;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import ihtcvirtualmetamodel.VirtualWorkloadToCapacity;
import ihtcvirtualmetamodel.Workload;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the rule <code>selectShiftToFirstWorkload()</code>.
 */
public class SelectShiftToFirstWorkloadMatch extends GraphTransformationMatch<SelectShiftToFirstWorkloadMatch, SelectShiftToFirstWorkloadRule> {
	private Patient varP;
	private VirtualShiftToWorkload varVsw;
	private VirtualWorkloadToCapacity varVwc;
	private Workload varW;

	/**
	 * Creates a new match for the rule <code>selectShiftToFirstWorkload()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public SelectShiftToFirstWorkloadMatch(final SelectShiftToFirstWorkloadRule pattern, final IMatch match) {
		super(pattern, match);
		varP = (Patient) match.get("p");
		varVsw = (VirtualShiftToWorkload) match.get("vsw");
		varVwc = (VirtualWorkloadToCapacity) match.get("vwc");
		varW = (Workload) match.get("w");
	}

	/**
	 * Returns the p.
	 *
	 * @return the p
	 */
	public Patient getP() {
		return varP;
	}

	/**
	 * Returns the vsw.
	 *
	 * @return the vsw
	 */
	public VirtualShiftToWorkload getVsw() {
		return varVsw;
	}

	/**
	 * Returns the vwc.
	 *
	 * @return the vwc
	 */
	public VirtualWorkloadToCapacity getVwc() {
		return varVwc;
	}

	/**
	 * Returns the w.
	 *
	 * @return the w
	 */
	public Workload getW() {
		return varW;
	}

	@Override
	public String toString() {
		String s = "match {" + java.lang.System.lineSeparator();
		s += "	p --> " + varP + java.lang.System.lineSeparator();
		s += "	vsw --> " + varVsw + java.lang.System.lineSeparator();
		s += "	vwc --> " + varVwc + java.lang.System.lineSeparator();
		s += "	w --> " + varW + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
