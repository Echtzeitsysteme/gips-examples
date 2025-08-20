package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.OptionalPatientsPattern;
import ihtcvirtualmetamodel.Patient;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>optionalPatients()</code>.
 */
public class OptionalPatientsMatch extends GraphTransformationMatch<OptionalPatientsMatch, OptionalPatientsPattern> {
	private Patient varP;

	/**
	 * Creates a new match for the pattern <code>optionalPatients()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public OptionalPatientsMatch(final OptionalPatientsPattern pattern, final IMatch match) {
		super(pattern, match);
		varP = (Patient) match.get("p");
	}

	/**
	 * Returns the p.
	 *
	 * @return the p
	 */
	public Patient getP() {
		return varP;
	}

	@Override
	public String toString() {
		String s = "match {" + java.lang.System.lineSeparator();
		s += "	p --> " + varP + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
