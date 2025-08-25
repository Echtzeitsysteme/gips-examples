package ihtcvirtualmetamodel.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.resource.Resource;

import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.VirtualOpTimeToCapacity;
import ihtcvirtualmetamodel.VirtualShiftToRoster;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import ihtcvirtualmetamodel.VirtualWorkloadToCapacity;
import ihtcvirtualmetamodel.VirtualWorkloadToOpTime;

public class VirtualModelComparator {

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(VirtualModelComparator.class.getName());

	/**
	 * Project folder location.
	 */
	private String projectFolder = System.getProperty("user.dir");

	/**
	 * Instance folder location.
	 */
	private String datasetFolder = projectFolder + "/../ihtcvirtualmetamodel/instances/";

	/**
	 * First input model path.
	 */
	private String firstModelPath = datasetFolder + "test01_pre-proc_gt.xmi";

	/**
	 * Second input model path.
	 */
	private String secondModelPath = datasetFolder + "test01_pre-proc_nogt.xmi";

	@SuppressWarnings("rawtypes")
	private Map<String, List> class2ObjectM1 = new HashMap<>();
	@SuppressWarnings("rawtypes")
	private Map<String, List> class2ObjectM2 = new HashMap<>();

	public static void main(final String[] args) {
		new VirtualModelComparator().run();
	}

