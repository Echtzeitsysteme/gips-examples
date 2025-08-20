package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.NurseRosterTupelPattern;
import ihtcvirtualmetamodel.Nurse;
import ihtcvirtualmetamodel.Roster;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>nurseRosterTupel()</code>.
 */
public class NurseRosterTupelMatch extends GraphTransformationMatch<NurseRosterTupelMatch, NurseRosterTupelPattern> {
	private Nurse varN;
	private Roster varRo;

	/**
	 * Creates a new match for the pattern <code>nurseRosterTupel()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public NurseRosterTupelMatch(final NurseRosterTupelPattern pattern, final IMatch match) {
		super(pattern, match);
		varN = (Nurse) match.get("n");
		varRo = (Roster) match.get("ro");
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
	 * Returns the ro.
	 *
	 * @return the ro
	 */
	public Roster getRo() {
		return varRo;
	}

	@Override
	public String toString() {
		String s = "match {" + java.lang.System.lineSeparator();
		s += "	n --> " + varN + java.lang.System.lineSeparator();
		s += "	ro --> " + varRo + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
