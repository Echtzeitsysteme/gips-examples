package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.SurgeonOptimeTupelPattern;
import ihtcvirtualmetamodel.OpTime;
import ihtcvirtualmetamodel.Surgeon;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>surgeonOptimeTupel()</code>.
 */
public class SurgeonOptimeTupelMatch extends GraphTransformationMatch<SurgeonOptimeTupelMatch, SurgeonOptimeTupelPattern> {
	private OpTime varOp;
	private Surgeon varS;

	/**
	 * Creates a new match for the pattern <code>surgeonOptimeTupel()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public SurgeonOptimeTupelMatch(final SurgeonOptimeTupelPattern pattern, final IMatch match) {
		super(pattern, match);
		varOp = (OpTime) match.get("op");
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
		s += "	s --> " + varS + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
