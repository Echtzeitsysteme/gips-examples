package teachingassistant.uni.metamodel.generator;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.resource.Resource;

import metamodel.SessionOccurrence;
import metamodel.TaAllocation;
import metamodel.TeachingAssistant;
import metamodel.TeachingSession;
import metamodel.TimeTableEntry;
import metamodel.Week;
import teachingassistant.uni.metamodel.export.FileUtils;
import teachingassistant.uni.utils.LoggingUtils;

public class TeachingAssistantUniManipulator {

	/**
	 * Logger for system outputs.
	 */
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	final String modelFilePath;
	final Random rand;

	public TeachingAssistantUniManipulator(final String modelFilePath) {
		Objects.requireNonNull(modelFilePath);
		LoggingUtils.configureLogging(logger);
		this.modelFilePath = modelFilePath;
		this.rand = new Random(0);
	}

	public static void main(final String[] args) {
		Objects.requireNonNull(args);
		if (args.length < 1) {
			throw new IllegalArgumentException("Missing argument modelFilePath.");
		}
		new TeachingAssistantUniManipulator(args[0]).executeBlocking();
	}

	public void executeBlocking() {
		Objects.requireNonNull(modelFilePath);

		// Load model
		final TaAllocation model = loadModel(modelFilePath);

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
		final TaAllocation model = loadModel(modelFilePath);

		// Alter model
		reduceOneTasWeeklyWorkTime(model, newWeeklyHourLimit);

		// Persist model
		writeModel(modelFilePath, model);
	}

	private TaAllocation loadModel(final String modelFilePath) {
		Objects.requireNonNull(modelFilePath);
		final Resource r = FileUtils.loadModel(modelFilePath);
		Objects.requireNonNull(r);
		final TaAllocation model = (TaAllocation) r.getContents().get(0);
		Objects.requireNonNull(model);
		return model;
	}

	private void writeModel(final String outputFilePath, final TaAllocation model) {
		Objects.requireNonNull(outputFilePath);
		Objects.requireNonNull(model);
		try {
			FileUtils.save(model, outputFilePath);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void blockOneTa(final TaAllocation model) {
		Objects.requireNonNull(model);
		final metamodel.Module module = model.getModules().get(0);
		Objects.requireNonNull(module);
		final TeachingSession session = module.getSessions().get(0);
		Objects.requireNonNull(session);
		final SessionOccurrence occ = session.getOccurrences().get(0);
		Objects.requireNonNull(occ);
		final TeachingAssistant ta = occ.getTas().get(0);
		Objects.requireNonNull(ta);
//		ta.getUnavailable_because_lessons().addAll(session.getEntries());
		// Use one specific entry that matches the occurrence.
		// Find matching `TimeTableEntry` for the selected `occurrence`
		final Set<TimeTableEntry> foundEntries = new HashSet<TimeTableEntry>();
		for (final TimeTableEntry entry : session.getEntries()) {
			for (final Week week : entry.getTimeTableWeeks()) {
				if (week.getId() == occ.getTimeTableWeek()) {
					foundEntries.add(entry);
					break;
				}
			}
		}

		logger.info("Number of matched entries: " + foundEntries.size());
		foundEntries.forEach(entry -> {
			ta.getUnavailable().add(SimpleTaUniGenerator.convertEntryToBlockedTimeSlot(entry));
		});

		// TODO: Future improvement: make sure occurrence is not in the past.
	}

	private void reduceOneTasWeeklyWorkTime(final TaAllocation model, final int newWeeklyHourLimit) {
		Objects.requireNonNull(model);

		if (newWeeklyHourLimit < 0) {
			throw new IllegalArgumentException("Given new weekly hour limit was negative.");
		}

		for (int i = 0; i < model.getModules().size(); i++) {
			final metamodel.Module module = model.getModules().get(i);
			Objects.requireNonNull(module);

			for (int j = 0; j < module.getSessions().size(); j++) {
				final TeachingSession session = module.getSessions().get(j);
				Objects.requireNonNull(session);

				for (int k = 0; k < session.getOccurrences().size(); k++) {
					final SessionOccurrence occ = session.getOccurrences().get(k);
					Objects.requireNonNull(occ);

					for (int l = 0; l < occ.getTas().size(); l++) {
						final TeachingAssistant ta = occ.getTas().get(l);
						Objects.requireNonNull(ta);

						if (session.getHoursPaidPerOccurrence() > newWeeklyHourLimit) {
							ta.setMaxHoursPerWeek(newWeeklyHourLimit);
							return;
						}
					}
				}
			}
		}

		throw new UnsupportedOperationException(
				"There was no possible conflict I could produce with the given new weekly hour limit: "
						+ newWeeklyHourLimit);
	}

}
