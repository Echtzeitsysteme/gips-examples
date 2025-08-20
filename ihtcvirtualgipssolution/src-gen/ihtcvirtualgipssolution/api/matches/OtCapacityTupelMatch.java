package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.OtCapacityTupelPattern;
import ihtcvirtualmetamodel.Capacity;
import ihtcvirtualmetamodel.OT;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>otCapacityTupel()</code>.
 */
public class OtCapacityTupelMatch extends GraphTransformationMatch<OtCapacityTupelMatch, OtCapacityTupelPattern> {
	private Capacity varC;
	private OT varOt;

	/**
	 * Creates a new match for the pattern <code>otCapacityTupel()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public OtCapacityTupelMatch(final OtCapacityTupelPattern pattern, final IMatch match) {
		super(pattern, match);
		varC = (Capacity) match.get("c");
		varOt = (OT) match.get("ot");
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
	 * Returns the ot.
	 *
	 * @return the ot
	 */
	public OT getOt() {
		return varOt;
	}

	@Override
	public String toString() {
		String s = "match {" + java.lang.System.lineSeparator();
		s += "	c --> " + varC + java.lang.System.lineSeparator();
		s += "	ot --> " + varOt + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
