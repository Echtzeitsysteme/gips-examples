package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.PatientForRoomPattern;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.Room;
import ihtcvirtualmetamodel.Shift;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import ihtcvirtualmetamodel.Workload;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>patientForRoom()</code>.
 */
public class PatientForRoomMatch extends GraphTransformationMatch<PatientForRoomMatch, PatientForRoomPattern> {
	private Patient varP;
	private Room varR;
	private Shift varS;
	private VirtualShiftToWorkload varVsw;
	private Workload varW;

	/**
	 * Creates a new match for the pattern <code>patientForRoom()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public PatientForRoomMatch(final PatientForRoomPattern pattern, final IMatch match) {
		super(pattern, match);
		varP = (Patient) match.get("p");
		varR = (Room) match.get("r");
		varS = (Shift) match.get("s");
		varVsw = (VirtualShiftToWorkload) match.get("vsw");
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
	 * Returns the r.
	 *
	 * @return the r
	 */
	public Room getR() {
		return varR;
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
	 * Returns the vsw.
	 *
	 * @return the vsw
	 */
	public VirtualShiftToWorkload getVsw() {
		return varVsw;
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
		s += "	r --> " + varR + java.lang.System.lineSeparator();
		s += "	s --> " + varS + java.lang.System.lineSeparator();
		s += "	vsw --> " + varVsw + java.lang.System.lineSeparator();
		s += "	w --> " + varW + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
