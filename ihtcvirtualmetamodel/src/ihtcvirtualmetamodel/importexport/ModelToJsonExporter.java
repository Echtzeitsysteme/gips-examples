package ihtcvirtualmetamodel.importexport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ihtcvirtualmetamodel.Nurse;
import ihtcvirtualmetamodel.Patient;
import ihtcvirtualmetamodel.Root;
import ihtcvirtualmetamodel.Roster;
import ihtcvirtualmetamodel.Shift;
import ihtcvirtualmetamodel.utils.FileUtils;
import ihtcvirtualmetamodel.utils.ModelCostCalculator;

/**
 * This model exporter can be used to convert an EMF model to the respective
 * JSON output format required by the competition.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class ModelToJsonExporter {

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(ModelToJsonExporter.class.getName());

	/**
	 * Hospital model to work with.
	 */
	private Root model = null;

	/**
	 * Initializes a new model to JSON exporter object with a given Hospital model.
	 * 
	 * @param model Hospital model.
	 */
	public ModelToJsonExporter(final Root model) {
		Objects.requireNonNull(model, "Given model was null.");

		this.model = model;

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
	 * Converts the contained model to a JSON output file written to the given
	 * output path.
	 * 
	 * @param outputPath Output path to write the JSON output file to.
	 */
	public void modelToJson(final String outputPath) {
		Objects.requireNonNull(outputPath);
		modelToJson(outputPath, false);
	}

	/**
	 * Converts the contained model to a JSON output file written to the given
	 * output path.
	 * 
	 * @param outputPath Output path to write the JSON output file to.
	 * @param verbose    If true, the exporter will print more information about the
	 *                   model.
	 */
	public void modelToJson(final String outputPath, final boolean verbose) {
		Objects.requireNonNull(outputPath);
		if (outputPath.isBlank()) {
			throw new IllegalArgumentException("Given path <" + outputPath + "> or blank.");
		}

		// If path contains at least one slash `/`, create the folder if not existent
		if (outputPath.contains("/")) {
			final int lastSlashIndex = outputPath.lastIndexOf("/");
			FileUtils.prepareFolder(outputPath.substring(0, lastSlashIndex));
		}

		final JsonArray patientsJson = new JsonArray();
		for (final Patient p : this.model.getPatients()) {
			if (p.isIsOccupant()) {
				continue;
			}
			patientsJson.add(convertPatientToJson(p, verbose));
		}

		final JsonArray nursesJson = new JsonArray();
		for (final Nurse n : this.model.getNurses()) {
			nursesJson.add(convertNurseToJson(n));
		}

		final JsonArray costsJson = convertModelToCostsJson(this.model, verbose);

		// Global JSON object
		final JsonObject json = new JsonObject();
		json.add("patients", patientsJson);
		json.add("nurses", nursesJson);
		json.add("costs", costsJson);

		// Write to output JSON file
		FileUtils.writeFileFromJson(outputPath, json, true);
	}

	/**
	 * Converts a given patient to a JSON object.
	 * 
	 * @param patient Patient.
	 * @return JSON object.
	 */
	protected JsonObject convertPatientToJson(final Patient patient, final boolean verbose) {
		Objects.requireNonNull(patient);

		final JsonObject patientJson = new JsonObject();
		patientJson.addProperty("id", patient.getName());

		// If patient was scheduled
		if (patient.getFirstWorkload().getDerivedShift() != null) {
			patientJson.addProperty("admission_day",
					convertShiftToDay(patient.getFirstWorkload().getDerivedShift().getShiftNo()));
			if (patient.getFirstWorkload().getDerivedShift().getRoom() != null) {
				patientJson.addProperty("room", patient.getFirstWorkload().getDerivedShift().getRoom().getName());
			}

			patientJson.addProperty("operating_theater",
					patient.getFirstWorkload().getDerivedCapacity().getOt().getName());
		} else {
			patientJson.addProperty("admission_day", "none");
		}

		return patientJson;
	}

	/**
	 * Converts a given nurse to a JSON object.
	 * 
	 * @param nurse Nurse.
	 * @return JSON object.
	 */
	protected JsonObject convertNurseToJson(final Nurse nurse) {
		Objects.requireNonNull(nurse);

		final JsonObject nurseJson = new JsonObject();
		nurseJson.addProperty("id", nurse.getName());

		final JsonArray assignmentsJson = new JsonArray();
		for (final Roster r : nurse.getRosters()) {
			final Map<Integer, Set<String>> shift2Room = new HashMap<>();
			for (final Shift s : r.getDerivedShifts()) {
				if (!shift2Room.containsKey(s.getShiftNo())) {
					shift2Room.put(s.getShiftNo(), new HashSet<String>());
				}

				shift2Room.get(s.getShiftNo()).add(s.getRoom().getName());
			}

			for (final int shiftId : shift2Room.keySet()) {
				final JsonObject assignment = new JsonObject();
				assignment.addProperty("day", convertShiftToDay(shiftId));
				assignment.addProperty("shift", convertShiftType(shiftId % 3));
				final JsonArray roomsJson = new JsonArray();
				for (final String room : shift2Room.get(shiftId)) {
					roomsJson.add(room);
				}
				assignment.add("rooms", roomsJson);
				assignmentsJson.add(assignment);
			}

		}

		nurseJson.add("assignments", assignmentsJson);

		return nurseJson;
	}

	/**
	 * Converts the overall model costs to a JSON array with the possibility to
	 * print all values on the console.
	 * 
	 * @param model   Complete model to calculate all costs for.
	 * @param verbose If true, the method will print all costs on the console.
	 * @return JSON array.
	 */
	protected JsonArray convertModelToCostsJson(final Root model, final boolean verbose) {
		Objects.requireNonNull(model);

		final ModelCostCalculator calc = new ModelCostCalculator();
		final int unscheduled = calc.calculateUnscheduledPatientsCost(model);
		final int delay = calc.calculateAdmissionDelayCost(model);
		final int openOt = calc.calculateOpenOtCost(model);
		final int ageMix = calc.calculateAgeMixCost(model);
		final int skill = calc.calculateSkillLevelCost(model);
		final int excess = calc.calculateExcessCost(model);
		final int continuity = calc.calculateContinuityCost(model);
		final int surgeonTransfer = calc.calculateSurgeonTransferCost(model);

		// total cost is the sum of all individual costs
		//
		// summing the individual values is faster than re-calculating the complete
		// model within the cost calculator
		final int costs = unscheduled + delay + openOt + ageMix + skill + excess + continuity + surgeonTransfer;

		if (verbose) {
			logger.info("Costs: " + costs);
			logger.info("Unscheduled: " + unscheduled);
			logger.info("Delay: " + delay);
			logger.info("OpenOT: " + openOt);
			logger.info("AgeMix: " + ageMix);
			logger.info("Skill: " + skill);
			logger.info("Excess: " + excess);
			logger.info("Continuity: " + continuity);
			logger.info("SurgeonTransfer: " + surgeonTransfer);
		}

		// Weirdly, the output costs is a JSON array with only one concatenated String
		final StringBuilder sb = new StringBuilder();
		sb.append("Cost: ");
		sb.append(costs);
		sb.append(", Unscheduled: ");
		sb.append(unscheduled);
		sb.append(", Delay: ");
		sb.append(delay);
		sb.append(", OpenOT: ");
		sb.append(openOt);
		sb.append(", AgeMix: ");
		sb.append(ageMix);
		sb.append(", Skill: ");
		sb.append(skill);
		sb.append(", Excess: ");
		sb.append(excess);
		sb.append(", Continuity: ");
		sb.append(continuity);
		sb.append(", SurgeonTransfer: ");
		sb.append(surgeonTransfer);

		final JsonArray costsJson = new JsonArray();
		costsJson.add(sb.toString());
		return costsJson;
	}

	/**
	 * Converts the given shift number to the corresponding day number.
	 * 
	 * @param shift Shift number.
	 * @return Day number.
	 */
	protected int convertShiftToDay(final int shift) {
		// Division of an integer by 3 to get the floored value.
		return shift / 3;
	}

	/**
	 * Converts the given shift type (number representation) to the corresponding
	 * string representation.
	 * 
	 * @param shiftType Shift type represented by an integer.
	 * @return Shift type represented by a string.
	 */
	protected String convertShiftType(final int shiftType) {
		switch (shiftType) {
		case 0: {
			return "early";
		}
		case 1: {
			return "late";
		}
		case 2: {
			return "night";
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + shiftType);
		}
	}

}
