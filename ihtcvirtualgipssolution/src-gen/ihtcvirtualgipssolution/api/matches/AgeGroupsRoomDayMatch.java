package ihtcvirtualgipssolution.api.matches;

import ihtcvirtualgipssolution.api.rules.AgeGroupsRoomDayPattern;
import ihtcvirtualmetamodel.AgeGroup;
import ihtcvirtualmetamodel.Day;
import ihtcvirtualmetamodel.Room;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>ageGroupsRoomDay()</code>.
 */
public class AgeGroupsRoomDayMatch extends GraphTransformationMatch<AgeGroupsRoomDayMatch, AgeGroupsRoomDayPattern> {
	private AgeGroup varAg;
	private Day varD;
	private Room varR;

	/**
	 * Creates a new match for the pattern <code>ageGroupsRoomDay()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public AgeGroupsRoomDayMatch(final AgeGroupsRoomDayPattern pattern, final IMatch match) {
		super(pattern, match);
		varAg = (AgeGroup) match.get("ag");
		varD = (Day) match.get("d");
		varR = (Room) match.get("r");
	}

	/**
	 * Returns the ag.
	 *
	 * @return the ag
	 */
	public AgeGroup getAg() {
		return varAg;
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
		s += "	ag --> " + varAg + java.lang.System.lineSeparator();
		s += "	d --> " + varD + java.lang.System.lineSeparator();
		s += "	r --> " + varR + java.lang.System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
