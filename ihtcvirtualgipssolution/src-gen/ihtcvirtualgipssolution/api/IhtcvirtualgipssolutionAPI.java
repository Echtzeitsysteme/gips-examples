package ihtcvirtualgipssolution.api;

import ihtcvirtualgipssolution.api.rules.AgeGroupsRoomDayPattern;
import ihtcvirtualgipssolution.api.rules.GenderRoomShiftPattern;
import ihtcvirtualgipssolution.api.rules.MandatoryPatientsPattern;
import ihtcvirtualgipssolution.api.rules.NursePatientTupelPattern;
import ihtcvirtualgipssolution.api.rules.NurseRosterTupelPattern;
import ihtcvirtualgipssolution.api.rules.NursetoWorkloadPattern;
import ihtcvirtualgipssolution.api.rules.OptionalPatientsPattern;
import ihtcvirtualgipssolution.api.rules.OtCapacityTupelPattern;
import ihtcvirtualgipssolution.api.rules.PatientForRoomPattern;
import ihtcvirtualgipssolution.api.rules.RoomDayTupelPattern;
import ihtcvirtualgipssolution.api.rules.RoomForShiftPattern;
import ihtcvirtualgipssolution.api.rules.SelectExtendingShiftToFirstWorkloadRule;
import ihtcvirtualgipssolution.api.rules.SelectOperationDayRule;
import ihtcvirtualgipssolution.api.rules.SelectShiftToFirstWorkloadRule;
import ihtcvirtualgipssolution.api.rules.SelectShiftToRosterRule;
import ihtcvirtualgipssolution.api.rules.SurgeonOptimeTupelPattern;
import ihtcvirtualgipssolution.api.rules.SurgeonOTForDayPattern;
import ihtcvirtualgipssolution.api.rules.VirtualNodesForOccupantRule;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.ibex.common.operational.IContextPatternInterpreter;
import org.emoflon.ibex.gt.api.GraphTransformationAPI;
import org.emoflon.ibex.gt.api.GraphTransformationPattern;
import org.emoflon.ibex.gt.api.GraphTransformationRule;

/**
 * The IhtcvirtualgipssolutionAPI with 5 rules and 18 patterns.
 */
public class IhtcvirtualgipssolutionAPI extends GraphTransformationAPI {
	
	public static String patternPath = "ihtcvirtualgipssolution/src-gen/ihtcvirtualgipssolution/api/ibex-patterns.xmi";

	/**
	 * Creates a new IhtcvirtualgipssolutionAPI.
	 *
	 * @param engine
	 *            the engine to use for queries and transformations
	 * @param model
	 *            the resource set containing the model file
	 * @param workspacePath
	 *            the path to the workspace which is concatenated with the project
	 *            relative path to the patterns
	 */
	public IhtcvirtualgipssolutionAPI(final IContextPatternInterpreter engine, final ResourceSet model, final String workspacePath) {
		super(engine, model);
		URI uri = URI.createFileURI(workspacePath + patternPath);
		interpreter.loadPatternSet(uri);
		patternMap = initiatePatternMap();
		gillespieMap = initiateGillespieMap();
	}
	
	/**
	 * Creates a new IhtcvirtualgipssolutionAPI.
	 *
	 * @param engine
	 *			  the engine to use for queries and transformations.
	 * @param model
	 *            the resource set containing the model file.
	 * @param patternPath
	 *            the path to the IBeX pattern XMI file to load.
	 */
	public IhtcvirtualgipssolutionAPI(final IContextPatternInterpreter engine, final ResourceSet model, final URI patternPath) {
		super(engine, model);
		interpreter.loadPatternSet(patternPath);
		patternMap = initiatePatternMap();
		gillespieMap = initiateGillespieMap();
	}

	/**
	 * Creates a new IhtcvirtualgipssolutionAPI.
	 *
	 * @param engine
	 *            the engine to use for queries and transformations
	 * @param model
	 *            the resource set containing the model file
	 * @param defaultResource
	 *            the default resource
	 * @param workspacePath
	 *            the path to the workspace which is concatenated with the project
	 *            relative path to the patterns
	 */
	public IhtcvirtualgipssolutionAPI(final IContextPatternInterpreter engine, final ResourceSet model, final Resource defaultResource,
			final String workspacePath) {
		super(engine, model, defaultResource);
		URI uri = URI.createFileURI(workspacePath + patternPath);
		interpreter.loadPatternSet(uri);
		patternMap = initiatePatternMap();
		gillespieMap = initiateGillespieMap();
	}
	
