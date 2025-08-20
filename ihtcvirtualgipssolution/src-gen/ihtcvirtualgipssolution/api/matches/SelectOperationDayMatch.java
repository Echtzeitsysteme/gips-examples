package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.SelectOperationDayRule;
import ihtcvirtualmetamodel.Capacity;
import ihtcvirtualmetamodel.OpTime;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.Surgeon;
import ihtcvirtualmetamodel.VirtualOpTimeToCapacity;
import ihtcvirtualmetamodel.VirtualWorkloadToCapacity;
import ihtcvirtualmetamodel.VirtualWorkloadToOpTime;
import ihtcvirtualmetamodel.Workload;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the rule <code>selectOperationDay()</code>.
 */
public class SelectOperationDayMatch extends GraphTransformationMatch<SelectOperationDayMatch, SelectOperationDayRule> {
	private Capacity varC;
	private OpTime varOpTime;
	private Patient varP;
	private Surgeon varS;
	private VirtualOpTimeToCapacity varVopc;
	private VirtualWorkloadToCapacity varVwc;
	private VirtualWorkloadToOpTime varVwop;
	private Workload varW;

	/**
	 * Creates a new match for the rule <code>selectOperationDay()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public SelectOperationDayMatch(final SelectOperationDayRule pattern, final IMatch match) {
		super(pattern, match);
		varC = (Capacity) match.get("c");
		varOpTime = (OpTime) match.get("opTime");
		varP = (Patient) match.get("p");
		varS = (Surgeon) match.get("s");
		varVopc = (VirtualOpTimeToCapacity) match.get("vopc");
		varVwc = (VirtualWorkloadToCapacity) match.get("vwc");
		varVwop = (VirtualWorkloadToOpTime) match.get("vwop");
		varW = (Workload) match.get("w");
	}

	/**
	 * Returns the c.
	 *
	 * @return the c
	 */
	public Capacity getC() {
		return varC;
	}

	/**
	 * Returns the opTime.
	 *
	 * @return the opTime
	 */
	public OpTime getOpTime() {
		return varOpTime;
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
	 * Returns the s.
	 *
	 * @return the s
	 */
	public Surgeon getS() {
		return varS;
	}

	/**
	 * Returns the vopc.
	 *
	 * @return the vopc
	 */
	public VirtualOpTimeToCapacity getVopc() {
		return varVopc;
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
	 * Returns the vwop.
	 *
	 * @return the vwop
	 */
	public VirtualWorkloadToOpTime getVwop() {
		return varVwop;
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
		s += "	c --> " + varC + java.lang.System.lineSeparator();
		s += "	opTime --> " + varOpTime + java.lang.System.lineSeparator();
		s += "	p --> " + varP + java.lang.System.lineSeparator();
		s += "	s --> " + varS + java.lang.System.lineSeparator();
		s += "	vopc --> " + varVopc + java.lang.System.lineSeparator();
		s += "	vwc --> " + varVwc + java.lang.System.lineSeparator();
		s += "	vwop --> " + varVwop + java.lang.System.lineSeparator();
		s += "	w --> " + varW + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
