package teachingassistant.kcl.metamodelalt.generator;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;

import metamodel.SessionOccurrence;
import metamodel.TA;
import metamodel.TAAllocation;
import metamodel.TeachingSession;
import metamodel.TimeTableEntry;
import metamodel.Week;
import teachingassistant.kcl.metamodelalt.export.FileUtils;

public class TeachingAssistantKclManipulator {

	final String modelFilePath;
	final Random rand;

	public TeachingAssistantKclManipulator(final String modelFilePath) {
		Objects.requireNonNull(modelFilePath);
		this.modelFilePath = modelFilePath;
		this.rand = new Random(0);
	}

	public static void main(final String[] args) {
		Objects.requireNonNull(args);
		if (args.length < 1) {
			throw new IllegalArgumentException("Missing argument modelFilePath.");
		}
		new TeachingAssistantKclManipulator(args[0]).executeBlocking();
	}

	public void executeBlocking() {
		Objects.requireNonNull(modelFilePath);

		// Load model
		final TAAllocation model = loadModel(modelFilePath);

		// Alter model
		blockOneTa(model);

		// Persist model
		writeModel(modelFilePath, model);
	}

	public void executeHourReduction(final int newWeeklyHourLimit) {
		Objects.requireNonNull(modelFilePath);

		if (newWeeklyHourLimit < 0) {
			throw new IllegalArgumentException("Given new weekly hour limit was less than 0.");
		}

		// Load model
		final TAAllocation model = loadModel(modelFilePath);

		// Alter model
		reduceOneTasWeeklyWorkTime(model, newWeeklyHourLimit);

		// Persist model
		writeModel(modelFilePath, model);
	}

	private TAAllocation loadModel(final String modelFilePath) {
		Objects.requireNonNull(modelFilePath);
		final Resource r = FileUtils.loadModel(modelFilePath);
		Objects.requireNonNull(r);
		final TAAllocation model = (TAAllocation) r.getContents().get(0);
		Objects.requireNonNull(model);
		return model;
	}

	private void writeModel(final String outputFilePath, final TAAllocation model) {
		try {
			FileUtils.save(model, outputFilePath);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void blockOneTa(final TAAllocation model) {
		Objects.requireNonNull(model);
		final metamodel.Module module = model.getModules().get(0);
		Objects.requireNonNull(module);
		final TeachingSession session = module.getSessions().get(0);
		Objects.requireNonNull(session);
		final SessionOccurrence occ = session.getOccurrences().get(0);
		Objects.requireNonNull(occ);
		final TA ta = occ.getTas().get(0);
		Objects.requireNonNull(ta);
//		ta.getUnavailable_because_lessons().addAll(session.getEntries());
		// Use one specific entry that matches the occurrence.
		// Find matching `TimeTableEntry` for the selected `occurrence`
		final Set<TimeTableEntry> foundEntries = new HashSet<TimeTableEntry>();
		for (final TimeTableEntry entry : session.getEntries()) {
			for (final Week week : entry.getTimeTableWeeks()) {
				if (week.getNumber() == occ.getTimeTableWeek()) {
					foundEntries.add(entry);
					break;
				}
			}
		}

		System.out.println("Number of matched entries: " + foundEntries.size());
		ta.getUnavailable_because_lessons().addAll(foundEntries);

		// TODO(Max): Make sure occurrence is not in the past.
	}

	private void reduceOneTasWeeklyWorkTime(final TAAllocation model, final int newWeeklyHourLimit) {
		Objects.requireNonNull(model);

		if (newWeeklyHourLimit < 0) {
			throw new IllegalArgumentException("Given new weekly hour limit was negative.");
		}

		final metamodel.Module module = model.getModules().get(0);
		Objects.requireNonNull(module);
		final TeachingSession session = module.getSessions().get(0);
		Objects.requireNonNull(session);
		final SessionOccurrence occ = session.getOccurrences().get(0);
		Objects.requireNonNull(occ);
		final TA ta = occ.getTas().get(0);
		Objects.requireNonNull(ta);

		ta.setMaxHoursPerWeek(newWeeklyHourLimit);
	}

}