	@Override
	protected Map<String, Supplier<? extends GraphTransformationPattern<?,?>>> initiatePatternMap(){
		Map<String, Supplier<? extends GraphTransformationPattern<?,?>>> map = new HashMap<>();
		map.put("selectExtendingShiftToFirstWorkload", () -> selectExtendingShiftToFirstWorkload());
		map.put("selectOperationDay", () -> selectOperationDay());
		map.put("selectShiftToFirstWorkload", () -> selectShiftToFirstWorkload());
		map.put("selectShiftToRoster", () -> selectShiftToRoster());
		map.put("virtualNodesForOccupant", () -> virtualNodesForOccupant());
		map.put("ageGroupsRoomDay", () -> ageGroupsRoomDay());
		map.put("genderRoomShift", () -> genderRoomShift());
		map.put("mandatoryPatients", () -> mandatoryPatients());
		map.put("nursePatientTupel", () -> nursePatientTupel());
		map.put("nurseRosterTupel", () -> nurseRosterTupel());
		map.put("nursetoWorkload", () -> nursetoWorkload());
		map.put("optionalPatients", () -> optionalPatients());
		map.put("otCapacityTupel", () -> otCapacityTupel());
		map.put("patientForRoom", () -> patientForRoom());
		map.put("roomDayTupel", () -> roomDayTupel());
		map.put("roomForShift", () -> roomForShift());
		map.put("surgeonOTForDay", () -> surgeonOTForDay());
		map.put("surgeonOptimeTupel", () -> surgeonOptimeTupel());
		return map;
	}
	
	@Override
	protected Map<GraphTransformationRule<?,?>, double[]> initiateGillespieMap(){
		Map<GraphTransformationRule<?,?>, double[]> map = 
			new HashMap<>();
		return map;
	}
					
