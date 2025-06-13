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

			// Create virtual edges between Workload and OpTime
			patient.getSurgeon().getOpTimes().forEach(opTime -> {
				// Check time frame (stay of the patient)
				if (patient.getEarliestDay() <= opTime.getDay() && opTime.getDay() <= patient.getDueDay()) {
					// Check time frame (duration of the surgery - surgeon)
					if (patient.getSurgeryDuration() <= opTime.getMaxOpTime()) {
						// Check if respective opTime object has at least one OT capacity available
						if (!opTime.getVirtualCapacity().isEmpty()) {

							final VirtualWorkloadToOpTime vnew = IhtcvirtualmetamodelFactory.eINSTANCE
									.createVirtualWorkloadToOpTime();
							vnew.setIsSelected(false);
							vnew.setOpTime(opTime);
							vnew.setWorkload(w);
							// TODO: The requires edge and the enable edge are not necessarily correct here.
							// In this implementation, only the first virtual capacity object will be used,
							// but it should be all of them. If I am correct, we do not have a common
							// understanding on what the requires edges actually mean in detail.
							vnew.getRequires_virtualOpTimeToCapacity().add(opTime.getVirtualCapacity().get(0));
							opTime.getVirtualCapacity().get(0).getEnables_virtualWorkloadToOpTime().add(vnew);

							opTime.getVirtualWorkload().add(vnew);
						}
					}
				}
			});

			// Create virtual edges between Workload and Capacity
			model.getOts().forEach(ot -> {
				ot.getCapacities().forEach(capacity -> {
					patient.getSurgeon().getOpTimes();

					VirtualOpTimeToCapacity vexists = null;
					for (final var v : capacity.getVirtualOpTime()) {
						if (v.getOpTime().getSurgeon().equals(patient.getSurgeon())) {
							vexists = v;
							break;
						}
					}

					// If the surgeon can possibly be assigned to the respective Capacity object,
					// the virtual edge between Workload and Capacity must be created.
					if (vexists != null) {
						final VirtualWorkloadToCapacity vnew = IhtcvirtualmetamodelFactory.eINSTANCE
								.createVirtualWorkloadToCapacity();
						vnew.setIsSelected(false);
						vnew.setCapacity(capacity);
						vnew.setWorkload(w);
						// TODO: The requires edge and the enable edge are not necessarily correct here.
						// In this implementation, only the first virtual capacity object will be used,
						// but it should be all of them. If I am correct, we do not have a common
						// understanding on what the requires edges actually mean in detail.
						vnew.getRequires_virtualOpTimeToCapacity().add(vexists);
						vexists.getEnables_virtual_WorkloadToCapacity().add(vnew);

						capacity.getVirtualWorkload().add(vnew);
					}
				});
			});
		});
	}

	private void createVirtualOpTimeToCapacityCandidates() {
		Objects.requireNonNull(model);
		model.getSurgeons().forEach(surgeon -> {
			surgeon.getOpTimes().forEach(opTime -> {
				if (opTime.getMaxOpTime() > 0) {
					final List<Capacity> allCapacitiesOnDay = getCapacitiesForDay(opTime.getDay());
					for (final Capacity capacity : allCapacitiesOnDay) {
						if (capacity.getMaxCapacity() <= 0) {
							continue;
						}
						final VirtualOpTimeToCapacity v = IhtcvirtualmetamodelFactory.eINSTANCE
								.createVirtualOpTimeToCapacity();
						v.setIsSelected(false);
						v.setOpTime(opTime);
						v.setCapacity(capacity);
						capacity.getVirtualOpTime().add(v);
					}
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
				// Set requires and enables edges
				if (vPrev != null) {
					v.getRequires_virtualShiftToWorkload().add(vPrev);
					vPrev.getEnables_virtualShiftToWorkload().add(v);
				}
				shift.getVirtualWorkload().add(v);
				vPrev = v;

				// Delete derived edges
				workload.setDerivedShift(null);
				shift.getDerivedWorkloads().remove(workload);
			}

		});
	}

	private void createVirtualShiftToRosterCandidates() {
		Objects.requireNonNull(model);
		model.getNurses().forEach(nurse -> {
			nurse.getRosters().forEach(roster -> {
				model.getRooms().forEach(room -> {
					try {
						final Shift shift = getShift(room, roster.getShiftNo());
						final VirtualShiftToRoster v = IhtcvirtualmetamodelFactory.eINSTANCE
								.createVirtualShiftToRoster();
						v.setIsSelected(false);
						v.setRoster(roster);
						v.setShift(shift);
						roster.getVirtualShift().add(v);
					} catch (final UnsupportedOperationException ex) {
					}
				});
			});
		});
	}

	private void createVirtualShiftToWorkloadInitialCandidates() {
		Objects.requireNonNull(model);
		model.getPatients().stream().filter(patient -> !patient.isIsOccupant()).forEach(patient -> {
			model.getRooms().stream().filter(room -> !patient.getIncompatibleRooms().contains(room)).forEach(room -> {
				room.getShifts().forEach(shift -> {
					// Check shift time conditions (i.e., only use the first shift per day)
					if (shift.getShiftNo() % 3 == 0) {
						// Check if the shift number / 3 matches any available OT's capacity object
						final int day = shift.getShiftNo() / 3;
						VirtualWorkloadToCapacity vfound = null;
						for (final var vexists : patient.getFirstWorkload().getVirtualCapacity()) {
							if (vexists.getCapacity().getDay() == day) {
								vfound = vexists;
								break;
							}
						}
						if (vfound != null) {
							// Check if shift is in potential start time frame
							if (day >= patient.getEarliestDay() && day <= patient.getDueDay()) {
								final VirtualShiftToWorkload v = IhtcvirtualmetamodelFactory.eINSTANCE
										.createVirtualShiftToWorkload();
								v.setIsSelected(false);
								v.setWasImported(false);
								v.setShift(shift);
								v.setWorkload(patient.getFirstWorkload());
								// TODO: The requires edge and the enable edge are not necessarily correct here.
								// In this implementation, only the first virtual capacity object will be used,
								// but it should be all of them. If I am correct, we do not have a common
								// understanding on what the requires edges actually mean in detail.
								v.getRequires_virtualWorkloadToCapacity().add(vfound);
								vfound.getEnables_virtualShiftToWorkload().add(v);
								shift.getVirtualWorkload().add(v);
							}
						}

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
			// Skip occupants
			if (vinit.getWorkload().getPatient().isIsOccupant()) {
				continue;
			}
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
			v.getEnables_virtualShiftToWorkload().add(vNew);
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
