package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.NursePatientTupelPattern;
import ihtcvirtualmetamodel.Nurse;
import ihtcvirtualmetamodel.Patient;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>nursePatientTupel()</code>.
 */
public class NursePatientTupelMatch extends GraphTransformationMatch<NursePatientTupelMatch, NursePatientTupelPattern> {
	private Nurse varN;
	private Patient varP;

	/**
	 * Creates a new match for the pattern <code>nursePatientTupel()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public NursePatientTupelMatch(final NursePatientTupelPattern pattern, final IMatch match) {
		super(pattern, match);
		varN = (Nurse) match.get("n");
		varP = (Patient) match.get("p");
	}

	/**
	 * Returns the n.
	 *
	 * @return the n
	 */
	public Nurse getN() {
		return varN;
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
		s += "	n --> " + varN + java.lang.System.lineSeparator();
		s += "	p --> " + varP + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
