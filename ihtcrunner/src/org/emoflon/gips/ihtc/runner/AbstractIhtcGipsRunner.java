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

/**
 * This abstract runner contains utility methods to wrap a given GIPS API object
 * in the context of the IHTC 2024 example.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public abstract class AbstractIhtcGipsRunner {

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
	 * @return Returns the objective value.
	 */
	protected double buildAndSolve(final GipsEngineAPI<?, ?> gipsApi) {
		gipsApi.buildProblem(true);
		final SolverOutput output = gipsApi.solveProblem();
		if (output.solutionCount() == 0) {
			gipsApi.terminate();
			throw new InternalError("No solution found!");
		}
		System.out.println("=> Objective value: " + output.objectiveValue());
		System.out.println("---");
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
	 */
	protected void applySolution(final IhtcgipssolutionGipsAPI gipsApi) {
		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAadp().applyNonZeroMappings(false);
		gipsApi.getAnrs().applyNonZeroMappings(false);
		gipsApi.getArp().applyNonZeroMappings(false);
		gipsApi.getAsp().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		System.out.println("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 */
	protected void applySolution(final HardonlyGipsAPI gipsApi) {
		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAadp().applyNonZeroMappings(false);
		gipsApi.getAnrs().applyNonZeroMappings(false);
		gipsApi.getArp().applyNonZeroMappings(false);
		gipsApi.getAsp().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		System.out.println("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 */
	protected void applySolution(final PatientssurgeonsroomsGipsAPI gipsApi) {
		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAadp().applyNonZeroMappings(false);
		gipsApi.getArp().applyNonZeroMappings(false);
		gipsApi.getAsp().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		System.out.println("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
	}

	/**
	 * Applies the best found solution (i.e., all non-zero mappings) with a given
	 * IHTC 2024 project GIPS API object.
	 * 
	 * @param gipsApi IHTC 2024 project GIPS API object to get all mapping
	 *                information from.
	 */
	protected void applySolution(final NursesroomsGipsAPI gipsApi) {
		// Apply found solution
		final long tick = System.nanoTime();
		gipsApi.getAnrs().applyNonZeroMappings(false);
		final long tock = System.nanoTime();
		System.out.println("=> GT rule application duration: " + (tock - tick) / 1_000_000_000 + "s.");
	}

}
