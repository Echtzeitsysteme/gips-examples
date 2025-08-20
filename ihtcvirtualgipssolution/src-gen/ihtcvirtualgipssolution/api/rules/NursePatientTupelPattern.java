package ihtcvirtualgipssolution.api.rules;

import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import ihtcvirtualgipssolution.api.matches.NursePatientTupelMatch;
import ihtcvirtualmetamodel.Nurse;
import ihtcvirtualmetamodel.Patient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationPattern;
import org.emoflon.ibex.gt.engine.GraphTransformationInterpreter;

/**
 * The pattern [org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@3eb32244 (name: n), org.emoflon.ibex.patternmodel.IBeXPatternModel.impl.IBeXNodeImpl@5312a98f (name: p)] which does the following:
 * Used to filter for the amount of different nurses assigned to each patient
 */
@SuppressWarnings("unused")
public class NursePatientTupelPattern extends GraphTransformationPattern<NursePatientTupelMatch, NursePatientTupelPattern> {
	private static String patternName = "nursePatientTupel";

	/**
	 * Creates a new pattern nursePatientTupel().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	
	public NursePatientTupelPattern(final IhtcvirtualgipssolutionAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	public NursePatientTupelMatch convertMatch(final IMatch match) {
		return new NursePatientTupelMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("n");
		names.add("p");
		return names;
	}

	/**
	 * Binds the node n to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NursePatientTupelPattern bindN(final Nurse object) {
		parameters.put("n", Objects.requireNonNull(object, "n must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node n to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NursePatientTupelPattern unbindN() {
		parameters.remove("n");
		return this;
	}

	/**
	 * Binds the node p to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NursePatientTupelPattern bindP(final Patient object) {
		parameters.put("p", Objects.requireNonNull(object, "p must not be null!"));
		return this;
	}
	
	/**
	 * Unbinds the node p to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public NursePatientTupelPattern unbindP() {
		parameters.remove("p");
		return this;
	}
	
	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + java.lang.System.lineSeparator();
		s += "	n --> " + parameters.get("n") + java.lang.System.lineSeparator();
		s += "	p --> " + parameters.get("p") + java.lang.System.lineSeparator();
		s += "}";
		return s;
	}
}
