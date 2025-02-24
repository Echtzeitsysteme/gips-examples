package org.emoflon.gips.ihtc.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.gips.core.api.GipsEngineAPI;
import org.emoflon.gips.core.milp.SolverOutput;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import ihtcgipssolution.api.gips.IhtcgipssolutionGipsAPI;
import ihtcgipssolution.api.gips.mapping.AadpMapping;
import ihtcgipssolution.api.gips.mapping.RoomDayLoadMapping;
import ihtcgipssolution.api.gips.mapping.RoomDayPatientLoadMapping;
import ihtcgipssolution.hardonly.api.gips.HardonlyGipsAPI;
import ihtcgipssolution.nursesrooms.api.gips.NursesroomsGipsAPI;
import ihtcgipssolution.patientssurgeonsrooms.api.gips.PatientssurgeonsroomsGipsAPI;
import ihtcgipssolution.softcnstr.optionaldelay.api.gips.OptionaldelayGipsAPI;
import ihtcgipssolution.softcnstr.optionalopenots.api.gips.OptionalopenotsGipsAPI;
import ihtcgipssolution.softcnstr.optionalpatients.api.gips.OptionalpatientsGipsAPI;
import ihtcgipssolution.softcnstrtuning.api.gips.SoftcnstrtuningGipsAPI;
import ihtcmetamodel.Hospital;
import ihtcmetamodel.importexport.JsonToModelLoader;
import ihtcmetamodel.importexport.ModelToJsonExporter;
import ihtcmetamodel.utils.FileUtils;

