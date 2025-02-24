package org.emoflon.gips.ihtc.runner.testing;

import org.emoflon.gips.ihtc.runner.softcnstr.optional.delay.IhtcInstancesGipsSoftCnstrOptionalDelayLoopRunner;
import org.emoflon.gips.ihtc.runner.softcnstr.optional.openots.IhtcInstancesGipsSoftCnstrOptionalOpenOtsLoopRunner;
import org.emoflon.gips.ihtc.runner.softcnstr.optional.patients.IhtcInstancesGipsSoftCnstrOptionalPatientsLoopRunner;
import org.emoflon.gips.ihtc.runner.softcnstrtuning.IhtcInstancesGipsSoftCnstrTuningLoopRunner;

/**
 * This runner can be used to combine multiple other runners and execute them
 * sequentially.
 * 
 * @author Maximilian Kratz (maximilian.kratz@es.tu-darmstadt.de)
 */
public class IhtcGipsTestingrunner {

	/**
	 * Main method to start the runner. Arguments will be ignored.
	 * 
	 * @param args Will be ignored.
	 */
	public static void main(final String[] args) {
		System.err.println("=> STARTING RUNNER: optional delay");
		IhtcInstancesGipsSoftCnstrOptionalDelayLoopRunner.main(null);

		System.err.println("=> STARTING RUNNER: optional open ots");
		IhtcInstancesGipsSoftCnstrOptionalOpenOtsLoopRunner.main(null);

		System.err.println("=> STARTING RUNNER: optional patients");
		IhtcInstancesGipsSoftCnstrOptionalPatientsLoopRunner.main(null);

		System.err.println("=> STARTING RUNNER: soft cnstr tuning");
		IhtcInstancesGipsSoftCnstrTuningLoopRunner.main(null);
	}

}
