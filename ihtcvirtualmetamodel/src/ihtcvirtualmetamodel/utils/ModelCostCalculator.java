package ihtcvirtualmetamodel.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
		Objects.requireNonNull(model, "Given hospital model was null.");

		int ageMixCost = 0;
		for (final Room r : model.getRooms()) {
			for (final Shift s : r.getShifts()) {
				// Only take shifts with type 'early' into account and ignore all other shifts
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
	 * shift `s`. The virtual metamodel does not differentiate between patients and
	 * occupants.
	 * 
	 * @param s Shift.
	 * @return Maximum age difference of all persons in room `r` on day `d`.
	 */
	protected int getMaxAgeDifferenceInShift(final Shift s) {
		Objects.requireNonNull(s, "Given shift was null.");

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
		Objects.requireNonNull(model, "Given hospital model was null.");

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
		Objects.requireNonNull(model, "Given hospital model was null.");

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
		Objects.requireNonNull(model, "Given hospital model was null.");

		int excessCost = 0;

		for (final Nurse n : model.getNurses()) {
			// find all shifts a nurse works on
			final Set<Shift> allWorkingShiftsOfNurse = new HashSet<Shift>();
			n.getRosters().forEach(r -> r.getDerivedShifts().forEach(s -> allWorkingShiftsOfNurse.add(s)));

			// All shifts (of all rooms) must be grouped by shift number
			final Map<Integer, Set<Shift>> number2Shifts = new HashMap<>();
			for (final Shift s : allWorkingShiftsOfNurse) {
				if (!number2Shifts.containsKey(s.getShiftNo())) {
					number2Shifts.put(s.getShiftNo(), new HashSet<Shift>());
				}
				number2Shifts.get(s.getShiftNo()).add(s);
			}

			// Check conditions and increase cost if necessary
			for (final Integer i : number2Shifts.keySet()) {
				final Set<Shift> allShiftsWithNumber = number2Shifts.get(i);

				final int nurseMaximumWorkload = findNurseMaxLoadInShift(n, i);

				// accumulate all workloads in this shift across all rooms
				int nurseSpecificAssignedWorkload = 0;

				for (final Shift s : allShiftsWithNumber) {
					final List<Patient> patients = getPatientsInRoomOnDay(model, s.getRoom(), shiftToDay(s));
					// calculate actual work load in this room and shift
					for (final Patient p : patients) {
						nurseSpecificAssignedWorkload += getWorkloadOfPatientByShift(p, s.getShiftNo());
					}
				}

				// check if workload of nurse `n` was exceeded for this shift number
				if (nurseMaximumWorkload < nurseSpecificAssignedWorkload) {
//					System.out.println("Excessive workload " + (nurseSpecificAssignedWorkload - nurseMaximumWorkload)
//							+ " for nurse " + n.getName() + " in shift " + i);
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
		Objects.requireNonNull(model, "Given hospital model was null.");

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
		Objects.requireNonNull(model, "Given hospital model was null.");

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
		Objects.requireNonNull(model, "Given hospital model was null.");

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
		Objects.requireNonNull(model, "Given hospital model was null.");

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
		Objects.requireNonNull(model, "Given hospital model was null.");
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
		return s / 3;
	}

	/**
	 * Converts a given shift object to the day number.
	 * 
	 * @param s Shift object.
	 * @return Day number.
	 */
	protected int shiftToDay(final Shift s) {
		Objects.requireNonNull(s, "Given shift was null.");
		return shiftToDay(s.getShiftNo());
	}

	/**
	 * Returns the maximum load of a nurse `n` in shift `s`.
	 * 
	 * @param n Nurse.
	 * @param s Shift.
	 * @return Maximum load of nurse `n` in shift `s`.
	 */
	protected int findNurseMaxLoadInShift(final Nurse n, final int s) {
		Objects.requireNonNull(n, "Given nurse was null.");

		int maxLoad = 0;

		for (final Roster r : n.getRosters()) {
			if (r.getShiftNo() == s) {
				maxLoad = r.getMaxWorkload();
				break;
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
	protected boolean patientInRoomOnDay(final Patient p, final Room r, final int d) {
		Objects.requireNonNull(p, "Given patient was null.");
		Objects.requireNonNull(r, "Given room was null.");

		// patient must have an assigned room
		if (p.getFirstWorkload().getDerivedShift() != null) {
			// room must match
			if (p.getFirstWorkload().getDerivedShift().getRoom().equals(r)) {
				// day must lay within the time frame of the patient's stay
				final int firstDayNo = shiftToDay(p.getFirstWorkload().getDerivedShift());
				final int stayLength = p.getStayLength();

				if (d >= firstDayNo && d < firstDayNo + stayLength) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Calculates the (possible) cost of one nurse `nurse` for patient `patient` on
	 * shift `shift`.
	 * 
	 * @param nurse   Nurse.
	 * @param patient Patient.
	 * @param shift   Shift.
	 * @return Cost of the nurse regarding the patient on the given shift.
	 */
	protected int calculateSkillLevelCostPerNursePatientShift(final Nurse nurse, final Patient patient, final int shift) {
		Objects.requireNonNull(nurse, "Given nurse was null.");
		Objects.requireNonNull(patient, "Given patient was null.");
		Objects.requireNonNull(shift, "Given shift was null.");

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
	protected List<Patient> getPatientsInRoomOnDay(final Root model, final Room room, final int day) {
		Objects.requireNonNull(model, "Given hospital model was null.");
		Objects.requireNonNull(room, "Given room was null.");

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
	 * Returns the specific workload of the given patient `p` on shift `shiftNo`.
	 * 
	 * @param p       Patient.
	 * @param shiftNo Shift number.
	 * @return Specific workload of the given patient `p` on shift `shiftNo`.
	 */
	protected int getWorkloadOfPatientByShift(final Patient p, final int shiftNo) {
		Objects.requireNonNull(p, "Given patient was null.");
		Objects.requireNonNull(p.getFirstWorkload(), "Patient's first workload was null.");

		if (p.getFirstWorkload().getDerivedShift() == null) {
			return 0;
		}

		Workload w = p.getFirstWorkload();
		while (w != null) {
			// If the derived shift is `null`, the model is either not valid or the
			// respective workload of the patient is outside of the current time frame
			// (which is okay).
			if (w.getDerivedShift() != null) {
				if (w.getDerivedShift().getShiftNo() == shiftNo) {
					return w.getWorkloadValue();
				}
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
	protected int countPatientsNurses(final Root model, final Patient patient) {
		Objects.requireNonNull(model, "Given hospital model was null.");
		Objects.requireNonNull(patient, "Given patient was null.");

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