	/**
	* Creates a new instance of the rule <code>selectExtendingShiftToFirstWorkload()</code> which does the following:
	* Rule that selects an extending VirtualShiftToWorkload-Node to be adopted in the final model.
	*
	* @return the new instance of the rule»
	*/
	public SelectExtendingShiftToFirstWorkloadRule selectExtendingShiftToFirstWorkload() {
		try{
			SelectExtendingShiftToFirstWorkloadRule rule = (SelectExtendingShiftToFirstWorkloadRule) interpreter.getRegisteredGraphTransformationPattern("selectExtendingShiftToFirstWorkload");
			return rule;
		} catch(Exception e) {
			return new SelectExtendingShiftToFirstWorkloadRule(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the rule <code>selectOperationDay()</code> which does the following:
	* Rule that selects an operation day for a patient.That includes three edges to be adopted in the final model.I. + II.: between the patient and opTime and between patient and capacity -> Selects a day and OT for the patientIII.: between opTime and Capacity to make sure the predefined surgeon of the patient is working in the correct OT on that day
	*
	* @return the new instance of the rule»
	*/
	public SelectOperationDayRule selectOperationDay() {
		try{
			SelectOperationDayRule rule = (SelectOperationDayRule) interpreter.getRegisteredGraphTransformationPattern("selectOperationDay");
			return rule;
		} catch(Exception e) {
			return new SelectOperationDayRule(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the rule <code>selectShiftToFirstWorkload()</code> which does the following:
	* GTRule that selects a VirtualShiftToWorkload-Node to be adopted in the final model.
	*
	* @return the new instance of the rule»
	*/
	public SelectShiftToFirstWorkloadRule selectShiftToFirstWorkload() {
		try{
			SelectShiftToFirstWorkloadRule rule = (SelectShiftToFirstWorkloadRule) interpreter.getRegisteredGraphTransformationPattern("selectShiftToFirstWorkload");
			return rule;
		} catch(Exception e) {
			return new SelectShiftToFirstWorkloadRule(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the rule <code>selectShiftToRoster()</code> which does the following:
	* Rule that selects an VirtualShiftToRoster-Node to be adopted in the final model.
	*
	* @return the new instance of the rule»
	*/
	public SelectShiftToRosterRule selectShiftToRoster() {
		try{
			SelectShiftToRosterRule rule = (SelectShiftToRosterRule) interpreter.getRegisteredGraphTransformationPattern("selectShiftToRoster");
			return rule;
		} catch(Exception e) {
			return new SelectShiftToRosterRule(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the rule <code>virtualNodesForOccupant()</code> which does the following:
	* Rule that selects all VirtualShiftToWorkload-Nodes to be adopted in the final model if they were imported (belong to an occupant)
	*
	* @return the new instance of the rule»
	*/
	public VirtualNodesForOccupantRule virtualNodesForOccupant() {
		try{
			VirtualNodesForOccupantRule rule = (VirtualNodesForOccupantRule) interpreter.getRegisteredGraphTransformationPattern("virtualNodesForOccupant");
			return rule;
		} catch(Exception e) {
			return new VirtualNodesForOccupantRule(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>ageGroupsRoomDay()</code> which does the following:
	* Pattern used for a Mapping to calculate what age groups are present in a specific room for all shifts.Additionally the highest and lowest age groups are also saved in variables
	*
	* @return the new instance of the pattern»
	*/
	public AgeGroupsRoomDayPattern ageGroupsRoomDay() {
		try{
			AgeGroupsRoomDayPattern pattern = (AgeGroupsRoomDayPattern) interpreter.getRegisteredGraphTransformationPattern("ageGroupsRoomDay");
			return pattern;
		} catch(Exception e) {
			return new AgeGroupsRoomDayPattern(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>genderRoomShift()</code> which does the following:
	* Pattern used for a mapping to save the information which genders are present in a specific room for each shiftThis information is only needed for each morning shift
	*
	* @return the new instance of the pattern»
	*/
	public GenderRoomShiftPattern genderRoomShift() {
		try{
			GenderRoomShiftPattern pattern = (GenderRoomShiftPattern) interpreter.getRegisteredGraphTransformationPattern("genderRoomShift");
			return pattern;
		} catch(Exception e) {
			return new GenderRoomShiftPattern(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>mandatoryPatients()</code> which does the following:
	* Utility Patterns used in ConstraintsPattern to find all mandatory patients
	*
	* @return the new instance of the pattern»
	*/
	public MandatoryPatientsPattern mandatoryPatients() {
		try{
			MandatoryPatientsPattern pattern = (MandatoryPatientsPattern) interpreter.getRegisteredGraphTransformationPattern("mandatoryPatients");
			return pattern;
		} catch(Exception e) {
			return new MandatoryPatientsPattern(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>nursePatientTupel()</code> which does the following:
	* Used to filter for the amount of different nurses assigned to each patient
	*
	* @return the new instance of the pattern»
	*/
	public NursePatientTupelPattern nursePatientTupel() {
		try{
			NursePatientTupelPattern pattern = (NursePatientTupelPattern) interpreter.getRegisteredGraphTransformationPattern("nursePatientTupel");
			return pattern;
		} catch(Exception e) {
			return new NursePatientTupelPattern(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>nurseRosterTupel()</code> which does the following:
	* Used in a mapping to calculate the workload for all nurses for each shift
	*
	* @return the new instance of the pattern»
	*/
	public NurseRosterTupelPattern nurseRosterTupel() {
		try{
			NurseRosterTupelPattern pattern = (NurseRosterTupelPattern) interpreter.getRegisteredGraphTransformationPattern("nurseRosterTupel");
			return pattern;
		} catch(Exception e) {
			return new NurseRosterTupelPattern(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>nursetoWorkload()</code> which does the following:
	* Used in a mapping to calculate which nurse is assigned to which workload.
	*
	* @return the new instance of the pattern»
	*/
	public NursetoWorkloadPattern nursetoWorkload() {
		try{
			NursetoWorkloadPattern pattern = (NursetoWorkloadPattern) interpreter.getRegisteredGraphTransformationPattern("nursetoWorkload");
			return pattern;
		} catch(Exception e) {
			return new NursetoWorkloadPattern(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>optionalPatients()</code> which does the following:
	* Pattern to find all optional patients
	*
	* @return the new instance of the pattern»
	*/
	public OptionalPatientsPattern optionalPatients() {
		try{
			OptionalPatientsPattern pattern = (OptionalPatientsPattern) interpreter.getRegisteredGraphTransformationPattern("optionalPatients");
			return pattern;
		} catch(Exception e) {
			return new OptionalPatientsPattern(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>otCapacityTupel()</code> which does the following:
	* Pattern used in a mapping to save the information if an OT is used on a specific Day and used to make sure the Capacity of an OT is not exceeded on each dayc.maxCapacity > 0 is already enforced by the preprocessing because Capacity Objects with maxCapacity <= 0 are not created
	*
	* @return the new instance of the pattern»
	*/
	public OtCapacityTupelPattern otCapacityTupel() {
		try{
			OtCapacityTupelPattern pattern = (OtCapacityTupelPattern) interpreter.getRegisteredGraphTransformationPattern("otCapacityTupel");
			return pattern;
		} catch(Exception e) {
			return new OtCapacityTupelPattern(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>patientForRoom()</code> which does the following:
	* Pattern that finds all patients that are assigned to a room -> first and following workloadsAlso holds the information which workload of a patient is assigned to which shift
	*
	* @return the new instance of the pattern»
	*/
	public PatientForRoomPattern patientForRoom() {
		try{
			PatientForRoomPattern pattern = (PatientForRoomPattern) interpreter.getRegisteredGraphTransformationPattern("patientForRoom");
			return pattern;
		} catch(Exception e) {
			return new PatientForRoomPattern(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>roomDayTupel()</code> which does the following:
	* Used in Softconstraint S1 to check the assigned age groups for each room on each day
	*
	* @return the new instance of the pattern»
	*/
	public RoomDayTupelPattern roomDayTupel() {
		try{
			RoomDayTupelPattern pattern = (RoomDayTupelPattern) interpreter.getRegisteredGraphTransformationPattern("roomDayTupel");
			return pattern;
		} catch(Exception e) {
			return new RoomDayTupelPattern(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>roomForShift()</code> which does the following:
	* Pattern used to map which rooms are occupied on a specific shift.Used for a mapping which counts the Patients over a specific room for a specific day -> enforce that the capacity of a room is not exceededAdditionally used to make sure nurses are not assigned to empty rooms and each non-empty room has an assigned nurse
	*
	* @return the new instance of the pattern»
	*/
	public RoomForShiftPattern roomForShift() {
		try{
			RoomForShiftPattern pattern = (RoomForShiftPattern) interpreter.getRegisteredGraphTransformationPattern("roomForShift");
			return pattern;
		} catch(Exception e) {
			return new RoomForShiftPattern(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>surgeonOTForDay()</code> which does the following:
	* Used in a mapping to save the information if a surgeon is operating in a specific OT for each day
	*
	* @return the new instance of the pattern»
	*/
	public SurgeonOTForDayPattern surgeonOTForDay() {
		try{
			SurgeonOTForDayPattern pattern = (SurgeonOTForDayPattern) interpreter.getRegisteredGraphTransformationPattern("surgeonOTForDay");
			return pattern;
		} catch(Exception e) {
			return new SurgeonOTForDayPattern(this, interpreter);
		}
	}
	/**
	* Creates a new instance of the pattern <code>surgeonOptimeTupel()</code> which does the following:
	* Pattern used in a mapping to save the information if a Surgeon operates on a specific Day and used to make sure the operation time of a surgeon is not exceeded on each dayop.maxOpTime > 0 is already enforced by the preprocessing because OpTime Objects with maxOpTime <= 0 are not created
	*
	* @return the new instance of the pattern»
	*/
	public SurgeonOptimeTupelPattern surgeonOptimeTupel() {
		try{
			SurgeonOptimeTupelPattern pattern = (SurgeonOptimeTupelPattern) interpreter.getRegisteredGraphTransformationPattern("surgeonOptimeTupel");
			return pattern;
		} catch(Exception e) {
			return new SurgeonOptimeTupelPattern(this, interpreter);
		}
	}
}
