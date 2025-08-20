package ihtcvirtualgipssolution.api.gips;
		
import ihtcvirtualgipssolution.api.gips.mapper.AssignedPatientsToRoomMapper;
import ihtcvirtualgipssolution.api.gips.mapper.AssignedNursesToWorkloadMapper;
import org.eclipse.emf.common.util.URI;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionHiPEApp;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedShiftToFirstWorkloadMapper;
import org.emoflon.gips.core.api.GipsEngineAPI;
import org.emoflon.gips.core.milp.SolverConfig;
import org.emoflon.gips.core.milp.Solver;
import org.eclipse.emf.ecore.resource.ResourceSet;
import ihtcvirtualgipssolution.api.IhtcvirtualgipssolutionAPI;
import org.emoflon.gips.core.milp.GurobiSolver;
import org.emoflon.gips.core.milp.CplexSolver;
import ihtcvirtualgipssolution.api.gips.mapper.CountPatientsForRoomMapper;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedOperationDayMapper;
import org.emoflon.gips.core.milp.GlpkSolver;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedOccupantNodesMapper;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedExtendingShiftToFirstWorkloadMapper;
import ihtcvirtualgipssolution.api.gips.mapper.SelectedShiftToRosterMapper;
import ihtcvirtualgipssolution.api.gips.mapper.AssignedGenderToRoomOnShiftMapper;
import org.emoflon.gips.core.GipsObjective;
		
public class IhtcvirtualgipssolutionGipsAPI extends GipsEngineAPI <IhtcvirtualgipssolutionHiPEApp, IhtcvirtualgipssolutionAPI>{
	final public static URI INTERMEDIATE_MODEL_URI = URI.createFileURI("/home/mkratz/git/gips-examples/ihtcvirtualgipssolution/src-gen/ihtcvirtualgipssolution/api/gips/gips-model.xmi");
	
	protected SelectedShiftToFirstWorkloadMapper selectedShiftToFirstWorkload;
	protected SelectedExtendingShiftToFirstWorkloadMapper selectedExtendingShiftToFirstWorkload;
	protected SelectedOperationDayMapper selectedOperationDay;
	protected SelectedShiftToRosterMapper selectedShiftToRoster;
	protected SelectedOccupantNodesMapper selectedOccupantNodes;
	protected CountPatientsForRoomMapper countPatientsForRoom;
	protected AssignedPatientsToRoomMapper assignedPatientsToRoom;
	protected AssignedGenderToRoomOnShiftMapper assignedGenderToRoomOnShift;
	protected AssignedNursesToWorkloadMapper assignedNursesToWorkload;
	
	public IhtcvirtualgipssolutionGipsAPI() {
		super(new IhtcvirtualgipssolutionHiPEApp());
	}
	
	/**
	 * Initializes the GIPS engine API with the given model URI.
	 * 
	 * @param modelUri Model URI to load.
	 */
	@Override
	public void init(final URI modelUri) {
		super.initInternal(INTERMEDIATE_MODEL_URI, modelUri);
	}
	
	/**
	 * Initializes the GIPS engine API with the given GIPS intermediate model URI
	 * and the model URI.
	 * 
	 * @param gipsModelURI GIPS intermediate model URI to load.
	 * @param modelUri     Model URI to load.
	 */
	@Override
	public void init(final URI gipsModelURI, final URI modelUri) {
		super.initInternal(gipsModelURI, modelUri);
	}
	
	/**
	 * Initializes the GIPS API with a given GIPS intermediate model URI, a model URI,
	 * and the IBeX pattern path to avoid using hard-coded paths in IBeX
	 * internally.
	 * 
	 * @param gipsModelURI    GIPS intermediate model URI to load.
	 * @param modelUri        Model URI to load.
	 * @param ibexPatternPath IBeX pattern path to load.
	 */
	@Override
	public void init(final URI gipsModelURI, final URI modelUri, final URI ibexPatternPath) {
		super.initInternal(gipsModelURI, modelUri, ibexPatternPath);
	}
	
	/**
	 * Initializes the API with a given resource set as model.
	 * 
	 * @param model Resource set as model.
	 */
	@Override
	public void init(final ResourceSet model) {
		super.initInternal(INTERMEDIATE_MODEL_URI, model);
	}
	
	/**
	 * Initializes the GIPS engine API with the given GIPS intermediate model URI
	 * and a resource set as model.
	 * 
	 * @param gipsModelUri GIPS intermediate model URI to load.
	 * @param model        Resource set as model.
	 */
	@Override
	public void init(final URI gipsModelUri, final ResourceSet model) {
		super.initInternal(gipsModelUri, model);
	}
	
