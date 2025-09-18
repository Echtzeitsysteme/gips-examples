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
import ihtcvirtualmetamodel.Patient;
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

/**
 * This app can be used to create the necessary pre-processing edges of the
 * virtual IHTC scenario without involving GT.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
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
	 * Model that should be worked on.
	 */
	private Root model = null;

	/**
	 * Creates a new instance of the pre-processing (non-GT) app. The given
	 * `xmiInputFilePath` will be used as input file path. The given
	 * `xmiOutputFilePath` will be used as output file path.
	 * 
	 * @param xmiInputFilePath  Input file path.
	 * @param xmiOutputFilePath Output file path.
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
	 * Executes the GT rules of this app according to the configuration.
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

	/**
	 * Creates all necessary virtual shift to workload objects for the occupants.
	 */
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
					v.setRequires_virtualShiftToWorkload(vPrev);
					vPrev.setEnables_virtualShiftToWorkload(v);
				}
				shift.getVirtualWorkload().add(v);
				vPrev = v;

				// Delete derived edges
				workload.setDerivedShift(null);
				shift.getDerivedWorkloads().remove(workload);
			}

		});
	}

	/**
	 * Creates all virtual shift to roster objects for the assignment of nurses to
	 * room.
	 */
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

	/**
	 * Creates all virtual OP time to capacity objects.
	 */
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

	/**
	 * Creates all virtual workload to OP time and workload to capacity objects.
	 */
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
							// Set all enabled and required edges
							vnew.getRequires_virtualOpTimeToCapacity().addAll(opTime.getVirtualCapacity());
							opTime.getVirtualCapacity().forEach(vc -> {
								vc.getEnables_virtualWorkloadToOpTime().add(vnew);
							});

							opTime.getVirtualWorkload().add(vnew);
						}
					}
				}
			});

		});

		// Create virtual edges between Workload and Capacity
		model.getOts().forEach(ot -> {
			ot.getCapacities().forEach(c -> {
				// This ensures we only look at OpTime, Capacity tuples on the same day.
				c.getVirtualOpTime().forEach(vop -> {
					vop.getOpTime().getVirtualWorkload().forEach(vw -> {
						final Workload w = vw.getWorkload();
						final VirtualWorkloadToCapacity vnew = IhtcvirtualmetamodelFactory.eINSTANCE
								.createVirtualWorkloadToCapacity();
						vnew.setWorkload(w);
						vnew.setCapacity(c);
						vnew.setIsSelected(false);

						vnew.getRequires_virtualWorkloadToOpTime().add(vw);
						vw.getEnables_virtual_WorkloadToCapacity().add(vnew);

						c.getVirtualWorkload().add(vnew);
					});
				});
			});
		});
	}

	/**
	 * Creates all virtual shift to workload elements for the initial assignment of
	 * patients to rooms.
	 */
	private void createVirtualShiftToWorkloadInitialCandidates() {
		Objects.requireNonNull(model);
		model.getPatients().stream().filter(patient -> !patient.isIsOccupant()).forEach(patient -> {
			model.getRooms().stream().filter(room -> !patient.getIncompatibleRooms().contains(room)).forEach(room -> {
				room.getShifts().forEach(shift -> {
					// Check shift time conditions (i.e., only use the first shift per day)
					if (shift.getShiftNo() % 3 == 0) {
						// If an occupant with a different gender is assigned to the same room during the potential stay time -> Don't create virtual shifts
						final List<Patient> occupantsInRoom = model.getPatients().stream() // Get all patients from the model
								.filter(occupant -> occupant.isIsOccupant() // Only take occupants into account
								 && occupant.getFirstWorkload().getVirtualShift().get(0).getShift().getRoom().equals(room)) // The occupant's room must match the patient's room
								.toList();
						// Find latest day of stay of all occupants in the room
						int lastDay = 0;
						// Genders of all occupants in the same room are equal
						String allOccupantGenderInRoom = patient.getGender(); // To be overwritten if the Gender of occupants in the same room and time do not match
						// Find earliest day a occupant leaves the room
						int earliestDay = model.getPeriod();
						for(final Patient p : occupantsInRoom) {
							if(lastDay < p.getStayLength() - 1) {
								lastDay = p.getStayLength() - 1;
								allOccupantGenderInRoom = p.getGender();
							}
							if(earliestDay > p.getStayLength()) {
								earliestDay = p.getStayLength();
							}
						}
						// If the room is already completely full with occupants -> don't create VSW
						int occupantsPerRoom = occupantsInRoom.size();
						final boolean roomFull = occupantsPerRoom == room.getBeds() && shift.getShiftNo() / 3 < earliestDay;
						// Check if gender of the occupants does *not* match the gender of the patient
						// ... latest occupant's stay is within the time frame
						final boolean genderMix = !patient.getGender().equals(allOccupantGenderInRoom) && shift.getShiftNo() / 3 <= lastDay;
						
						// ... do/do not create virtual shift objects
						if (!genderMix && !roomFull) {
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
									if ((day - patient.getEarliestDay()) * model.getWeight().getPatientDelay() > model.getWeight().getUnscheduledOptional() ) {
										// If the cost to delay the patient is higher than the cost to not schedule him at all -> Don't create virtual shifts
									} else {
										final VirtualShiftToWorkload v = IhtcvirtualmetamodelFactory.eINSTANCE
												.createVirtualShiftToWorkload();
										v.setIsSelected(false);
										v.setWasImported(false);
										v.setShift(shift);
										v.setWorkload(patient.getFirstWorkload());
										v.getRequires_virtualWorkloadToCapacity()
												.addAll(patient.getFirstWorkload().getVirtualCapacity().stream().filter(
														vwc -> vwc.getCapacity().getDay() == v.getShift().getShiftNo() / 3).toList());
										patient.getFirstWorkload().getVirtualCapacity().stream().filter(
												vwc -> vwc.getCapacity().getDay() == v.getShift().getShiftNo() / 3).toList()
												.forEach(vc -> {
													vc.getEnables_virtualShiftToWorkload().add(v);
										});
										shift.getVirtualWorkload().add(v);
									}
								}
							}
						}
					}
				});
			});
		});
	}

	/**
	 * Creates all following virtual shift to workload elements that build upon the
	 * previously created initial assignments of patients to rooms.
	 */
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

	/**
	 * Extends the "ladder" of workloads assigned to a specific room, i.e., to its
	 * shifts.
	 * 
	 * @param initShift    Initially assigned shift object.
	 * @param initWorkload Initially assigned workload object, i.e., this should be
	 *                     the first workload of a patient.
	 * @param initV        Initially created virtual shift to workload object of the
	 *                     first possible assignment.
	 */
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
			vNew.setRequires_virtualShiftToWorkload(v);
			v.setEnables_virtualShiftToWorkload(vNew);
			s.getVirtualWorkload().add(vNew);
			w = (Workload) w.getNext();
			s = (Shift) s.getNext();
			v = vNew;
		}
	}

	//
	// Utility methods.
	//

	/**
	 * Searches for and returns a specific shift object of a given room with a given
	 * shift number. This method assumes that only one object matching the described
	 * criteria can be present.
	 * 
	 * @param room    Room to search for the respective shift.
	 * @param shiftNo Shift number to search for.
	 * @return Shift object of the given room that matches the given shift number.
	 */
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

	/**
	 * Searches for and returns a specific capacity object of a given OT with a
	 * given day number. This method assumes that only one object matching the
	 * described criteria can be present.
	 * 
	 * @param ot  OT to search for the respective capacity.
	 * @param day Day to search for.
	 * @return Capacity object of the given OT that matches the given day number.
	 */
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

	/**
	 * Returns all capacity objects of all OTs for a specific day.
	 * 
	 * @param day Day number to search for.
	 * @return List of all capacity objects of all OTs for a specific day.
	 */
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