/**
 * This abstract runner contains utility methods to wrap a given GIPS API object
 * in the context of the IHTC 2024 example.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public abstract class AbstractIhtcGipsRunner {

	/**
	 * The scenario (JSON) file to load.
	 */
	public String scenarioFileName = "test01.json";

	/**
	 * Project folder location.
	 */
	public String projectFolder = System.getProperty("user.dir");

	/**
	 * Data set folder location.
	 */
	public String datasetFolder = projectFolder + "/../ihtcmetamodel/resources/ihtc2024_test_dataset/";

	/**
	 * Default input path.
	 */
	public String inputPath = datasetFolder + scenarioFileName;

	/**
	 * Default instance folder path.
	 */
	public String instanceFolder = projectFolder + "/../ihtcmetamodel/instances/";

	/**
	 * Default instance XMI path.
	 */
	public String instancePath = instanceFolder + scenarioFileName.replace(".json", ".xmi");

	/**
	 * Default instance solved XMI path.
	 */
	public String gipsOutputPath = instanceFolder + scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json"))
			+ "_solved.xmi";

	/**
	 * Default JSON output folder path.
	 */
	public String datasetSolutionFolder = projectFolder + "/../ihtcmetamodel/resources/";

	/**
	 * Default JSON output file path.
	 */
	public String outputPath = datasetSolutionFolder + "sol_"
			+ scenarioFileName.substring(0, scenarioFileName.lastIndexOf(".json")) + "_gips.json";

	/**
	 * Runtime tick.
	 */
	private long tick = 0;

	/**
	 * Runtime tock.
	 */
	private long tock = 0;

	/**
	 * Sets the current system time as tick value. The tock value gets re-set to 0.
	 */
	protected void tick() {
		this.tick = System.nanoTime();
		this.tock = 0;
	}

	/**
	 * Sets the current system time as tock value.
	 */
	protected void tock() {
		this.tock = System.nanoTime();
	}

	/**
	 * Prints the measured wall clock runtime value to System.out if its value is
	 * smaller than 10 minutes and to System.err otherwise.
	 */
	protected void printWallClockRuntime() {
		final double runtime = 1.0 * (tock - tick) / 1_000_000_000;

		if (runtime < 0) {
			throw new IllegalArgumentException("Runtime value was negative.");
		}

		final String runtimeString = String.format("%,4.2f", runtime);

		if (runtime > 600) {
			System.err.println("=> Time limit of 10 minutes violated.");
			System.err.println("=> Wall clock run time: " + runtimeString + "s.");
		} else {
			System.out.println("=> Time limit of 10 minutes respected.");
			System.out.println("=> Wall clock run time: " + runtimeString + "s.");
		}
	}

	/**
	 * Prints all relevant GIPS-related variables for a given IHTC GIPS solution API
	 * object.
	 * 
	 * @param gipsApi IHTC GIPS solution API object to print all relevant variable
	 *                values for.
	 */
	protected void printVariableValues(final IhtcgipssolutionGipsAPI gipsApi) {
		// Load of all rooms on all days
		System.out.println("=> Day, room: load");
		final List<RoomDayLoadMapping> roomDayLoadMappings = new ArrayList<RoomDayLoadMapping>();
		roomDayLoadMappings.addAll(gipsApi.getRoomDayLoad().getMappings().values());
		roomDayLoadMappings.sort(new Comparator<RoomDayLoadMapping>() {
			@Override
			public int compare(RoomDayLoadMapping arg0, RoomDayLoadMapping arg1) {
				if (arg0.getMatch().getD().getId() == arg1.getMatch().getD().getId()) {
					return arg0.getMatch().getR().getName().compareTo(arg1.getMatch().getR().getName());
				}
				return arg0.getMatch().getD().getId() - arg1.getMatch().getD().getId();
			}
		});
		roomDayLoadMappings.forEach(m -> {
			if (m.getValueOfLoad() > 0) {
				System.out.println("Day : " + m.getMatch().getD().getId() + ", room : " + m.getMatch().getR().getName()
						+ ", value : " + m.getValueOfLoad());
			}
		});

		// Specific loads of all patients on days in rooms
		System.out.println("=> Day, room, patient: load");
		final List<RoomDayPatientLoadMapping> roomDayPersonLoadMappings = new ArrayList<RoomDayPatientLoadMapping>();
		roomDayPersonLoadMappings.addAll(gipsApi.getRoomDayPatientLoad().getMappings().values());
		roomDayPersonLoadMappings.sort(new Comparator<RoomDayPatientLoadMapping>() {
			@Override
			public int compare(RoomDayPatientLoadMapping arg0, RoomDayPatientLoadMapping arg1) {
				if (arg0.getMatch().getD().getId() == arg1.getMatch().getD().getId()) {
					if (arg0.getMatch().getR().getName().compareTo(arg1.getMatch().getR().getName()) == 0) {
						return arg0.getMatch().getP().getName().compareTo(arg1.getMatch().getP().getName());
					}
					return arg0.getMatch().getR().getName().compareTo(arg1.getMatch().getR().getName());
				}
				return arg0.getMatch().getD().getId() - arg1.getMatch().getD().getId();
			}
		});
		roomDayPersonLoadMappings.forEach(m -> {
			if (m.getValue() == 1) {
				System.out.println("Day : " + m.getMatch().getD().getId() + ", room : " + m.getMatch().getR().getName()
						+ ", patient : " + m.getMatch().getP().getName() + ", load : " + m.getValue());
			}
		});

		// Patient to room assignment
		System.out.println("=> Patient: room");
		gipsApi.getArp().getMappings().values().forEach(m -> {
			if (m.getValue() == 1) {
				System.out.println(
						"Patient : " + m.getMatch().getP().getName() + ", room : " + m.getMatch().getR().getName());
			}
		});

		// Patient start and leave day
		System.out.println("=> Patient from, to (day)");
		gipsApi.getAadp().getMappings().values().forEach(m -> {
			if (m.getValue() == 1) {
				System.out.println(
						"Patient : " + m.getMatch().getP().getName() + ", from : " + m.getMatch().getD().getId()
								+ ", to " + (m.getMatch().getP().getLengthOfStay() + m.getMatch().getD().getId() - 1));
			}
		});

		// Patient admission days
		final List<AadpMapping> admissionDays = new ArrayList<AadpMapping>();
		admissionDays.addAll(gipsApi.getAadp().getMappings().values());
		admissionDays.sort(new Comparator<AadpMapping>() {
			@Override
			public int compare(final AadpMapping arg0, final AadpMapping arg1) {
				if (arg0.getMatch().getP().getName().compareTo(arg1.getMatch().getP().getName()) == 0) {
					return arg0.getMatch().getD().getId() - arg1.getMatch().getD().getId();
				}
				return arg0.getMatch().getP().getName().compareTo(arg1.getMatch().getP().getName());
			}
		});
		System.out.println("=> Patient admission day");
		admissionDays.forEach(m -> {
			if (m.getValue() == 1) {
				System.out.println("Patient : " + m.getMatch().getP().getName() + ", admission day : "
						+ m.getMatch().getD().getId());
			}
		});
	}

	/**
	 * Saves the result of a run of a given GIPS API to a given path as XMI file.
	 * 
	 * @param gipsApi GIPS API to save results from.
	 * @param path    (XMI) path to save the results to.
	 */
	protected void gipsSave(final GipsEngineAPI<?, ?> gipsApi, final String path) {
		try {
			gipsApi.saveResult(path);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the given ResourceSet to an XMI file at the given file path.
	 * 
	 * @param path File path to save the ResourceSet's contents to.
	 * @param rs   ResourceSet which should be saved to file.
	 */
	protected void writeXmiToFile(final String path, final ResourceSet rs) {
		// Workaround: Always use absolute path
		final URI absPath = URI.createFileURI(path);

		// Create new model for saving
		final ResourceSet rs2 = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl(null));
		// ^null is okay if all paths are absolute
		final Resource r = rs2.createResource(absPath);
		// Fetch model contents from eMoflon
		r.getContents().add(rs.getResources().get(0).getContents().get(0));
		try {
			r.save(null);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builds and solves the ILP problem for the given GIPS API. Also prints the
	 * objective value to the console and throws an error if no solution could be
	 * found.
	 * 
	 * @param gipsApi GIPS API to build and solve the ILP problem for.
	 * @param verbose If true, the method will print some more information about the
	 *                objective value.
	 * @return Returns the objective value.
	 */
	protected double buildAndSolve(final GipsEngineAPI<?, ?> gipsApi, final boolean verbose) {
		gipsApi.buildProblem(true);
		final SolverOutput output = gipsApi.solveProblem();
		if (output.solutionCount() == 0) {
			gipsApi.terminate();
			throw new InternalError("No solution found!");
		}
		if (verbose) {
			System.out.println("=> Objective value: " + output.objectiveValue());
			System.out.println("---");
		}
		return output.objectiveValue();
	}

	/**
	 * Checks if a file for the given path exists and throws an exception otherwise.
	 * 
	 * @param path Path to check the file existence for.
	 */
	protected void checkIfFileExists(final String path) {
		final File xmiInputFile = new File(path);
		if (!xmiInputFile.exists() || xmiInputFile.isDirectory()) {
			throw new IllegalArgumentException("File <" + path + "> could not be found.");
		}
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 */
	protected void applySolution(final IhtcgipssolutionGipsAPI gipsApi, final boolean verbose) {
		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAadp().applyNonZeroMappings(false);
		gipsApi.getAnrs().applyNonZeroMappings(false);
		gipsApi.getArp().applyNonZeroMappings(false);
		gipsApi.getAsp().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		if (verbose) {
			System.out.println("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
		}
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 */
	protected void applySolution(final HardonlyGipsAPI gipsApi, final boolean verbose) {
		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAadp().applyNonZeroMappings(false);
		gipsApi.getAnrs().applyNonZeroMappings(false);
		gipsApi.getArp().applyNonZeroMappings(false);
		gipsApi.getAsp().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		if (verbose) {
			System.out.println("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
		}
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 */
	protected void applySolution(final SoftcnstrtuningGipsAPI gipsApi, final boolean verbose) {
		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAadp().applyNonZeroMappings(false);
		gipsApi.getAnrs().applyNonZeroMappings(false);
		gipsApi.getArp().applyNonZeroMappings(false);
		gipsApi.getAsp().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		if (verbose) {
			System.out.println("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
		}
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 */
	protected void applySolution(final OptionaldelayGipsAPI gipsApi, final boolean verbose) {
		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAadp().applyNonZeroMappings(false);
		gipsApi.getAnrs().applyNonZeroMappings(false);
		gipsApi.getArp().applyNonZeroMappings(false);
		gipsApi.getAsp().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		if (verbose) {
			System.out.println("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
		}
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 */
	protected void applySolution(final OptionalopenotsGipsAPI gipsApi, final boolean verbose) {
		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAadp().applyNonZeroMappings(false);
		gipsApi.getAnrs().applyNonZeroMappings(false);
		gipsApi.getArp().applyNonZeroMappings(false);
		gipsApi.getAsp().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		if (verbose) {
			System.out.println("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
		}
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 */
	protected void applySolution(final OptionalpatientsGipsAPI gipsApi, final boolean verbose) {
		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAadp().applyNonZeroMappings(false);
		gipsApi.getAnrs().applyNonZeroMappings(false);
		gipsApi.getArp().applyNonZeroMappings(false);
		gipsApi.getAsp().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		if (verbose) {
			System.out.println("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
		}
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 */
	protected void applySolution(final PatientssurgeonsroomsGipsAPI gipsApi, final boolean verbose) {
		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAadp().applyNonZeroMappings(false);
		gipsApi.getArp().applyNonZeroMappings(false);
		gipsApi.getAsp().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		if (verbose) {
			System.out.println("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
		}
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 * @param verbose If true, the method will print some more information about the
	 *                GT rule application.
	 */
	protected void applySolution(final NursesroomsGipsAPI gipsApi, final boolean verbose) {
		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAnrs().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		if (verbose) {
			System.out.println("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
		}
	}

	/**
	 * Transforms a given JSON file to an XMI file.
	 * 
	 * @param inputJsonPath Input JSON file.
	 * @param outputXmiPath Output XMI file.
	 */
	protected void transformJsonToModel(final String inputJsonPath, final String outputXmiPath) {
		final JsonToModelLoader loader = new JsonToModelLoader();
		loader.jsonToModel(inputJsonPath);
		final Hospital model = loader.getModel();
		try {
			FileUtils.prepareFolder(instanceFolder);
			FileUtils.save(model, outputXmiPath);
		} catch (final IOException e) {
			throw new InternalError(e.getMessage());
		}
	}

	/**
	 * Transforms the model to JSON.
	 */
	protected void transformModelToJson() {
		final Resource loadedResource = FileUtils.loadModel(gipsOutputPath);
		final Hospital solvedHospital = (Hospital) loadedResource.getContents().get(0);
		final ModelToJsonExporter exporter = new ModelToJsonExporter(solvedHospital);
		exporter.modelToJson(outputPath);
	}

	/**
	 * Runs the execution of the configured scenario.
	 */
	protected abstract void run();

}
