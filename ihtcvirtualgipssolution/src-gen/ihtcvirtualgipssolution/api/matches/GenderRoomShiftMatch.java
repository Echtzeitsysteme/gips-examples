package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.GenderRoomShiftPattern;
import ihtcvirtualmetamodel.Gender;
import ihtcvirtualmetamodel.Room;
import ihtcvirtualmetamodel.Shift;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>genderRoomShift()</code>.
 */
public class GenderRoomShiftMatch extends GraphTransformationMatch<GenderRoomShiftMatch, GenderRoomShiftPattern> {
	private Gender varG;
	private Room varR;
	private Shift varS;

	/**
	 * Creates a new match for the pattern <code>genderRoomShift()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public GenderRoomShiftMatch(final GenderRoomShiftPattern pattern, final IMatch match) {
		super(pattern, match);
		varG = (Gender) match.get("g");
		varR = (Room) match.get("r");
		varS = (Shift) match.get("s");
	}

	/**
	 * Returns the g.
	 *
	 * @return the g
	 */
	public Gender getG() {
		return varG;
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

	@Override
	public String toString() {
		String s = "match {" + java.lang.System.lineSeparator();
		s += "	g --> " + varG + java.lang.System.lineSeparator();
		s += "	r --> " + varR + java.lang.System.lineSeparator();
		s += "	s --> " + varS + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
