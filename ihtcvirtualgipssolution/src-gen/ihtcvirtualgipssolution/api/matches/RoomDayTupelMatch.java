package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.RoomDayTupelPattern;
import ihtcvirtualmetamodel.Day;
import ihtcvirtualmetamodel.Room;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>roomDayTupel()</code>.
 */
public class RoomDayTupelMatch extends GraphTransformationMatch<RoomDayTupelMatch, RoomDayTupelPattern> {
	private Day varD;
	private Room varR;

	/**
	 * Creates a new match for the pattern <code>roomDayTupel()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public RoomDayTupelMatch(final RoomDayTupelPattern pattern, final IMatch match) {
		super(pattern, match);
		varD = (Day) match.get("d");
		varR = (Room) match.get("r");
	}

	/**
	 * Returns the d.
	 *
	 * @return the d
	 */
	public Day getD() {
		return varD;
	}

	/**
	 * Returns the r.
	 *
	 * @return the r
	 */
	public Room getR() {
		return varR;
	}

	@Override
	public String toString() {
		String s = "match {" + java.lang.System.lineSeparator();
		s += "	d --> " + varD + java.lang.System.lineSeparator();
		s += "	r --> " + varR + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