	/**
     * Initializes the GIPS engine API with the given GIPS intermediate model URI, a
	 * resource set as model, and the IBeX pattern path to avoid using hard-coded
	 * paths in IBeX.
	 * 
	 * @param gipsModelUri    GIPS intermediate model URI to load.
	 * @param model           Resource set as model.
	 * @param ibexPatternPath IBeX pattern path to load.
	 */
	@Override
	public void init(final URI gipsModelUri, final ResourceSet model, final URI ibexPatternPath) {
		super.initInternal(gipsModelUri, model, ibexPatternPath);
	}
	
	public SelectedShiftToFirstWorkloadMapper getSelectedShiftToFirstWorkload() {
		return selectedShiftToFirstWorkload;
	}
	public SelectedExtendingShiftToFirstWorkloadMapper getSelectedExtendingShiftToFirstWorkload() {
		return selectedExtendingShiftToFirstWorkload;
	}
	public SelectedOperationDayMapper getSelectedOperationDay() {
		return selectedOperationDay;
	}
	public SelectedShiftToRosterMapper getSelectedShiftToRoster() {
		return selectedShiftToRoster;
	}
	public SelectedOccupantNodesMapper getSelectedOccupantNodes() {
		return selectedOccupantNodes;
	}
	public CountPatientsForRoomMapper getCountPatientsForRoom() {
		return countPatientsForRoom;
	}
	public AssignedPatientsToRoomMapper getAssignedPatientsToRoom() {
		return assignedPatientsToRoom;
	}
	public AssignedGenderToRoomOnShiftMapper getAssignedGenderToRoomOnShift() {
		return assignedGenderToRoomOnShift;
	}
	public AssignedNursesToWorkloadMapper getAssignedNursesToWorkload() {
		return assignedNursesToWorkload;
	}
	
	@Override
	protected void createMappers() {
		selectedShiftToFirstWorkload = (SelectedShiftToFirstWorkloadMapper) mapperFactory.createMapper(name2Mapping.get("selectedShiftToFirstWorkload"));
		addMapper(selectedShiftToFirstWorkload);
		selectedExtendingShiftToFirstWorkload = (SelectedExtendingShiftToFirstWorkloadMapper) mapperFactory.createMapper(name2Mapping.get("selectedExtendingShiftToFirstWorkload"));
		addMapper(selectedExtendingShiftToFirstWorkload);
		selectedOperationDay = (SelectedOperationDayMapper) mapperFactory.createMapper(name2Mapping.get("selectedOperationDay"));
		addMapper(selectedOperationDay);
		selectedShiftToRoster = (SelectedShiftToRosterMapper) mapperFactory.createMapper(name2Mapping.get("selectedShiftToRoster"));
		addMapper(selectedShiftToRoster);
		selectedOccupantNodes = (SelectedOccupantNodesMapper) mapperFactory.createMapper(name2Mapping.get("selectedOccupantNodes"));
		addMapper(selectedOccupantNodes);
		countPatientsForRoom = (CountPatientsForRoomMapper) mapperFactory.createMapper(name2Mapping.get("countPatientsForRoom"));
		addMapper(countPatientsForRoom);
		assignedPatientsToRoom = (AssignedPatientsToRoomMapper) mapperFactory.createMapper(name2Mapping.get("assignedPatientsToRoom"));
		addMapper(assignedPatientsToRoom);
		assignedGenderToRoomOnShift = (AssignedGenderToRoomOnShiftMapper) mapperFactory.createMapper(name2Mapping.get("assignedGenderToRoomOnShift"));
		addMapper(assignedGenderToRoomOnShift);
		assignedNursesToWorkload = (AssignedNursesToWorkloadMapper) mapperFactory.createMapper(name2Mapping.get("assignedNursesToWorkload"));
		addMapper(assignedNursesToWorkload);
	}
	
	@Override
	protected void initMapperFactory() {
		mapperFactory = new IhtcvirtualgipssolutionGipsMapperFactory(this, eMoflonAPI);
	}
	
	@Override	
	protected void initConstraintFactory() {
		constraintFactory = new IhtcvirtualgipssolutionGipsConstraintFactory(this, eMoflonAPI);
	}
	
	@Override	
	protected void initLinearFunctionFactory() {
		functionFactory = new IhtcvirtualgipssolutionGipsLinearFunctionFactory(this, eMoflonAPI);
	}
	
	@Override
	protected GipsObjective createObjective() {
		// No objective was defined!
		return null;
	}
	
	@Override
	protected Solver createSolver() {
		Solver solver = null;
		try {
			solver = new GurobiSolver(this, solverConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return solver;
	}
	
	@Override
	protected void updateConstants() {
	}
				
}