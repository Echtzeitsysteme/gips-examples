package ihtcmetamodel.loader;

import java.util.ArrayList;
import java.util.List;

import ihtcmetamodel.AgeGroup;
import ihtcmetamodel.Day;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.Nurse;
import ihtcmetamodel.Occupant;
import ihtcmetamodel.OperatingTheater;
import ihtcmetamodel.Patient;
import ihtcmetamodel.Room;
import ihtcmetamodel.RoomShiftNurseAssignment;
import ihtcmetamodel.Shift;
import ihtcmetamodel.Surgeon;
import ihtcmetamodel.SurgeryAssignment;

/**
 * This class is a helper to calculate (soft) constraint costs for S1 to S8.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class ModelCostCalculator {

	/**
	 * Soft constraint Patient Admission Scheduling, S1.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Age mix cost for the whole model.
	 */
	protected int calculateAgeMixCost(final Hospital model) {
		int ageMixCost = 0;
		for (final Day d : model.getDays()) {
			for (final Room r : model.getRooms()) {
				ageMixCost += getMaxAgeDifferenceInRoomAtDay(model, r, d);
			}
		}
		return ageMixCost * model.getWeight().getRoomMixedAge();
	}

	/**
	 * Soft constraint Nurse-to-Room Assignment, S2.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Skill level cost for the whole model.
	 */
	protected int calculateSkillLevelCost(final Hospital model) {
		int skillLevelCost = 0;
		for (final Nurse n : model.getNurses()) {
			for (final RoomShiftNurseAssignment rsna : n.getAssignedRoomShifts()) {
				// all patients in this room
				final List<Patient> patientsInRoom = getPatientsInRoomOnDay(model, rsna.getRoom(),
						rsna.getShift().getDay());
				for (final Patient p : patientsInRoom) {
					skillLevelCost += calculateSkillLevelCostPerNursePatientShift(n, p, rsna.getShift());
				}

				// all occupants in this room
				final List<Occupant> occupantsInRoom = getOccupantsInRoomOnDay(model, rsna.getRoom(),
						rsna.getShift().getDay());
				for (final Occupant o : occupantsInRoom) {
					skillLevelCost += calculateSkillLevelCostPerNurseOccupantShift(n, o, rsna.getShift());
				}
			}
		}
		return skillLevelCost * model.getWeight().getRoomNurseSkill();
	}

	/**
	 * Soft constraint Nurse-to-Room Assignment, S3.
	 * 
	 * This implementation is partly inspired by the given C++ validator code.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Continuity cost for the whole model.
	 */
	protected int calculateContinuityCost(final Hospital model) {
		int continuityCost = 0;

		// Occupants
		for (final Occupant o : model.getOccupants()) {
			final int localOccupantCount = countOccupantNurses(model, o);
			continuityCost += localOccupantCount;
		}

		// Patients
		for (final Patient p : model.getPatients()) {
			final int localPatientCount = countPatientNurses(model, p);
			continuityCost += localPatientCount;
		}

		return continuityCost * model.getWeight().getContinuityOfCare();
	}

	/**
	 * Soft constraint Nurse-to-Room Assignment, S4.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Excess cost for the whole model.
	 */
	protected int calculateExcessCost(final Hospital model) {
		int excessCost = 0;

		for (final Nurse n : model.getNurses()) {
			for (final RoomShiftNurseAssignment rsna : n.getAssignedRoomShifts()) {
				// accumulate all workloads in this shift across all rooms
				int nurseSpecificAssignedWorkload = 0;
				final List<Occupant> occupants = getOccupantsInRoomOnDay(model, rsna.getRoom(),
						rsna.getShift().getDay());
				final List<Patient> patients = getPatientsInRoomOnDay(model, rsna.getRoom(), rsna.getShift().getDay());

				// calculate actual work load in this room and shift
				int workloadInRoomAndShift = 0;
				for (final Occupant o : occupants) {
					workloadInRoomAndShift += getWorkloadOfOccupantByShift(o, rsna.getShift());
				}
				for (final Patient p : patients) {
					workloadInRoomAndShift += getWorkloadOfPatientByShift(p, rsna.getShift());
				}

				nurseSpecificAssignedWorkload += workloadInRoomAndShift;

				// check if workload of nurse `n` was exceeded for this shift
				// TODO
				try {
					final int nurseMaximumWorkload = n.getShiftMaxLoads().get(rsna.getShift().getId()).getMaxLoad();
					if (nurseMaximumWorkload < nurseSpecificAssignedWorkload) {
						excessCost += (nurseSpecificAssignedWorkload - nurseMaximumWorkload);
					}
				} catch (final RuntimeException ex) {
//					System.out.println("");
				}
			}

		}

		return excessCost * model.getWeight().getNurseEccessiveWorkload();
	}

	/**
	 * Soft constraint Surgical Case Planning, S5.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Number of open OTs cost for the whole model.
	 */
	protected int calculateOpenOtCost(final Hospital model) {
		int openOtCost = 0;

		for (final OperatingTheater ot : model.getOperatingTheaters()) {
			final List<SurgeryAssignment> assignments = ot.getSurgeryAssignments();
			for (final Day d : model.getDays()) {
				// check if `ot` has at least one surgery on day `d`
				for (final SurgeryAssignment sa : assignments) {
					if (sa.getDay().equals(d)) {
						openOtCost++;
						break;
					}
				}
			}
		}

		return openOtCost * model.getWeight().getOpenOperatingTheater();
	}

	/**
	 * Soft constraint Surgical Case Planning, S6.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Surgeon transfer cost for the whole model.
	 */
	protected int calculateSurgeonTransferCost(final Hospital model) {
		int surgeonTransferCost = 0;

		for (final Surgeon s : model.getSurgeons()) {
			final List<SurgeryAssignment> assignments = s.getSurgeryAssignments();
			for (final OperatingTheater ot : model.getOperatingTheaters()) {
				for (final Day d : model.getDays()) {
					for (final SurgeryAssignment sa : assignments) {
						// day must be matched
						if (sa.getDay().equals(d)) {
							// operating theater must be matched
							if (sa.getOperationTheater().equals(ot)) {
								surgeonTransferCost++;
							}
						}
					}
				}
			}

		}

		return surgeonTransferCost * model.getWeight().getSurgeonTransfer();
	}

	/**
	 * Soft constraint Global constraints, S7.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Admission delay cost for the whole model.
	 */
	protected int calculateAdmissionDelayCost(final Hospital model) {
		int admissionDelayCost = 0;

		for (final Patient p : model.getPatients()) {
			// check if patient was scheduled at all
			if (p.getAdmissionDay() != null) {
				// I am not completely sure if this condition is correct
				if (p.getAdmissionDay().getId() > p.getAdmissionDay().getId() + p.getSurgeryReleaseDay()) {
					admissionDelayCost += (p.getAdmissionDay().getId() - p.getSurgeryReleaseDay());
				}
			}
		}

		return admissionDelayCost * model.getWeight().getPatientDelay();
	}

	/**
	 * Soft constraint Global constraints, S8.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Unscheduled patients cost for the whole model.
	 */
	protected int calculateUnscheduledPatientsCost(final Hospital model) {
		int unscheduledPatientsCost = 0;

		for (final Patient p : model.getPatients()) {
			if (p.getAdmissionDay() == null) {
				unscheduledPatientsCost++;
			}
		}

		return unscheduledPatientsCost * model.getWeight().getUnscheduledOptional();
	}

	/*
	 * Internal utility methods.
	 */

	/**
	 * This method calculates the maximum age difference for a given room `r` on day
	 * `d` for all new patients and all previously assigned occupants which are
	 * already placed in this room.
	 * 
	 * @param model Complete hospital model.
	 * @param r     Room.
	 * @param d     Day.
	 * @return Maximum age difference of all persons in room `r` on day `d`.
	 */
	private int getMaxAgeDifferenceInRoomAtDay(final Hospital model, final Room r, final Day d) {
		int maxAgeFound = Integer.MIN_VALUE;
		int minAgeFound = Integer.MAX_VALUE;

		// find minimum and maximum age of new patients assigned to room `r` on day `d`
		for (final Patient p : model.getPatients()) {
			if (patientInRoomOnDay(p, r, d)) {
				final int age = convertAgeGroupToInt(model, p.getAgeGroup());
				if (maxAgeFound < age) {
					maxAgeFound = age;
				}
				if (minAgeFound > age) {
					minAgeFound = age;
				}
			}
		}

		// find minimum and maximum age of occupants previously assigned to room `r` on
		// day `d`
		for (final Occupant o : model.getOccupants()) {
			if (occupantInRoomOnDay(o, r, d)) {
				final int age = convertAgeGroupToInt(model, o.getAgeGroup());
				if (maxAgeFound < age) {
					maxAgeFound = age;
				}
				if (minAgeFound > age) {
					minAgeFound = age;
				}
			}
		}

		int cost = 0;
		if (maxAgeFound > minAgeFound) {
			cost = maxAgeFound - minAgeFound;
		}

		return cost;
	}

	/**
	 * Returns true if the patient `p` was assigned to stay in room `r` on day `d`.
	 * 
	 * @param p Patient.
	 * @param r Room.
	 * @param d Day.
	 * @return True if the condition above holds.
	 */
	private boolean patientInRoomOnDay(final Patient p, final Room r, final Day d) {
		// patient must have an assigned room
		if (p.getAssignedRoom() != null) {
			// patient must have an assigned admission day
			if (p.getAdmissionDay() != null) {
				// room must match
				if (p.getAssignedRoom().equals(r)) {
					// day must lay within the time frame of the patient's stay
					final int admissionDayId = p.getAdmissionDay().getId();
					if (d.getId() >= admissionDayId
							&& d.getId() <= p.getAdmissionDay().getId() + p.getLengthOfStay() - 1) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Returns true if the occupant `o` was assigned to stay in room `r` on day `d`.
	 * 
	 * @param o Occupant.
	 * @param r Room.
	 * @param d Day.
	 * @return True if the condition above holds.
	 */
	private boolean occupantInRoomOnDay(final Occupant o, final Room r, final Day d) {
		if (o.getRoomId().equals(r.getName())) {
			if (d.getId() <= o.getLengthOfStay() - 1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Converts the name of a given age group to an integer value. I.e., this method
	 * searches the index (starting with 0) of the given name of an age group in all
	 * possible age groups within the hospital model.
	 * 
	 * @param model    Complete hospital model.
	 * @param ageGroup Given age group to find the integer value (index) for.
	 * @return Integer value (index) of the given age group.
	 */
	private int convertAgeGroupToInt(final Hospital model, final String ageGroup) {
		int ageCounter = 0;
		for (final AgeGroup ag : model.getAgeGroups()) {
			if (ag.getName().equals(ageGroup)) {
				break;
			}
			ageCounter++;
		}
		return ageCounter;
	}

	private int calculateSkillLevelCostPerNursePatientShift(final Nurse nurse, final Patient patient,
			final Shift shift) {
		int cost = 0;
		final int nurseLevel = nurse.getSkillLevel();

		// calculate relative shift index to get the correct required skill level from
		// patient
//		final int index = patient.getAdmissionDay().getShifts().get(0).getId() + shift.getId();
		final int index = shift.getId() - patient.getAdmissionDay().getShifts().get(0).getId();

		// TODO
		int patientLevel = 0;
		try {
			patientLevel = patient.getSkillLevelsRequired().get(index).getSkillLevelRequired();
		} catch (final RuntimeException e) {
			patientLevel = Integer.MIN_VALUE;
		}

		/*
		 * "If the skill level of the nurse assigned to a patient’s room in a shift does
		 * not reach the minimum level required by that patient, a penalty is incurred
		 * equal to the difference between the two skill levels. Note that a nurse with
		 * a skill level greater than the minimum required can be assigned to the room
		 * at no additional cost."
		 */
		if (patientLevel > nurseLevel) {
			cost = patientLevel - nurseLevel;
		}
		return cost;
	}

	private int calculateSkillLevelCostPerNurseOccupantShift(final Nurse nurse, final Occupant occupant,
			final Shift shift) {
		int cost = 0;
		final int nurseLevel = nurse.getSkillLevel();

		// occupant
		final int shiftId = shift.getId();

		// TODO
		int occupantLevel = 0;
		try {
			final var requiredSkillLevels = occupant.getSkillLevelsRequired();
			final var requiredSkillLevel = requiredSkillLevels.get(shiftId);
//		final int occupantLevel = occupant.getSkillLevelsRequired().get(shift.getId()).getSkillLevelRequired();
			occupantLevel = requiredSkillLevel.getSkillLevelRequired();
		} catch (final RuntimeException ex) {

		}

		/*
		 * "If the skill level of the nurse assigned to a patient’s room in a shift does
		 * not reach the minimum level required by that patient, a penalty is incurred
		 * equal to the difference between the two skill levels. Note that a nurse with
		 * a skill level greater than the minimum required can be assigned to the room
		 * at no additional cost."
		 * 
		 * Assumption: We assume this also holds true for all occupants that are not
		 * strictly speaking new patients.
		 */
		if (occupantLevel > nurseLevel) {
			cost = occupantLevel - nurseLevel;
		}
		return cost;
	}

	private List<Patient> getPatientsInRoomOnDay(final Hospital model, final Room room, final Day day) {
		final List<Patient> patientsInRoom = new ArrayList<Patient>();
		for (final Patient p : model.getPatients()) {
			// room and day must match
			if (patientInRoomOnDay(p, room, day)) {
				patientsInRoom.add(p);
			}
		}
		return patientsInRoom;
	}

	private List<Occupant> getOccupantsInRoomOnDay(final Hospital model, final Room room, final Day day) {
		final List<Occupant> occupantsInRoom = new ArrayList<Occupant>();
		for (final Occupant o : model.getOccupants()) {
			// room and day must match
			if (occupantInRoomOnDay(o, room, day)) {
				occupantsInRoom.add(o);
			}
		}
		return occupantsInRoom;
	}

	private int getWorkloadOfOccupantByShift(final Occupant o, final Shift s) {
		// TODO
		try {
			return o.getWorkloadsProduced().get(s.getId()).getWorkloadProduced();
		} catch (final Exception ex) {
			return 0;
		}
	}

	private int getWorkloadOfPatientByShift(final Patient p, final Shift shift) {
		// TODO
		try {
			return p.getWorkloadsProduced().get(p.getAdmissionDay().getShifts().get(0).getId() + shift.getId())
					.getWorkloadProduced();
		} catch (final RuntimeException ex) {
			return 0;
		}
	}

	private int countPatientNurses(final Hospital model, final Patient patient) {
		int count = 0;

		for (final Nurse n : model.getNurses()) {
			for (final RoomShiftNurseAssignment rsna : n.getAssignedRoomShifts()) {
				// room must match
				if (patient.getAssignedRoom() != null
						&& rsna.getRoom().getName().equals(patient.getAssignedRoom().getName())) {
					// time frame must match
					if (rsna.getShift().getDay().getId() >= patient.getAdmissionDay().getId() && rsna.getShift()
							.getDay().getId() <= patient.getAdmissionDay().getId() + patient.getLengthOfStay()) {
						count++;
					}
				}
			}
		}

		return count;
	}

	private int countOccupantNurses(final Hospital model, final Occupant occupant) {
		int count = 0;

		for (final Nurse n : model.getNurses()) {
			for (final RoomShiftNurseAssignment rsna : n.getAssignedRoomShifts()) {
				// room must match
				if (rsna.getRoom().getName().equals(occupant.getRoomId())) {
					// time frame must match
					if (rsna.getShift().getDay().getId() <= occupant.getLengthOfStay()) {
						count++;
					}
				}
			}
		}

		return count;
	}

}