	private VirtualModelComparator() {
		// Model 1
		class2ObjectM1.put(VirtualWorkloadToCapacity.class.getSimpleName(), new ArrayList<VirtualWorkloadToCapacity>());
		class2ObjectM1.put(VirtualOpTimeToCapacity.class.getSimpleName(), new ArrayList<VirtualOpTimeToCapacity>());
		class2ObjectM1.put(VirtualShiftToRoster.class.getSimpleName(), new ArrayList<VirtualShiftToRoster>());
		class2ObjectM1.put(VirtualShiftToWorkload.class.getSimpleName(), new ArrayList<VirtualShiftToWorkload>());
		class2ObjectM1.put(VirtualWorkloadToOpTime.class.getSimpleName(), new ArrayList<VirtualWorkloadToOpTime>());

		// Model 2
		class2ObjectM2.put(VirtualWorkloadToCapacity.class.getSimpleName(), new ArrayList<VirtualWorkloadToCapacity>());
		class2ObjectM2.put(VirtualOpTimeToCapacity.class.getSimpleName(), new ArrayList<VirtualOpTimeToCapacity>());
		class2ObjectM2.put(VirtualShiftToRoster.class.getSimpleName(), new ArrayList<VirtualShiftToRoster>());
		class2ObjectM2.put(VirtualShiftToWorkload.class.getSimpleName(), new ArrayList<VirtualShiftToWorkload>());
		class2ObjectM2.put(VirtualWorkloadToOpTime.class.getSimpleName(), new ArrayList<VirtualWorkloadToOpTime>());

		// Configure logging
		logger.setUseParentHandlers(false);
		final ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new Formatter() {
			@Override
			public String format(final LogRecord record) {
				Objects.requireNonNull(record, "Given log entry was null.");
				return record.getMessage() + System.lineSeparator();
			}
		});
		logger.addHandler(handler);
	}

	private void run() {
		Objects.requireNonNull(firstModelPath);
		Objects.requireNonNull(secondModelPath);
		Objects.requireNonNull(class2ObjectM1);
		Objects.requireNonNull(class2ObjectM2);

		// Load models
		final Root firstModel = loadModel(firstModelPath);
		final Root secondModel = loadModel(secondModelPath);

		// Search for virtual elements in both models
		traverseModelForVirtualElements(firstModel, class2ObjectM1);
		traverseModelForVirtualElements(secondModel, class2ObjectM2);

		// Compare the found virtual elements
		logger.info("Checking model size: Model 1");
		printStatsOfAllElements(class2ObjectM1);
		logger.info("---");
		logger.info("Checking model size: Model 2");
		printStatsOfAllElements(class2ObjectM2);
		logger.info("---");

		logger.info("Checking for duplicates: Model 1");
		findDuplicates(class2ObjectM1);
		logger.info("---");
		logger.info("Checking for duplicates: Model 2");
		findDuplicates(class2ObjectM2);
	}

	//
	// Utility methods
	//

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void findDuplicates(final Map<String, List> data) {
		Objects.requireNonNull(data);

		data.keySet().forEach(className -> {
			final List instances = data.get(className);

			// No duplicates if list is empty
			if (!instances.isEmpty()) {
				final Object v = instances.get(0);
				if (v instanceof VirtualWorkloadToOpTime) {
					findDuplicatesVirtualWorkloadToOpTime((List<VirtualWorkloadToOpTime>) instances);
				} else if (v instanceof VirtualShiftToRoster) {
					findDuplicatesVirtualShiftToRoster((List<VirtualShiftToRoster>) instances);
				} else if (v instanceof VirtualOpTimeToCapacity) {
					findDuplicatesVirtualOpTimeToCapacity((List<VirtualOpTimeToCapacity>) instances);
				} else if (v instanceof VirtualWorkloadToCapacity) {
					findDuplicatesVirtualWorkloadToCapacity((List<VirtualWorkloadToCapacity>) instances);
				} else if (v instanceof VirtualShiftToWorkload) {
					findDuplicatesVirtualShiftToWorkload((List<VirtualShiftToWorkload>) instances);
				} else {
					throw new InternalError("Type did not match.");
				}
			}
		});
	}

	private boolean isEqual(final VirtualWorkloadToOpTime a, final VirtualWorkloadToOpTime b) {
		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a == b) {
			return true;
		}

		if (a.equals(b)) {
			return true;
		}

		if (a.getOpTime().equals(b.getOpTime()) && a.getWorkload().equals(b.getWorkload())) {
			return true;
		}

		return false;
	}

	private boolean isEqual(final VirtualShiftToRoster a, final VirtualShiftToRoster b) {
		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a == b) {
			return true;
		}

		if (a.equals(b)) {
			return true;
		}

		if (a.getShift().equals(b.getShift()) && a.getRoster().equals(b.getRoster())) {
			return true;
		}

		return false;
	}

	private boolean isEqual(final VirtualOpTimeToCapacity a, final VirtualOpTimeToCapacity b) {
		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a == b) {
			return true;
		}

		if (a.equals(b)) {
			return true;
		}

		if (a.getOpTime().equals(b.getOpTime()) && a.getCapacity().equals(b.getCapacity())) {
			return true;
		}

		return false;
	}

	private boolean isEqual(final VirtualWorkloadToCapacity a, final VirtualWorkloadToCapacity b) {
		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a == b) {
			return true;
		}

		if (a.equals(b)) {
			return true;
		}

		if (a.getWorkload().equals(b.getWorkload()) && a.getCapacity().equals(b.getCapacity())) {
			return true;
		}

		return false;
	}

	private boolean isEqual(final VirtualShiftToWorkload a, final VirtualShiftToWorkload b) {
		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a == b) {
			return true;
		}

		if (a.equals(b)) {
			return true;
		}

		if (a.getShift().equals(b.getShift()) && a.getWorkload().equals(b.getWorkload())) {
			return true;
		}

		return false;
	}

	private void findDuplicatesVirtualShiftToWorkload(final List<VirtualShiftToWorkload> virtualElements) {
		Objects.requireNonNull(virtualElements);
		for (int i = 0; i < virtualElements.size(); i++) {
			final VirtualShiftToWorkload a = virtualElements.get(i);
			for (int j = 0; j < virtualElements.size(); j++) {
				final VirtualShiftToWorkload b = virtualElements.get(j);
				if (i != j) {
					if (isEqual(a, b)) {
						logger.info("VirtualShiftToWorkload duplicate: " + a + " and " + b);
					}
				}
			}
		}
	}

	private void findDuplicatesVirtualWorkloadToCapacity(final List<VirtualWorkloadToCapacity> virtualElements) {
		Objects.requireNonNull(virtualElements);
		for (int i = 0; i < virtualElements.size(); i++) {
			final VirtualWorkloadToCapacity a = virtualElements.get(i);
			for (int j = 0; j < virtualElements.size(); j++) {
				final VirtualWorkloadToCapacity b = virtualElements.get(j);
				if (i != j) {
					if (isEqual(a, b)) {
						logger.info("VirtualWorkloadToCapacity duplicate: " + a + " and " + b);
					}
				}
			}
		}
	}

	private void findDuplicatesVirtualOpTimeToCapacity(final List<VirtualOpTimeToCapacity> virtualElements) {
		Objects.requireNonNull(virtualElements);
		for (int i = 0; i < virtualElements.size(); i++) {
			final VirtualOpTimeToCapacity a = virtualElements.get(i);
			for (int j = 0; j < virtualElements.size(); j++) {
				final VirtualOpTimeToCapacity b = virtualElements.get(j);
				if (i != j) {
					if (isEqual(a, b)) {
						logger.info("VirtualOpTimeToCapacity duplicate: " + a + " and " + b);
					}
				}
			}
		}
	}

	private void findDuplicatesVirtualShiftToRoster(final List<VirtualShiftToRoster> virtualElements) {
		Objects.requireNonNull(virtualElements);
		for (int i = 0; i < virtualElements.size(); i++) {
			final VirtualShiftToRoster a = virtualElements.get(i);
			for (int j = 0; j < virtualElements.size(); j++) {
				final VirtualShiftToRoster b = virtualElements.get(j);
				if (i != j) {
					if (isEqual(a, b)) {
						logger.info("VirtualShiftToRoster duplicate: " + a + " and " + b);
					}
				}
			}
		}
	}

	private void findDuplicatesVirtualWorkloadToOpTime(final List<VirtualWorkloadToOpTime> virtualElements) {
		Objects.requireNonNull(virtualElements);
		for (int i = 0; i < virtualElements.size(); i++) {
			final VirtualWorkloadToOpTime a = virtualElements.get(i);
			for (int j = 0; j < virtualElements.size(); j++) {
				final VirtualWorkloadToOpTime b = virtualElements.get(j);
				if (i != j) {
					if (isEqual(a, b)) {
						logger.info("VirtualWorkloadToOpTime duplicate: " + a + " and " + b);
					}
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void printStatsOfAllElements(final Map<String, List> data) {
		Objects.requireNonNull(data);
		printStatsOfElements(VirtualWorkloadToCapacity.class.getSimpleName(), data);
		printStatsOfElements(VirtualOpTimeToCapacity.class.getSimpleName(), data);
		printStatsOfElements(VirtualShiftToRoster.class.getSimpleName(), data);
		printStatsOfElements(VirtualShiftToWorkload.class.getSimpleName(), data);
		printStatsOfElements(VirtualWorkloadToOpTime.class.getSimpleName(), data);
	}

	@SuppressWarnings("rawtypes")
	private void printStatsOfElements(final String className, final Map<String, List> data) {
		Objects.requireNonNull(className);
		Objects.requireNonNull(data);
		if (className.isBlank()) {
			throw new IllegalArgumentException("Given class name was blank.");
		}

		final List instances = data.get(className);
		logger.info(className + ": " + instances.size() + " instances.");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void traverseModelForVirtualElements(final Root model, Map<String, List> data) {
		Objects.requireNonNull(model);
		Objects.requireNonNull(data);

		// Rosters
		model.getNurses().forEach(nurse -> {
			nurse.getRosters().forEach(roster -> {
				data.get(VirtualShiftToRoster.class.getSimpleName()).addAll(roster.getVirtualShift());
			});
		});

		// Shift
		model.getRooms().forEach(room -> {
			room.getShifts().forEach(shift -> {
				data.get(VirtualShiftToWorkload.class.getSimpleName()).addAll(shift.getVirtualWorkload());
			});
		});

		// OpTime
		model.getSurgeons().forEach(surgeon -> {
			surgeon.getOpTimes().forEach(optime -> {
				data.get(VirtualWorkloadToOpTime.class.getSimpleName()).addAll(optime.getVirtualWorkload());
			});
		});

		// Capacity (2x)
		model.getOts().forEach(ot -> {
			ot.getCapacities().forEach(capacity -> {
				data.get(VirtualOpTimeToCapacity.class.getSimpleName()).addAll(capacity.getVirtualOpTime());
				data.get(VirtualWorkloadToCapacity.class.getSimpleName()).addAll(capacity.getVirtualWorkload());
			});
		});
	}

	private Root loadModel(final String filepath) {
		Objects.requireNonNull(filepath);
		final Resource res = FileUtils.loadModel(filepath);
		Objects.requireNonNull(res);
		return (Root) res.getContents().get(0);
	}

}
