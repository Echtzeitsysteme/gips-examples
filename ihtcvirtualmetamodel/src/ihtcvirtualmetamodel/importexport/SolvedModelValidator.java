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
import ihtcvirtualmetamodel.Nurse;
import ihtcvirtualmetamodel.OT;
import ihtcvirtualmetamodel.OpTime;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.Shift;
import ihtcvirtualmetamodel.Surgeon;
import ihtcvirtualmetamodel.VirtualOpTimeToCapacity;
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
		// If path contains at least one slash `/`, create the folder if not existent
		if (debugOutputPath.contains("/")) {
			final int lastSlashIndex = debugOutputPath.lastIndexOf("/");
			FileUtils.prepareFolder(debugOutputPath.substring(0, lastSlashIndex));
		}
		
		for (final Patient p : this.model.getPatients()) {
			if (p.isIsOccupant()) {
				validateOccupant(p);
			}else {
				validatePatient(p);
			}
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
		if(verbose) {
			logger.info("Write debugfile to " + debugOutputPath);
			FileUtils.writeFile(debugOutputPath, debug);
		}
	}

	private void validatePatient(final Patient patient) {
		Objects.requireNonNull(patient);
		String patientDebug = "";
		Shift admissionShift = null;
		
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
		
		if(debug.length() > 0) {
			debug += "\n";
		}
		debug += "Patient " +  patient.getName() + ": \n\t";
		
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
			
			debug += "Was assigned to room " + admissionShift.getRoom().getName() + " on shift " + admissionShift.getShiftNo() + ". \n\t"; 
			debug += "The operation by surgeon " + selectedOpTime.getSurgeon().getName() + " is scheduled in OT " + scheduledOt.getName() + " on day " + selectedvwc.getCapacity().getDay() + ". \n";
			
		}else {
			debug += "Was not scheduled. \n";
		}
		
	}
	
	private void validateOccupant(final Patient occupant) {
		Objects.requireNonNull(occupant);
		String patientDebug = "";
		VirtualShiftToWorkload selectedvsw = checkAllWorkloads(occupant);
		Shift admissionShift = null;

		if(debug.length() > 0) {
			debug += "\n";
		}
		debug += "Occupant " +  occupant.getName() + ": \n\t";
		
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

	private void validateNurse(final Nurse nurse) {
		// TODO
		
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
					ots.add(v.getCapacity().getOt());
				}
			}
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
		
		debug += "\nOT " +  ot.getName() + ": \n\t";
		
		final Collection<Capacity> capacity = ot.getCapacities();
		for(Capacity c : capacity) {
			day = c.getDay();
			surgeryTime = 0;
			
			final Collection<VirtualWorkloadToCapacity> vwc = c.getVirtualWorkload();
			for(VirtualWorkloadToCapacity v : vwc) {
				if(v.isIsSelected()) {
					surgeryTime += v.getWorkload().getPatient().getSurgeryDuration();
				}
			}
			debug += "Day " + day + ": \n\t\t";
			
			if(c.getMaxCapacity() < surgeryTime) {
				otDebug = "Available capacity is exceeded on day " + day + " by " + (surgeryTime - c.getMaxCapacity());
				logger.warning(otDebug); 
				debug += "ERROR: " + otDebug + "\n\t\t";
			}
			
			debug += "Available capacity = " + c.getMaxCapacity() + ", Scheduled operations = " + surgeryTime + "\n\t";
		}
		
	}

	private VirtualShiftToWorkload checkAllWorkloads(final Patient patientOrOccupant) {
		final Collection<Workload> workloads = patientOrOccupant.getWorkloads();
		int assignedShifts;
		int workloadNumber = 0;
		String patientDebug = "";
		VirtualShiftToWorkload virtualAdmissionShift = null;
		Shift currentShift = null;
		
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
				patientDebug = "For Workload " + workloadNumber + " there are " + assignedShifts + "virtual Shifts assigned!";
				logger.warning(patientDebug); 
				debug += "ERROR: " + patientDebug + "\n\t";
			}
			workloadNumber++;
			currentShift = null;
		}
	return virtualAdmissionShift;
	}
}
