package ihtcvirtualpreprocessing;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import ihtcvirtualmetamodel.Capacity;
import ihtcvirtualmetamodel.IhtcvirtualmetamodelFactory;
import ihtcvirtualmetamodel.OT;
import ihtcvirtualmetamodel.Room;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.Shift;
import ihtcvirtualmetamodel.VirtualOpTimeToCapacity;
import ihtcvirtualmetamodel.VirtualShiftToRoster;
import ihtcvirtualmetamodel.VirtualShiftToWorkload;
import ihtcvirtualmetamodel.VirtualWorkloadToCapacity;
import ihtcvirtualmetamodel.VirtualWorkloadToOpTime;
import ihtcvirtualmetamodel.Workload;
import ihtcvirtualmetamodel.utils.FileUtils;

public class PreprocessingNoGtApp {

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(PreprocessingNoGtApp.class.getName());

	// TODO: Remove this attribute
	/**
	 * XMI model input file path. This value will be used to read the input model
	 * and write the output model to.
	 */
	@Deprecated
	private final String xmiInputFilePath;

	/**
	 * XMI model output file path. This value will be used to write the output model
	 * to.
	 */
	private final String xmiOutputFilePath;

	/**
	 * TODO
	 */
	private Root model = null;

	/**
	 * TODO
	 * 
	 * @param xmiInputFilePath
	 * @param xmiOutputFilePath
	 */
	public PreprocessingNoGtApp(final String xmiInputFilePath, final String xmiOutputFilePath) {
		Objects.requireNonNull(xmiInputFilePath);
		Objects.requireNonNull(xmiOutputFilePath);

		this.xmiInputFilePath = xmiInputFilePath;
		this.xmiOutputFilePath = xmiOutputFilePath;

		// Load model from given XMI file path
		model = (Root) FileUtils.loadModel(xmiInputFilePath).getContents().get(0);

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

	/**
	 * TODO
	 */
	public void run() {
		// Model was loaded within the constructor.
		logger.info("Started pre-processing without GT.");

		// preprocessOccupantsWorkload
		createVirtualShiftToWorkloadsOccupants();

		// assignNurseToRoom
		createVirtualShiftToRosterCandidates();

		// assignSurgeonToOt
		createVirtualOpTimeToCapacityCandidates();

		// fixOperationDay
		createVirtualWorkloadToOperationCandidates();

		// assignPatientToRoom (initial)
		createVirtualShiftToWorkloadInitialCandidates();

		// assignPatientToRoom (extending)
		createVirtualShiftToWorkloadExtendingCandidates();

		// Save the altered model.
		try {
			FileUtils.save(model, xmiOutputFilePath);
		} catch (final IOException e) {
			logger.warning("IOException occurred while writing the output XMI file." + e.getMessage());
			System.exit(1);
		}
	}

	private void createVirtualWorkloadToOperationCandidates() {
		Objects.requireNonNull(model);
		model.getPatients().stream().filter(patient -> !patient.isIsOccupant()).forEach(patient -> {
			final Workload w = patient.getFirstWorkload();
			patient.getSurgeon().getOpTimes().forEach(opTime -> {
				// Check time frame (stay of the patient)
				if (patient.getEarliestDay() <= opTime.getDay() && opTime.getDay() <= patient.getDueDay()) {
					final List<VirtualOpTimeToCapacity> opTimeCapacityCandidates = opTime.getVirtualCapacity();
					// Check time frame (duration of the surgery - surgeon)
					if (patient.getSurgeryDuration() <= opTime.getMaxOpTime()) {
						for (final VirtualOpTimeToCapacity vExists : opTimeCapacityCandidates) {
							final Capacity c = vExists.getCapacity();

							// Check time frame (duration of the surgery - capacity of the OT)
							if (patient.getSurgeryDuration() <= c.getMaxCapacity()) {
								final VirtualWorkloadToOpTime vnew = IhtcvirtualmetamodelFactory.eINSTANCE
										.createVirtualWorkloadToOpTime();
								vnew.setIsSelected(false);
								vnew.setOpTime(opTime);
								vnew.setWorkload(w);
								vnew.getRequires_virtualOpTimeToCapacity().add(vExists);

								final VirtualWorkloadToCapacity vnew2 = IhtcvirtualmetamodelFactory.eINSTANCE
										.createVirtualWorkloadToCapacity();
								vnew2.setIsSelected(false);
								vnew2.setCapacity(c);
								vnew2.setWorkload(w);
								vnew2.getRequires_virtualOpTimeToCapacity().add(vExists);

								vExists.getEnables_virtualWorkloadToOpTime().add(vnew);
								vExists.getEnables_virtual_WorkloadToCapacity().add(vnew2);

								opTime.getVirtualWorkload().add(vnew);
								c.getVirtualWorkload().add(vnew2);
							}
						}
					}
				}
			});
		});
	}

	private void createVirtualOpTimeToCapacityCandidates() {
		Objects.requireNonNull(model);
		model.getSurgeons().forEach(surgeon -> {
			surgeon.getOpTimes().forEach(opTime -> {
				final List<Capacity> allCapacitiesOnDay = getCapacitiesForDay(opTime.getDay());
				for (final Capacity capacity : allCapacitiesOnDay) {
					final VirtualOpTimeToCapacity v = IhtcvirtualmetamodelFactory.eINSTANCE
							.createVirtualOpTimeToCapacity();
					v.setIsSelected(false);
					v.setOpTime(opTime);
					v.setCapacity(capacity);
					capacity.getVirtualOpTime().add(v);
				}
			});
		});

	}

	private void createVirtualShiftToWorkloadsOccupants() {
		Objects.requireNonNull(model);
		model.getPatients().stream().filter(patient -> patient.isIsOccupant()).forEach(occupant -> {
			VirtualShiftToWorkload vPrev = null;
			for (final Workload workload : occupant.getWorkloads()) {
				final Shift shift = workload.getDerivedShift();

				final VirtualShiftToWorkload v = IhtcvirtualmetamodelFactory.eINSTANCE.createVirtualShiftToWorkload();
				v.setIsSelected(false);
				v.setWasImported(true);
				v.setShift(shift);
				v.setWorkload(workload);
				if (vPrev != null) {
					v.getRequires_virtualShiftToWorkload().add(vPrev);
				}
				shift.getVirtualWorkload().add(v);
				vPrev = v;
			}

		});
	}

	private void createVirtualShiftToRosterCandidates() {
		Objects.requireNonNull(model);
		model.getNurses().forEach(nurse -> {
			nurse.getRosters().forEach(roster -> {
				model.getRooms().forEach(room -> {
					final Shift shift = getShift(room, roster.getShiftNo());
					final VirtualShiftToRoster v = IhtcvirtualmetamodelFactory.eINSTANCE.createVirtualShiftToRoster();
					v.setIsSelected(false);
					v.setRoster(roster);
					v.setShift(shift);
					roster.getVirtualShift().add(v);
				});
			});
		});
	}

	private void createVirtualShiftToWorkloadInitialCandidates() {
		Objects.requireNonNull(model);
		model.getPatients().stream().filter(patient -> !patient.isIsOccupant()).forEach(patient -> {
			final int earliestPossibleFirstShift = dayToShift(patient.getEarliestDay());
			final int latestPossibleFirstShift = dayToShift(patient.getDueDay());
			model.getRooms().stream().filter(room -> !patient.getIncompatibleRooms().contains(room)).forEach(room -> {
				room.getShifts().forEach(shift -> {
					// Check if shift is in potential start time frame
					if (shift.getShiftNo() >= earliestPossibleFirstShift
							&& shift.getShiftNo() <= latestPossibleFirstShift) {
						final VirtualShiftToWorkload v = IhtcvirtualmetamodelFactory.eINSTANCE
								.createVirtualShiftToWorkload();
						v.setIsSelected(false);
						v.setWasImported(false);
						v.setShift(shift);
						v.setWorkload(patient.getFirstWorkload());
						shift.getVirtualWorkload().add(v);
					}
				});
			});
		});
	}

	private void createVirtualShiftToWorkloadExtendingCandidates() {
		Objects.requireNonNull(model);

		// Collect all initial assignments (to be iterated over later on)
		final List<VirtualShiftToWorkload> initialAssignments = new LinkedList<VirtualShiftToWorkload>();
		model.getRooms().forEach(room -> {
			room.getShifts().forEach(shift -> {
				shift.getVirtualWorkload().forEach(vinit -> {
					initialAssignments.add(vinit);
				});
			});
		});

		// For every initial virtual assignment, build the extending ladder
		for (final VirtualShiftToWorkload vinit : initialAssignments) {
			extendLadder(vinit.getShift(), vinit.getWorkload(), vinit);
		}
	}

	private void extendLadder(final Shift initShift, final Workload initWorkload, final VirtualShiftToWorkload initV) {
		Objects.requireNonNull(initShift);
		Objects.requireNonNull(initWorkload);
		Objects.requireNonNull(initV);

		Workload w = (Workload) initWorkload.getNext();
		Shift s = (Shift) initShift.getNext();
		VirtualShiftToWorkload v = initV;

		while (w != null && s != null) {
			final VirtualShiftToWorkload vNew = IhtcvirtualmetamodelFactory.eINSTANCE.createVirtualShiftToWorkload();
			vNew.setIsSelected(false);
			vNew.setWasImported(false);
			vNew.setShift(s);
			vNew.setWorkload(w);
			vNew.getRequires_virtualShiftToWorkload().add(v);
			s.getVirtualWorkload().add(vNew);
			w = (Workload) w.getNext();
			s = (Shift) s.getNext();
			v = vNew;
		}
	}

	//
	// Utility methods.
	//

	private Shift getShift(final Room room, final int shiftNo) {
		Objects.requireNonNull(room);
		if (shiftNo < 0) {
			throw new IllegalArgumentException("Given shift number was negative.");
		}

		for (final Shift s : room.getShifts()) {
			if (s.getShiftNo() == shiftNo) {
				return s;
			}
		}
		throw new UnsupportedOperationException(
				"Shift with number " + shiftNo + " not found in room " + room.getName());
	}

	private int dayToShift(final int day) {
		if (day < 0) {
			throw new IllegalArgumentException("Given day number was negative.");
		}
		return day * 3;
	}

	private Capacity getCapacityForRoomOnDay(final OT ot, final int day) {
		Objects.requireNonNull(ot);
		if (day < 0) {
			throw new IllegalArgumentException("Given day number was negative.");
		}

		for (final Capacity capacity : ot.getCapacities()) {
			if (capacity.getDay() == day) {
				return capacity;
			}
		}

		throw new UnsupportedOperationException("Capacity with day number " + day + " not found in OT " + ot.getName());
	}

	private List<Capacity> getCapacitiesForDay(final int day) {
		if (day < 0) {
			throw new IllegalArgumentException("Given day number was negative.");
		}

		final List<Capacity> allCapacities = new LinkedList<Capacity>();

		for (final OT ot : model.getOts()) {
			try {
				allCapacities.add(getCapacityForRoomOnDay(ot, day));
			} catch (final UnsupportedOperationException ex) {
			}
		}

		return allCapacities;
	}

}
