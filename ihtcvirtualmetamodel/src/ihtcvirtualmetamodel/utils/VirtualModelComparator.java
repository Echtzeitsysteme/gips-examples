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

import ihtcvirtualmetamodel.Capacity;
import ihtcvirtualmetamodel.Nurse;
import ihtcvirtualmetamodel.OT;
import ihtcvirtualmetamodel.OpTime;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.Room;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.Roster;
import ihtcvirtualmetamodel.Shift;
import ihtcvirtualmetamodel.Surgeon;
import ihtcvirtualmetamodel.VirtualOpTimeToCapacity;
import ihtcvirtualmetamodel.VirtualShiftToRoster;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import ihtcvirtualmetamodel.VirtualWorkloadToCapacity;
import ihtcvirtualmetamodel.VirtualWorkloadToOpTime;
import ihtcvirtualmetamodel.Workload;

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
	private String firstModelPath = datasetFolder + "i01_pre-proc_gt.xmi";

	/**
	 * Second input model path.
	 */
	private String secondModelPath = datasetFolder + "i01_pre-proc_no-gt.xmi";

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

		// Check model sizes
		logger.info("Checking model size: Model 1");
		printStatsOfAllElements(class2ObjectM1);
		logger.info("---");
		logger.info("Checking model size: Model 2");
		printStatsOfAllElements(class2ObjectM2);
		logger.info("---");

		// Check model for duplicate entries
		logger.info("Checking for duplicates: Model 1");
		findDuplicates(class2ObjectM1);
		logger.info("---");
		logger.info("Checking for duplicates: Model 2");
		findDuplicates(class2ObjectM2);
		logger.info("---");

		// Compare the found virtual elements
		logger.info("Comparing both models in depth.");
		compare(class2ObjectM1, class2ObjectM2);
		logger.info("---");
		logger.info("Model comparation done.");
	}

	//
	// Utility methods
	//

	@SuppressWarnings("rawtypes")
	private void compare(final Map<String, List> class2ObjectM1, final Map<String, List> class2ObjectM2) {
		Objects.requireNonNull(class2ObjectM1);
		Objects.requireNonNull(class2ObjectM2);

		if (class2ObjectM1.keySet().size() != class2ObjectM2.keySet().size()) {
			logger.info("Key set of class names had different sizes.");
			return;
		}

		for (final String key : class2ObjectM1.keySet()) {
			if (class2ObjectM2.get(key) == null || class2ObjectM1.get(key).size() != class2ObjectM2.get(key).size()) {
				logger.info("Value size for key <" + key + "> did not match between both models.");
				return;
			}

			// For every instance in model 1, there must be exactly one "equal" instance in
			// model 2
			for (final Object o1 : class2ObjectM1.get(key)) {
				int noOfEqualObjects = 0;
				for (final Object o2 : class2ObjectM2.get(key)) {
					if (isEqual(o1, o2)) {
						noOfEqualObjects++;
					}
				}

				if (noOfEqualObjects != 1) {
					logger.info("Object <" + o1.toString() + "> has had a wrong number of matches in second model: "
							+ noOfEqualObjects);
				}
			}

//			// For every instance in model 2, there must be exactly one "equal" instance in
//			// model 1
//			for (final Object o1 : class2ObjectM2.get(key)) {
//				int noOfEqualObjects = 0;
//				for (final Object o2 : class2ObjectM1.get(key)) {
//					if (isEqual(o1, o2)) {
//						noOfEqualObjects++;
//					}
//				}
//
//				if (noOfEqualObjects != 1) {
//					logger.info("Object <" + o1.toString() + "> has had a wrong number of matches in first model: "
//							+ noOfEqualObjects);
//				}
//			}
		}
	}

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

	private boolean isEqual(final Object o1, final Object o2) {
		if (o1 == o2) {
			return true;
		} else if (o1 == null && o2 != null || o1 != null && o2 == null) {
			return false;
		}

		Objects.requireNonNull(o1);
		Objects.requireNonNull(o2);

		if (!o1.getClass().equals(o2.getClass())) {
			return false;
		}

		if (o1 instanceof VirtualWorkloadToOpTime o1v) {
			return isEqual(o1v, (VirtualWorkloadToOpTime) o2);
		} else if (o1 instanceof VirtualShiftToRoster o1v) {
			return isEqual(o1v, (VirtualShiftToRoster) o2);
		} else if (o1 instanceof VirtualOpTimeToCapacity o1v) {
			return isEqual(o1v, (VirtualOpTimeToCapacity) o2);
		} else if (o1 instanceof VirtualWorkloadToCapacity o1v) {
			return isEqual(o1v, (VirtualWorkloadToCapacity) o2);
		} else if (o1 instanceof VirtualShiftToWorkload o1v) {
			return isEqual(o1v, (VirtualShiftToWorkload) o2);
		} else if (o1 instanceof VirtualShiftToRoster o1v) {
			return isEqual(o1v, (VirtualShiftToRoster) o2);
		} else {
			throw new IllegalArgumentException(
					"Both objects were of equal classes but the virtual type did not match in casting.");
		}
	}

	private boolean isEqual(final VirtualWorkloadToOpTime a, final VirtualWorkloadToOpTime b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

		if (isEqual(a.getOpTime(), b.getOpTime()) && isEqual(a.getWorkload(), b.getWorkload())) {
			return true;
		}

		return false;
	}

	private boolean isEqual(final VirtualShiftToRoster a, final VirtualShiftToRoster b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

//		if (a.getShift().equals(b.getShift()) && a.getRoster().equals(b.getRoster())) {
		if (isEqual(a.getShift(), b.getShift()) && isEqual(a.getRoster(), b.getRoster())) {
			return true;
		}

		return false;
	}

	private boolean isEqual(final VirtualOpTimeToCapacity a, final VirtualOpTimeToCapacity b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

//		if (a.getOpTime().equals(b.getOpTime()) && a.getCapacity().equals(b.getCapacity())) {
		if (isEqual(a.getOpTime(), b.getOpTime()) && isEqual(a.getCapacity(), b.getCapacity())) {
			return true;
		}

		return false;
	}

	private boolean isEqual(final VirtualWorkloadToCapacity a, final VirtualWorkloadToCapacity b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

//		if (a.getWorkload().equals(b.getWorkload()) && a.getCapacity().equals(b.getCapacity())) {
		if (isEqual(a.getWorkload(), b.getWorkload()) && isEqual(a.getCapacity(), b.getCapacity())) {
			return true;
		}

		return false;
	}

	private boolean isEqual(final VirtualShiftToWorkload a, final VirtualShiftToWorkload b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

//		if (a.getShift().equals(b.getShift()) && a.getWorkload().equals(b.getWorkload())) {
		if (a.isWasImported() == b.isWasImported() && isEqual(a.getShift(), b.getShift())
				&& isEqual(a.getWorkload(), b.getWorkload()) && a.isIsSelected() == b.isIsSelected()
				&& isEqual(a.getRequires_virtualShiftToWorkload(), b.getRequires_virtualShiftToWorkload())
		// Break recursion loop; all instances will be checked individually anyway
//				&& isEqual(a.getEnables_virtualShiftToWorkload(), b.getEnables_virtualShiftToWorkload())
		) {
			return true;
		}

		return false;
	}

	private boolean isEqual(final OpTime a, final OpTime b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

		boolean equals = true;
		equals = equals && (a.getDay() == b.getDay());
		equals = equals && (a.getMaxOpTime() == b.getMaxOpTime());
		equals = equals && isEqual(a.getSurgeon(), b.getSurgeon());

		// TODO: Collections are missing

		return equals;
	}

	private boolean isEqual(final Workload a, final Workload b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

		boolean equals = true;
		equals = equals && (a.getWorkloadValue() == b.getWorkloadValue());
		equals = equals && (a.getMinNurseSkill() == b.getMinNurseSkill());
		equals = equals && (isEqual(a.getPatient(), b.getPatient()));
		equals = equals && (isEqual(a.getDerivedShift(), b.getDerivedShift()));
		equals = equals && (isEqual(a.getDerivedCapacity(), b.getDerivedCapacity()));
		equals = equals && (isEqual(a.getDerivedOpTimes(), b.getDerivedOpTimes()));

		// TODO: Collections are missing

		return equals;
	}

	private boolean isEqual(final Shift a, final Shift b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

		boolean equals = true;
		equals = equals && (a.getShiftNo() == b.getShiftNo());
		equals = equals && (isEqual(a.getRoom(), b.getRoom()));
		equals = equals && (isEqual(a.getDerivedRoster(), b.getDerivedRoster()));

		// TODO: Collections are missing

		return equals;
	}

	private boolean isEqual(final Roster a, final Roster b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

		boolean equals = true;
		equals = equals && (a.getShiftNo() == b.getShiftNo());
		equals = equals && (a.getMaxWorkload() == b.getMaxWorkload());
		equals = equals && (isEqual(a.getNurse(), b.getNurse()));

		// TODO: Collections are missing

		return equals;
	}

	private boolean isEqual(final Capacity a, final Capacity b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

		boolean equals = true;
		equals = equals && (a.getDay() == b.getDay());
		equals = equals && (a.getMaxCapacity() == b.getMaxCapacity());
		equals = equals && (isEqual(a.getOt(), b.getOt()));

		// TODO: Collections are missing

		return equals;
	}

	private boolean isEqual(final Surgeon a, final Surgeon b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

		boolean equals = true;
		equals = equals && (a.getName().equals(b.getName()));

		// TODO: Collections are missing

		return equals;
	}

	private boolean isEqual(final OT a, final OT b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

		boolean equals = true;
		equals = equals && (a.getName().equals(b.getName()));

		// TODO: Collections are missing

		return equals;
	}

	private boolean isEqual(final Room a, final Room b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

		boolean equals = true;
		equals = equals && (a.getBeds() == b.getBeds());
		equals = equals && (a.getName().equals(b.getName()));

		// TODO: Collections are missing

		return equals;
	}

	private boolean isEqual(final Nurse a, final Nurse b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

		boolean equals = true;
		equals = equals && (a.getSkillLevel() == b.getSkillLevel());
		equals = equals && (a.getName().equals(b.getName()));

		// TODO: Collections are missing

		return equals;
	}

	private boolean isEqual(final Patient a, final Patient b) {
		if (a == b) {
			return true;
		} else if (a == null && b != null || a != null && b == null) {
			return false;
		}

		Objects.requireNonNull(a);
		Objects.requireNonNull(b);

		if (a.equals(b)) {
			return true;
		}

		boolean equals = true;
		equals = equals && (a.getEarliestDay() == b.getEarliestDay());
		equals = equals && (a.getDueDay() == b.getDueDay());
		equals = equals && (a.isMandatory() == b.isMandatory());
		equals = equals && (a.getAgeGroup() == b.getAgeGroup());
		equals = equals && (a.getGender().equals(b.getGender()));
		equals = equals && (a.getStayLength() == b.getStayLength());
		equals = equals && (a.getSurgeryDuration() == b.getSurgeryDuration());
		// Skipping first workload to not create a recursive endless loop
//		equals = equals && (isEqual(a.getFirstWorkload(), b.getFirstWorkload()));
		equals = equals && (isEqual(a.getSurgeon(), b.getSurgeon()));
		equals = equals && (a.isIsOccupant() == b.isIsOccupant());
		equals = equals && (a.getName().equals(b.getName()));

		// TODO: Collections are missing

		return equals;
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
