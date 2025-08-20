package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.SurgeonOTForDayPattern;
import ihtcvirtualmetamodel.OpTime;
import ihtcvirtualmetamodel.OT;
import ihtcvirtualmetamodel.Surgeon;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>surgeonOTForDay()</code>.
 */
public class SurgeonOTForDayMatch extends GraphTransformationMatch<SurgeonOTForDayMatch, SurgeonOTForDayPattern> {
	private OpTime varOp;
	private OT varOt;
	private Surgeon varS;

	/**
	 * Creates a new match for the pattern <code>surgeonOTForDay()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public SurgeonOTForDayMatch(final SurgeonOTForDayPattern pattern, final IMatch match) {
		super(pattern, match);
		varOp = (OpTime) match.get("op");
		varOt = (OT) match.get("ot");
		varS = (Surgeon) match.get("s");
	}

	/**
	 * Returns the op.
	 *
	 * @return the op
	 */
	public OpTime getOp() {
		return varOp;
	}

	/**
	 * Returns the ot.
	 *
	 * @return the ot
	 */
	public OT getOt() {
		return varOt;
	}

	/**
	 * Returns the s.
	 *
	 * @return the s
	 */
	public Surgeon getS() {
		return varS;
	}

	@Override
	public String toString() {
		String s = "match {" + java.lang.System.lineSeparator();
		s += "	op --> " + varOp + java.lang.System.lineSeparator();
		s += "	ot --> " + varOt + java.lang.System.lineSeparator();
		s += "	s --> " + varS + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
