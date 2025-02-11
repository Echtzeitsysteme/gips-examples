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
				final int nurseMaximumWorkload = n.getShiftMaxLoads().get(rsna.getShift().getId()).getMaxLoad();
				if (nurseMaximumWorkload < nurseSpecificAssignedWorkload) {
					excessCost += (nurseSpecificAssignedWorkload - nurseMaximumWorkload);
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
			// assigned room must match
			if (p.getAssignedRoom() != null && p.getAssignedRoom().equals(r)) {
				// time frame must match
				if (d.getId() >= p.getAdmissionDay().getId()
						&& d.getId() <= p.getAdmissionDay().getId() + p.getLengthOfStay()) {
					final int age = convertAgeGroupToInt(model, p.getAgeGroup());
					if (maxAgeFound < age) {
						maxAgeFound = age;
					}
					if (minAgeFound > age) {
						minAgeFound = age;
					}
				}
			}
		}

		// find minimum and maximum age of occupants previously assigned to room `r` on
		// day `d`
		for (final Occupant o : model.getOccupants()) {
			// previously assigned room must match
			if (o.getRoomId() == r.getName()) {
				// time frame must match
				if (o.getLengthOfStay() <= d.getId()) {
					final int age = convertAgeGroupToInt(model, o.getAgeGroup());
					if (maxAgeFound < age) {
						maxAgeFound = age;
					}
					if (minAgeFound > age) {
						minAgeFound = age;
					}
				}
			}
		}

		return maxAgeFound - minAgeFound;
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
		}
		return ageCounter;
	}

	private int calculateSkillLevelCostPerNursePatientShift(final Nurse nurse, final Patient patient,
			final Shift shift) {
		int cost = 0;
		final int nurseLevel = nurse.getSkillLevel();

		// calculate relative shift index to get the correct required skill level from
		// patient
		final int index = patient.getAdmissionDay().getShifts().get(0).getId() + shift.getId();
		final int patientLevel = patient.getSkillLevelsRequired().get(index).getSkillLevelRequired();

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
		final int occupantLevel = occupant.getSkillLevelsRequired().get(shift.getId()).getSkillLevelRequired();

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
			// room must match
			if (p.getAssignedRoom() != null && p.getAssignedRoom().equals(room)) {
				// day must match
				if (day.getId() >= p.getAdmissionDay().getId()
						&& day.getId() <= p.getAdmissionDay().getId() + p.getLengthOfStay()) {
					patientsInRoom.add(p);
				}
			}
		}
		return patientsInRoom;
	}

	private List<Occupant> getOccupantsInRoomOnDay(final Hospital model, final Room room, final Day day) {
		final List<Occupant> occupantsInRoom = new ArrayList<Occupant>();
		for (final Occupant o : model.getOccupants()) {
			// room must match
			if (o.getRoomId() != null && o.getRoomId().equals(room.getName())) {
				// day must match
				if (day.getId() <= o.getLengthOfStay()) {
					occupantsInRoom.add(o);
				}
			}
		}
		return occupantsInRoom;
	}

	private int getWorkloadOfOccupantByShift(final Occupant o, final Shift s) {
		return o.getWorkloadsProduced().get(s.getId()).getWorkloadProduced();
	}

	private int getWorkloadOfPatientByShift(final Patient p, final Shift shift) {
		return p.getWorkloadsProduced().get(p.getAdmissionDay().getShifts().get(0).getId() + shift.getId())
				.getWorkloadProduced();
	}

	private int countPatientNurses(final Hospital model, final Patient patient) {
		int count = 0;

		for (final Nurse n : model.getNurses()) {
			for (final RoomShiftNurseAssignment rsna : n.getAssignedRoomShifts()) {
				// room must match
				if (rsna.getRoom().getName().equals(patient.getAssignedRoom().getName())) {
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
