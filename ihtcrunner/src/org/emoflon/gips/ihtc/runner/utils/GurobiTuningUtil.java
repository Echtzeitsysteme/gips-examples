package org.emoflon.gips.ihtc.runner.utils;

import java.lang.reflect.Field;

import org.emoflon.gips.core.GipsEngine;
import org.emoflon.gips.core.api.GipsEngineAPI;
import org.emoflon.gips.core.milp.GurobiSolver;
import org.emoflon.gips.core.milp.Solver;
import org.emoflon.gips.core.milp.SolverConfig;

import com.gurobi.gurobi.GRB.DoubleParam;
import com.gurobi.gurobi.GRB.IntParam;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBModel;

/**
 * Tuning utility to fine tune some of Gurobis parameters via Java reflections.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class GurobiTuningUtil {

	/**
	 * The MIP focus parameter to set.
	 */
	private static final int MIP_FOCUS = 1;

	/**
	 * The MIP gap parameter to set.
	 */
	private static final double MIP_GAP = 0.1;

	/**
	 * No public instances of this class allowed.
	 */
	private GurobiTuningUtil() {
	}

	/**
	 * Overwrites the configured value of the MIP focus of the Gurobi solver within
	 * the given GIPS API.
	 * 
	 * @param gipsApi GIPS API to overwrite the MIP focus value for.
	 */
	public static void setMipFocus(final GipsEngineAPI<?, ?> gipsApi) {
		try {
			final GRBModel grbModel = getGrbModel(gipsApi);
			grbModel.set(IntParam.MIPFocus, MIP_FOCUS);
		} catch (final GRBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Overwrites the configured value of the MIP gap of the Gurobi solver within
	 * the given GIPS API.
	 * 
	 * @param gipsApi GIPS API to overwrite the MIP gap value for.
	 */
	public static void setMipGap(final GipsEngineAPI<?, ?> gipsApi) {
		try {
			final GRBModel grbModel = getGrbModel(gipsApi);
			grbModel.set(DoubleParam.MIPGap, MIP_GAP);
		} catch (final GRBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Overwrites the configured value of the time limit of the Gurobi solver within
	 * the given GIPS API.
	 * 
	 * @param gipsApi      GIPS API to overwrite the time limit value for.
	 * @param newTimeLimit New time limit to set.
	 */
	public static void updateTimeLimit(final GipsEngineAPI<?, ?> gipsApi, final double newTimeLimit) {
		if (newTimeLimit < 0) {
			throw new IllegalArgumentException("Given new time limit was negative.");
		}

		// Update Gurobi solver configuration (that is contained within the Gurobi
		// solver object)
		try {
			final GurobiSolver solver = (GurobiSolver) getSolver(gipsApi);
			Field solverConfigField;
			solverConfigField = GurobiSolver.class.getDeclaredField("config");
			solverConfigField.setAccessible(true);
			final SolverConfig oldConfig = (SolverConfig) solverConfigField.get(solver);
			solverConfigField.set(solver, oldConfig.withNewTimeLimit(newTimeLimit));
		} catch (final NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException(e);
		}

		// Update Gurobi solver itself
		try {
			final GRBModel grbModel = getGrbModel(gipsApi);
			grbModel.set(DoubleParam.TimeLimit, newTimeLimit);
		} catch (final GRBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extracts the solver object from the given GIPS Engine API via Java
	 * reflections.
	 * 
	 * @param gipsApi GIPS Engine API to extract solver object from.
	 * @return Solver object.
	 */
	private static Solver getSolver(final GipsEngineAPI<?, ?> gipsApi) {
		if (gipsApi == null) {
			throw new IllegalArgumentException("Given GIPS API object was null.");
		}

		try {
			final Field solverField = GipsEngine.class.getDeclaredField("solver");
			solverField.setAccessible(true);
			if (!(solverField.get(gipsApi) instanceof GurobiSolver)) {
				throw new UnsupportedOperationException("Solver object of the given GIPS API was not Gurobi.");
			}
			return (GurobiSolver) solverField.get(gipsApi);
		} catch (final NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException(e);
		}
	}

	/**
	 * Extracts the Gurobi solver model from the given GIPS Engine API via Java
	 * reflections.
	 * 
	 * @param gipsApi GIPS Engine API to extract Gurobi model from.
	 * @return Gurobi solver model.
	 */
	private static GRBModel getGrbModel(final GipsEngineAPI<?, ?> gipsApi) {
		if (gipsApi == null) {
			throw new IllegalArgumentException("Given GIPS API object was null.");
		}

		try {
			final GurobiSolver solver = (GurobiSolver) getSolver(gipsApi);

			final Field solverModelField = GurobiSolver.class.getDeclaredField("model");
			solverModelField.setAccessible(true);
			return (GRBModel) solverModelField.get(solver);
		} catch (final NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new UnsupportedOperationException(e);
		}
	}

}
