package teachingassistant.kcl.metamodelalt.generator;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import org.eclipse.emf.ecore.resource.Resource;

import metamodel.SessionOccurrence;
import metamodel.TA;
import metamodel.TAAllocation;
import metamodel.TeachingSession;
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
		new TeachingAssistantKclManipulator(args[0]).execute();
	}

	public void execute() {
		Objects.requireNonNull(modelFilePath);

		// Load model
		final Resource r = FileUtils.loadModel(modelFilePath);
		Objects.requireNonNull(r);
		final TAAllocation model = (TAAllocation) r.getContents().get(0);
		Objects.requireNonNull(model);

		// Alter model
		alterModel(model);

		// Persist model
		try {
			FileUtils.save(model, modelFilePath);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void alterModel(final TAAllocation model) {
		Objects.requireNonNull(model);
		final metamodel.Module module = model.getModules().get(0);
		Objects.requireNonNull(module);
		final TeachingSession session = module.getSessions().get(0);
		Objects.requireNonNull(session);
		final SessionOccurrence occ = session.getOccurrences().get(0);
		Objects.requireNonNull(occ);
		final TA ta = occ.getTas().get(0);
		Objects.requireNonNull(ta);
		ta.getUnavailable_because_lessons().addAll(session.getEntries());
	}

}
