package ihtcvirtualmetamodel.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import ihtcvirtualmetamodel.Workload;

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
	public int calculateAgeMixCost(final Root model) {
		int ageMixCost = 0;
		for (final Room r : model.getRooms()) {
			for (final Shift s : r.getShifts()) {
				// Only take shifts with type 'early' into account
				if (s.getShiftNo() % 3 != 0) {
					continue;
				}

				ageMixCost += getMaxAgeDifferenceInShift(s);
			}
		}
		return ageMixCost * model.getWeight().getRoomMixedAge();
	}

	/**
	 * This method calculates the maximum age difference for a given room `r` on day
	 * `d` for all new patients and all previously assigned occupants which are
	 * already placed in this room. `r` and `d` will be determined by the given
	 * shift `s`.
	 * 
	 * @param s Shift.
	 * @return Maximum age difference of all persons in room `r` on day `d`.
	 */
	private int getMaxAgeDifferenceInShift(final Shift s) {
		int minAge = Integer.MAX_VALUE;
		int maxAge = Integer.MIN_VALUE;

		for (final Workload w : s.getDerivedWorkloads()) {
			final int age = w.getPatient().getAgeGroup();
			if (age < minAge) {
				minAge = age;
			}
			if (age > maxAge) {
				maxAge = age;
			}
		}

		int cost = 0;
		if (maxAge > minAge) {
			cost = maxAge - minAge;
		}

		return cost;
	}

	/**
	 * Soft constraint Nurse-to-Room Assignment, S2.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Skill level cost for the whole model.
	 */
	public int calculateSkillLevelCost(final Root model) {
		int skillLevelCost = 0;
		for (final Nurse n : model.getNurses()) {
			for (final Roster r : n.getRosters()) {
				for (final Shift s : r.getDerivedShifts()) {
					// all patients in this room
					final List<Patient> patientsInRoom = getPatientsInRoomOnDay(model, s.getRoom(), shiftToDay(s));
					for (final Patient p : patientsInRoom) {
						skillLevelCost += calculateSkillLevelCostPerNursePatientShift(n, p, s.getShiftNo());
					}
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
	public int calculateContinuityCost(final Root model) {
		int continuityCost = 0;

		// Patients
		for (final Patient p : model.getPatients()) {
			final int localPatientCount = countPatientsNurses(model, p);
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
	public int calculateExcessCost(final Root model) {
		int excessCost = 0;

		for (final Nurse n : model.getNurses()) {
			// find all shifts a nurse works on
			final Set<Shift> allWorkingShiftsOfNurse = new HashSet<Shift>();
			n.getRosters().forEach(r -> r.getDerivedShifts().forEach(s -> allWorkingShiftsOfNurse.add(s)));

			for (final Shift s : allWorkingShiftsOfNurse) {
				final int nurseMaximumWorkload = findNurseMaxLoadInShift(n, s.getShiftNo());

				// accumulate all workloads in this shift across all rooms
				int nurseSpecificAssignedWorkload = 0;

				final List<Patient> patients = getPatientsInRoomOnDay(model, s.getRoom(), shiftToDay(s));
				// calculate actual work load in this room and shift
				for (final Patient p : patients) {
					nurseSpecificAssignedWorkload += getWorkloadOfPatientByShift(p, s.getShiftNo());
				}

				// check if workload of nurse `n` was exceeded for this shift
				if (nurseMaximumWorkload < nurseSpecificAssignedWorkload) {
					excessCost += (nurseSpecificAssignedWorkload - nurseMaximumWorkload);
				}
			}
		}

		return excessCost * model.getWeight().getNurseExcessiveWorkload();
	}

	/**
	 * Soft constraint Surgical Case Planning, S5.
	 * 
	 * @param model Hospital model to calculate the cost from.
	 * @return Number of open OTs cost for the whole model.
	 */
	public int calculateOpenOtCost(final Root model) {
		int openOtCost = 0;

		for (final OT ot : model.getOts()) {
			// This assumes that there is at most one `Capacity` object per day per OT
			for (final Capacity c : ot.getCapacities()) {
				if (c.getDerivedOpTimes().size() > 0 || c.getDerivedWorkloads().size() > 0) {
					openOtCost++;
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
	public int calculateSurgeonTransferCost(final Root model) {
		int surgeonTransferCost = 0;

		for (final Surgeon s : model.getSurgeons()) {
			for (final OpTime opTime : s.getOpTimes()) {
				if (opTime.getDerivedCapacities().size() > 1) {
					surgeonTransferCost += opTime.getDerivedCapacities().size() - 1;
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
	public int calculateAdmissionDelayCost(final Root model) {
		int admissionDelayCost = 0;

		for (final Patient p : model.getPatients()) {
			// check if patient was scheduled at all
			if (p.getFirstWorkload().getDerivedShift() != null) {
				if (shiftToDay(p.getFirstWorkload().getDerivedShift()) > p.getEarliestDay()) {
					admissionDelayCost += (shiftToDay(p.getFirstWorkload().getDerivedShift()) - p.getEarliestDay());
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
	public int calculateUnscheduledPatientsCost(final Root model) {
		int unscheduledPatientsCost = 0;

		for (final Patient p : model.getPatients()) {
			if (!p.isMandatory() && p.getFirstWorkload().getDerivedShift() == null) {
				unscheduledPatientsCost++;
			}
		}

		return unscheduledPatientsCost * model.getWeight().getUnscheduledOptional();
	}

	/**
	 * Calculates the total cost for a complete hospital model.
	 * 
	 * @param model Hospital model to calculate cost from.
	 * @return Complete hospital cost.
	 */
	public int calculateTotalCost(final Root model) {
		int totalCost = 0;
		totalCost += calculateUnscheduledPatientsCost(model);
		totalCost += calculateAdmissionDelayCost(model);
		totalCost += calculateOpenOtCost(model);
		totalCost += calculateAgeMixCost(model);
		totalCost += calculateSkillLevelCost(model);
		totalCost += calculateExcessCost(model);
		totalCost += calculateContinuityCost(model);
		totalCost += calculateSurgeonTransferCost(model);
		return totalCost;
	}

	/*
	 * Internal utility methods.
	 */

	/**
	 * Converts a given shift number to the day number.
	 * 
	 * @param s Shift number.
	 * @return Day number.
	 */
	private int shiftToDay(final int s) {
		return s % 3;
	}

	/**
	 * Converts a given shift object to the day number.
	 * 
	 * @param s Shift object.
	 * @return Day number.
	 */
	private int shiftToDay(final Shift s) {
		return shiftToDay(s.getShiftNo());
	}

	/**
	 * Returns the maximum load of a nurse `n` in shift `s`.
	 * 
	 * @param n Nurse.
	 * @param s Shift.
	 * @return Maximum load of nurse `n` in shift `s`.
	 */
	private int findNurseMaxLoadInShift(final Nurse n, final int s) {
		int maxLoad = 0;

		for (final Roster r : n.getRosters()) {
			if (r.getShiftNo() == s) {
				for (final Shift shift : r.getDerivedShifts()) {
					for (final Workload w : shift.getDerivedWorkloads()) {
						if (w.getWorkloadValue() > maxLoad) {
							maxLoad = w.getWorkloadValue();
							// TODO: Is this correct???
						}
					}
				}
			}
		}

		return maxLoad;
	}

	/**
	 * Returns true if the patient `p` was assigned to stay in room `r` on day `d`.
	 * 
	 * @param p Patient.
	 * @param r Room.
	 * @param d Day.
	 * @return True if the condition above holds.
	 */
	private boolean patientInRoomOnDay(final Patient p, final Room r, final int d) {
		// patient must have an assigned room
		if (p.getFirstWorkload().getDerivedShift() != null) {
			// room must match
			if (p.getFirstWorkload().getDerivedShift().getRoom().equals(r)) {
				// day must lay within the time frame of the patient's stay
				final int firstDayNo = shiftToDay(p.getFirstWorkload().getDerivedShift());
				final int stayLength = p.getStayLength();

				if (d >= firstDayNo && d <= firstDayNo + stayLength) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Calculates the (possible) cost of one nurse `nurse` for patient `patient` on
	 * shift `shift.`
	 * 
	 * @param nurse   Nurse.
	 * @param patient Patient.
	 * @param shift   Shift.
	 * @return Cost of the nurse regarding the patient on the given shift.
	 */
	private int calculateSkillLevelCostPerNursePatientShift(final Nurse nurse, final Patient patient, final int shift) {
		int cost = 0;
		final int nurseLevel = nurse.getSkillLevel();

		int patientLevel = 0;
		for (final Workload w : patient.getWorkloads()) {
			if (w.getDerivedShift() != null && w.getDerivedShift().getShiftNo() == shift) {
				patientLevel = w.getMinNurseSkill();
				break;
			}
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

	/**
	 * Returns a list of all patients that are scheduled for the given room on the
	 * given day.
	 * 
	 * @param model Hospital model to extract data from.
	 * @param room  Room.
	 * @param day   Day.
	 * @return List of all patients that are scheduled for the given room on the
	 *         given day.
	 */
	private List<Patient> getPatientsInRoomOnDay(final Root model, final Room room, final int day) {
		final List<Patient> patientsInRoom = new ArrayList<Patient>();
		for (final Patient p : model.getPatients()) {
			// room and day must match
			if (patientInRoomOnDay(p, room, day)) {
				patientsInRoom.add(p);
			}
		}
		return patientsInRoom;
	}

	/**
	 * Returns the specific workload of the given patient `p` on shift `s`.
	 * 
	 * @param p       Patient.
	 * @param shiftNo Shift number.
	 * @return Specific workload of the given patient `p` on shift `s`.
	 */
	private int getWorkloadOfPatientByShift(final Patient p, final int shiftNo) {
		Workload w = p.getFirstWorkload();
		while (w.getNext() != null) {
			if (w.getDerivedShift().getShiftNo() == shiftNo) {
				return w.getWorkloadValue();
			}
			w = (Workload) w.getNext();
		}

		return 0;
	}

	/**
	 * Returns the distinct number of nurses a given patient has.
	 * 
	 * @param model   Hospital model to extract data from.
	 * @param patient Patient.
	 * @return Distinct number of nurses a given patient has.
	 */
	private int countPatientsNurses(final Root model, final Patient patient) {
		final Set<Nurse> foundNurses = new HashSet<Nurse>();

		for (final Workload w : patient.getWorkloads()) {
			if (w.getDerivedShift() != null //
					&& w.getDerivedShift().getDerivedRoster() != null //
					&& w.getDerivedShift().getDerivedRoster().getNurse() != null) {
				foundNurses.add(w.getDerivedShift().getDerivedRoster().getNurse());
			}
		}

		return foundNurses.size();
	}

}
