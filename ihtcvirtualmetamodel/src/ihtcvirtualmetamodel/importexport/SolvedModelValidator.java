package ihtcvirtualmetamodel.importexport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import ihtcvirtualmetamodel.Capacity;
import ihtcvirtualmetamodel.Gender;
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
import ihtcvirtualmetamodel.utils.FileUtils;

public class SolvedModelValidator {

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(ModelToJsonExporter.class.getName());

	/**
	 * Hospital model to work with.
	 */
	private Root model = null;
	
	public boolean verbose = false;
	
	public String debug;
	
	int roomSkillLevel = 0;
	
	int roomAgeMix = 0;
	
	int continuityOfCare = 0;
	
	int excessiveNurseWorkload = 0;
	
	int openOperatingTheater = 0;
	
	int surgeonTransfer = 0;
	
	int patientDelay = 0;
	
	int electiveUnscheduledPatients = 0;
	
	public SolvedModelValidator(final Root model, boolean verbose) {
		Objects.requireNonNull(model, "Given model was null.");
		this.model = model;
		this.verbose = verbose;
		this.debug = "";
		
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
	
	
	public void validate(final String debugOutputPath) {
		int sum = 0;
		// If path contains at least one slash `/`, create the folder if not existent
		if (debugOutputPath.contains("/")) {
			final int lastSlashIndex = debugOutputPath.lastIndexOf("/");
			FileUtils.prepareFolder(debugOutputPath.substring(0, lastSlashIndex));
		}
		
		// TODO: For Patients consecutive care 
		for (final Patient p : this.model.getPatients()) {
			if (p.isIsOccupant()) {
				validateOccupant(p);
			}else {
				validatePatient(p);
			}
		}
		
		for(final Room r : this.model.getRooms()) {
			validateRoom(r);
		}
		
		for (final Surgeon s : this.model.getSurgeons()) {
			validateSurgeon(s);
		}
		
		for (final OT ot : this.model.getOts()) {
			validateOt(ot);
		}

		for (final Nurse n : this.model.getNurses()) {
			validateNurse(n);
		}
		
		debug += "\nCOSTS (weight X cost):";
		debug += "\nRoomAgeMix........................." + this.model.getWeight().getRoomMixedAge() * roomAgeMix + " (" + this.model.getWeight().getRoomMixedAge() + " X " + roomAgeMix + ")";
		debug += "\nRoomSkillLevel....................." + this.model.getWeight().getRoomNurseSkill() * roomSkillLevel + " (" + this.model.getWeight().getRoomNurseSkill() + " X " + roomSkillLevel + ")";
		debug += "\nContinuityOfCare..................." + this.model.getWeight().getContinuityOfCare() * continuityOfCare + " (" + this.model.getWeight().getContinuityOfCare() + " X " + continuityOfCare + ")";
		debug += "\nExcessiveNurseWorkload............." + this.model.getWeight().getNurseExcessiveWorkload() * excessiveNurseWorkload + " (" + this.model.getWeight().getNurseExcessiveWorkload() + " X " + excessiveNurseWorkload + ")";
		debug += "\nOpenOperatingTheater..............." + this.model.getWeight().getOpenOperatingTheater() * openOperatingTheater + " (" + this.model.getWeight().getOpenOperatingTheater() + " X " + openOperatingTheater + ")";
		debug += "\nSurgeonTransfer...................." + this.model.getWeight().getSurgeonTransfer() * surgeonTransfer + " (" + this.model.getWeight().getSurgeonTransfer() + " X " + surgeonTransfer + ")";
		debug += "\nPatientDelay......................." + this.model.getWeight().getPatientDelay() * patientDelay + " (" + this.model.getWeight().getPatientDelay() + " X " + patientDelay + ")";
		debug += "\nElectiveUnscheduledPatients........" + this.model.getWeight().getUnscheduledOptional() * electiveUnscheduledPatients + " (" + this.model.getWeight().getPatientDelay() + " X " + electiveUnscheduledPatients + ")";
		sum = this.model.getWeight().getRoomMixedAge() * roomAgeMix + this.model.getWeight().getRoomNurseSkill() * roomSkillLevel + this.model.getWeight().getContinuityOfCare() * continuityOfCare + this.model.getWeight().getNurseExcessiveWorkload() * excessiveNurseWorkload + this.model.getWeight().getOpenOperatingTheater() * openOperatingTheater + this.model.getWeight().getSurgeonTransfer() * surgeonTransfer + this.model.getWeight().getPatientDelay() * patientDelay + this.model.getWeight().getUnscheduledOptional() * electiveUnscheduledPatients; 
		debug += "\nCosts: " + sum;
		
		if(verbose) {
			logger.info("Write debugfile to " + debugOutputPath);
			FileUtils.writeFile(debugOutputPath, debug);
		}
	}

	private void validatePatient(final Patient patient) {
		Objects.requireNonNull(patient);
		String patientDebug = "";
		Shift admissionShift = null;
		
		if(debug.length() > 0) {
			debug += "\n";
		}
		String mandatory = patient.isMandatory() ? "mandatory" : "optional";
		debug += "Patient " +  patient.getName() + ": " + mandatory + ", age group = " + patient.getAgeGroup() + ", Gender = " + patient.getGender() +"\n\t";
		
		// Find the selected admission shift and checks all assigned shifts
		VirtualShiftToWorkload selectedvsw = checkAllWorkloads(patient);

		if(selectedvsw != null) {
			admissionShift = selectedvsw.getShift();
		}

		// Find the selected OT
		final Collection<VirtualWorkloadToCapacity> possibleOtAssignments = patient.getFirstWorkload().getVirtualCapacity();
		OT scheduledOt = null;
		VirtualWorkloadToCapacity selectedvwc = null;
		for (final VirtualWorkloadToCapacity v : possibleOtAssignments) {
			if (v.isIsSelected()) {
				scheduledOt = v.getCapacity().getOt();
				selectedvwc = v;
				break;
			}
		}
		
		// Find the selected OpTime
		final Collection<VirtualWorkloadToOpTime> possibleOpTimeAssignments = patient.getFirstWorkload().getVirtualOpTime();
		OpTime selectedOpTime = null;
		VirtualWorkloadToOpTime selectedvwop = null;
		VirtualOpTimeToCapacity selectedvopc = null;
		for (final VirtualWorkloadToOpTime v : possibleOpTimeAssignments) {
			if (v.isIsSelected()) {
				selectedOpTime = v.getOpTime();
				selectedvwop = v;
				final Collection<VirtualOpTimeToCapacity> possibleSurgeonOTAssignments = selectedOpTime.getVirtualCapacity();
				for (final VirtualOpTimeToCapacity vopc : possibleSurgeonOTAssignments) {
					if (vopc.isIsSelected()) {
						selectedvopc = vopc;
						break;
					}
				}
				break;
			}
		}
		
		// Checks if assignments for a patient are viable
		if(admissionShift != null) {
			Objects.requireNonNull(selectedvsw);
			// If the patient has an admission shift, there has to be at least one non-null Node of the following types
			if(selectedvwc == null) {
				patientDebug = "Was assigned an admissionshift but no OT!";
				logger.warning(patientDebug);
				debug += "ERROR: " + patientDebug + "\n\t";
			}
			if(selectedvwop == null) {
				patientDebug = "Was assigned an admissionshift but no Surgeon!";
				logger.warning(patientDebug);
				debug += "ERROR: " + patientDebug + "\n\t";
			}
			if(selectedvwop == null) {
				patientDebug = "Was assigned an admissionshift but the Surgeon is not assigned to an OT!";
				logger.warning(patientDebug);
				debug += "ERROR: " + patientDebug + "\n\t";
			}

			// Check required Edges
			if(!selectedvwop.getRequires_virtualOpTimeToCapacity().contains(selectedvopc)) {
				patientDebug = "VirtualWorkloadToOpTime is selected but not enabled by a VirtualOpTimeToCapacity-Object!";
				logger.warning(patientDebug);
				debug += "ERROR: " + patientDebug + "\n\t";
			}
			if(!selectedvwc.getRequires_virtualWorkloadToOpTime().contains(selectedvwop)) {
				patientDebug = "VirtualWorkloadToCapacity is selected but not enabled by a VirtualWorkloadToOpTime-Object!";
				logger.warning(patientDebug);
				debug += "ERROR: " + patientDebug + "\n\t";
			}
			if(!selectedvsw.getRequires_virtualWorkloadToCapacity().contains(selectedvwc)) {
				patientDebug = "VirtualShiftToWorkload is selected but not enabled by a VirtualWorkloadToCapacity-Object!";
				logger.warning(patientDebug);
				debug += "ERROR: " + patientDebug + "\n\t";
			}
			
			// Check correct Assignments 
			if((admissionShift.getShiftNo() / 3 != selectedvwc.getCapacity().getDay()) | 
					(admissionShift.getShiftNo() / 3 != selectedOpTime.getDay()) |
					(selectedvwc.getCapacity().getDay() != selectedOpTime.getDay())) {
				patientDebug = "The selected virtual Nodes are not on the same day! shift: " + admissionShift.getShiftNo() + " capacity.day = " + selectedvwc.getCapacity().getDay() + " opTime.day = " + selectedOpTime.getDay();
				logger.warning(patientDebug);
				debug += "ERROR: " + patientDebug + "\n\t";
			}
			if(patient.getSurgeon() != selectedvwop.getOpTime().getSurgeon()) {
				patientDebug = "Not assigned to the correct surgeon! Expected: " + patient.getSurgeon().getName() + " Assigned: " + selectedvwop.getOpTime().getSurgeon().getName();
				logger.warning(patientDebug); 
				debug += "ERROR: " + patientDebug + "\n\t";
			}
			
			checkSurgeryAssignment(patient, debug);
			
			debug += "Was assigned to room " + admissionShift.getRoom().getName() + " on shift " + admissionShift.getShiftNo() + ". \n\t"; 
			debug += "The operation by surgeon " + selectedOpTime.getSurgeon().getName() + " is scheduled in OT " + scheduledOt.getName() + " on day " + selectedvwc.getCapacity().getDay() + ". \n";
			
			this.patientDelay += (admissionShift.getShiftNo() / 3) - patient.getEarliestDay();
		}else {
			debug += "Was not scheduled. \n";
			this.electiveUnscheduledPatients++;
		}
		
	}
	
	private void validateOccupant(final Patient occupant) {
		Objects.requireNonNull(occupant);
		String patientDebug = "";
		Shift admissionShift = null;

		if(debug.length() > 0) {
			debug += "\n";
		}
		debug += "Occupant " +  occupant.getName() + ": \n\t";
		
		VirtualShiftToWorkload selectedvsw = checkAllWorkloads(occupant);
		
		if(selectedvsw == null) {
			patientDebug = "First workload is not assigned to a shift!";
			logger.warning(patientDebug); 
			debug += "ERROR: " + patientDebug + "\n\t";
			return;
		}
		
		admissionShift = selectedvsw.getShift();
		
		if(admissionShift.getShiftNo() != 0) {
			patientDebug = "First workload is not assigned on first day! ";
			logger.warning(patientDebug); 
			debug += "ERROR: " + patientDebug + "\n\t";
			return;
		}
		
		debug += "First shift is assigned correctly. \n";
		
	}
	
	private void validateRoom(Room room) {
		Objects.requireNonNull(room);
		String roomDebug = "";
		int day = 0;
		List<Patient> patients = new ArrayList<>();
		String firstFoundGender = "";
		int minAge = 0;
		int maxAge = 0;
		int ageMix = 0;
		
		debug += "\nRoom " +  room.getName() + ": capacity = " + room.getBeds() + "\n\t";
		
		final Collection<Shift> shifts = room.getShifts();
		for(Shift shift : shifts) {
			if(shift.getShiftNo() % 3 != 0) {
				continue;
			}	
			patients.clear();
			day = shift.getShiftNo() / 3;
			firstFoundGender = "";
			minAge = 1000;
			maxAge = 0;
			
			final Collection<VirtualShiftToWorkload> possibleWorkloads = shift.getVirtualWorkload();
			for(VirtualShiftToWorkload v : possibleWorkloads) {
				if(v.isIsSelected()) {
					if(firstFoundGender.length() == 0) {
						firstFoundGender = v.getWorkload().getPatient().getGender();
						debug += "Day " +  day + ": " + "\n\t\t";
					}else if(!firstFoundGender.equals(v.getWorkload().getPatient().getGender())) {
						roomDebug = "Gendermix in room " + room.getName() + "!";
						logger.warning(roomDebug); 
						debug += "ERROR: " + roomDebug + "\n\t\t";
					}
					patients.add(v.getWorkload().getPatient());
					if(v.getWorkload().getPatient().getAgeGroup() > maxAge) {
						maxAge = v.getWorkload().getPatient().getAgeGroup();
					} 
					if(v.getWorkload().getPatient().getAgeGroup() < minAge) {
						minAge = v.getWorkload().getPatient().getAgeGroup();
					}
				}	
			}
			
			if(patients.size() > room.getBeds()) {
				roomDebug = "The capacity of room " + room.getName() + " on day " + day + " is exceeded!";
				logger.warning(roomDebug); 
				debug += "ERROR: " + roomDebug + "\n\t\t";
			}
			
			if(patients.size() > 0) {
				int index = 1;
				for(Patient p : patients) {
					debug += "Patient " + p.getName() + " with age group " + p.getAgeGroup() + " and gender " + p.getGender() + "\n\t";
					if(index < patients.size()) {
						debug += "\t";
					}
					index++; 
				}
			}	
			ageMix += Math.max(0, maxAge - minAge);
		}
		this.roomAgeMix += ageMix;
	}

	private void validateNurse(final Nurse nurse) {
		Objects.requireNonNull(nurse);
		String nurseDebug = "";
		String tmpDebug = "";
		int assignedWorkload = 0;
		int penalizedSkillDiff = 0;
		List<Workload> workloads = new ArrayList<>();
		
		debug += "\nNurse " +  nurse.getName() + ": Skill level " + nurse.getSkillLevel() + "\n\t";
		
		final Collection<Roster> rosters = nurse.getRosters();
		for(Roster ro : rosters) {
			assignedWorkload = 0;
			tmpDebug = "";
			
			final Collection<VirtualShiftToRoster> possibleShifts = ro.getVirtualShift();
			for(VirtualShiftToRoster vsr : possibleShifts) {
				workloads.clear();
				
				if(vsr.isIsSelected()) {
					final Collection<VirtualShiftToWorkload> possibleWorkloads = vsr.getShift().getVirtualWorkload();
					for(VirtualShiftToWorkload vsw : possibleWorkloads) {
						if(vsw.isIsSelected()) {
							workloads.add(vsw.getWorkload());
							assignedWorkload += vsw.getWorkload().getWorkloadValue();
						}
					}
					tmpDebug += "\tRoom " + vsr.getShift().getRoom().getName() + ": \n\t\t\t";
					int count = 1;
					for(Workload w : workloads) {
						penalizedSkillDiff += Math.max(0, w.getMinNurseSkill() - nurse.getSkillLevel());
						tmpDebug += "Patient " + w.getPatient().getName() + ": workload = " + w.getWorkloadValue() + ", min skill = " + w.getMinNurseSkill() + "\n\t";
						if(count < workloads.size()) {
							tmpDebug += "\t\t";
						}
						count++;	
					}
				}
			}
			if(assignedWorkload > 0) {
				debug += "Roster " + ro.getShiftNo() + ": Maximum workload = " +  ro.getMaxWorkload() + " Assigned workload = " + assignedWorkload +  "\n\t";
				debug += tmpDebug;
				this.excessiveNurseWorkload += Math.max(0, assignedWorkload - ro.getMaxWorkload());
			}
		}
		debug += "=> Skill difference = " + penalizedSkillDiff + "\n"; 
		this.roomSkillLevel += penalizedSkillDiff;
		
	}

	private void validateSurgeon(final Surgeon surgeon) {
		Objects.requireNonNull(surgeon);
		String surgeonDebug = "";
		String otsAsString = "";
		String patientsAsString = "";
		List<OT> ots = new ArrayList<>(); 
		List<Patient> patients = new ArrayList<>();
		int day = 0;
		int surgeryTime = 0;
		
		
		debug += "\nSurgeon " +  surgeon.getName() + ": \n\t";
		
		final Collection<OpTime> opTimes = surgeon.getOpTimes();
		for(OpTime op : opTimes) {
			day = op.getDay();
			ots.clear();
			patients.clear();
			otsAsString = "";
			patientsAsString = "";
			surgeryTime = 0;
			
			final Collection<VirtualOpTimeToCapacity> possibleCapacitys = op.getVirtualCapacity();
			for(VirtualOpTimeToCapacity v : possibleCapacitys) {
				if(v.isIsSelected()) {
					if(!ots.contains(v.getCapacity().getOt())) {
						ots.add(v.getCapacity().getOt());
					}
				}
			}

			this.surgeonTransfer += Math.max(0, ots.size() - 1);
			
			for(OT ot : ots) {
				if(otsAsString.length() > 0) {
					otsAsString += ", " + ot.getName();
				}else {
					otsAsString += ot.getName();
				}
			}
			
			final Collection<VirtualWorkloadToOpTime> possibleWorkloads = op.getVirtualWorkload();
			for(VirtualWorkloadToOpTime v : possibleWorkloads) {
				if(v.isIsSelected()) {
					patients.add(v.getWorkload().getPatient());
					surgeryTime += patients.getLast().getSurgeryDuration();
				}
			}
			for(Patient patient : patients) {
				if(patientsAsString.length() > 0) {
					patientsAsString += ", " + patient.getName();
				}else {
					patientsAsString += patient.getName();
				}
			}
			debug += "Day " + day + ": \n\t\t";
			if(op.getMaxOpTime() < surgeryTime) {
				surgeonDebug = "Available operation time is exceeded on day " + day + " by " + (surgeryTime - op.getMaxOpTime());
				logger.warning(surgeonDebug); 
				debug += "ERROR: " + surgeonDebug + "\n\t\t";
			}
			
			if(patientsAsString.length() > 0) {
				debug += "Operates " + patientsAsString + " in " + otsAsString + "\n\t\t";
			}			
			debug += "Available operation time = " + op.getMaxOpTime() + ", Scheduled operation time = " + surgeryTime + "\n\t";

		}
		
	}
	
	private void validateOt(OT ot) {
		Objects.requireNonNull(ot);
		int day = 0;
		int surgeryTime = 0;
		String otDebug = "";
		boolean otOpen;
		
		debug += "\nOT " +  ot.getName() + ": \n\t";
		
		final Collection<Capacity> capacity = ot.getCapacities();
		for(Capacity c : capacity) {
			day = c.getDay();
			surgeryTime = 0;
			otOpen = false;
			
			final Collection<VirtualWorkloadToCapacity> vwc = c.getVirtualWorkload();
			for(VirtualWorkloadToCapacity v : vwc) {
				if(v.isIsSelected()) {
					surgeryTime += v.getWorkload().getPatient().getSurgeryDuration();
					otOpen = true;
				}
			}
			if(otOpen) {
				this.openOperatingTheater++;
				debug += "Day " + day + ": \n\t\t";
				
				if(c.getMaxCapacity() < surgeryTime) {
					otDebug = "Available capacity is exceeded on day " + day + " by " + (surgeryTime - c.getMaxCapacity());
					logger.warning(otDebug); 
					debug += "ERROR: " + otDebug + "\n\t\t";
				}
				
				debug += "Available capacity = " + c.getMaxCapacity() + ", Scheduled operations = " + surgeryTime + "\n\t";
			}		
		}
	}

	private VirtualShiftToWorkload checkAllWorkloads(final Patient patientOrOccupant) {
		final Collection<Workload> workloads = patientOrOccupant.getWorkloads();
		int assignedShifts;
		int workloadNumber = 0;
		String patientDebug = "";
		VirtualShiftToWorkload virtualAdmissionShift = null;
		Shift currentShift = null;
		List<Nurse> nurses = new ArrayList<>();
		
		for(Workload w : workloads) {
			assignedShifts = 0;
			
			final Collection<VirtualShiftToWorkload> possibleShiftAssignments = w.getVirtualShift();
			for (final VirtualShiftToWorkload v : possibleShiftAssignments) {
				if (v.isIsSelected()) {
					assignedShifts++;
					if(workloadNumber == 0) {
						virtualAdmissionShift = v;
					}
					currentShift = v.getShift();
				}
			}
			
			// The patient was not assigned 
			if(virtualAdmissionShift == null) {
				return virtualAdmissionShift; 
			}
			
			// Every workload must be assigned to a shift
			if(currentShift == null) {
				if(virtualAdmissionShift.getShift().getShiftNo() + workloadNumber >= model.getPeriod() * 3) {
					// If the workload is outside of the scheduling period no shift can be assigned
					break;
				}
				patientDebug = "For Workload " + workloadNumber + " there is no virtual Shift assigned!";
				logger.warning(patientDebug); 
				debug += "ERROR: " + patientDebug + "\n\t";
				return virtualAdmissionShift;
			}
			
			// The workloads have to be assigned to consecutive shifts
			if(virtualAdmissionShift.getShift().getShiftNo() + workloadNumber != currentShift.getShiftNo()) {
				patientDebug = "For Workload " + workloadNumber + " the assigned shift is not correct! Expected shiftnumber: " + virtualAdmissionShift.getShift().getShiftNo() + workloadNumber + "Assigned shiftnumber: " + currentShift.getShiftNo();
				logger.warning(patientDebug); 
				debug += "ERROR: " + patientDebug + "\n\t";
			}
			
			if(assignedShifts > 1) {
				patientDebug = "For Workload " + workloadNumber + " there are " + assignedShifts + " virtual Shifts assigned!";
				logger.warning(patientDebug); 
				debug += "ERROR: " + patientDebug + "\n\t";
			}
			
			final Collection<VirtualShiftToRoster> possibleRosterAssignments = currentShift.getVirtualRoster();
			for(VirtualShiftToRoster vsr : possibleRosterAssignments) {
				if(vsr.isIsSelected() && !nurses.contains(vsr.getRoster().getNurse())) {
					nurses.add(vsr.getRoster().getNurse());
				}
			}
			
			workloadNumber++;
			currentShift = null;
		}
	this.continuityOfCare += nurses.size();
	return virtualAdmissionShift;
	}
	
	
	private String checkSurgeryAssignment(final Patient patient, String debug){
		final Collection<VirtualWorkloadToCapacity> possibleOTAssignments = patient.getFirstWorkload().getVirtualCapacity();
		VirtualWorkloadToCapacity v = null;
		boolean valid = false;
		for(VirtualWorkloadToCapacity vwc : possibleOTAssignments) {
			if(vwc.isIsSelected()) {
				v = vwc;
				break;
			}
		}
		if(v != null) {
			final Collection<VirtualShiftToWorkload> possibleShiftAssignments = patient.getFirstWorkload().getVirtualShift();
			for(VirtualShiftToWorkload vws : possibleShiftAssignments) {
				if(vws.isIsSelected()) {
					valid = true;
					break;
				}
			}
			if(!valid) {
				String patientDebug = "VirtualWorkloadToCapacity is selected but no VirtualShiftToWorkload Assignment is made!";
				logger.warning(patientDebug);
				debug += "ERROR: " + patientDebug + "\n\t";
			}
		}
		return debug;
	}
}
