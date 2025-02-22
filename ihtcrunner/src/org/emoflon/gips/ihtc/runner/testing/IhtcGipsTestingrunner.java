package org.emoflon.gips.ihtc.runner.testing;

import org.emoflon.gips.ihtc.runner.softcnstr.optional.delay.IhtcInstancesGipsSoftCnstrOptionalDelayLoopRunner;
import org.emoflon.gips.ihtc.runner.softcnstr.optional.openots.IhtcInstancesGipsSoftCnstrOptionalOpenOtsLoopRunner;
import org.emoflon.gips.ihtc.runner.softcnstr.optional.patients.IhtcInstancesGipsSoftCnstrOptionalPatientsLoopRunner;
import org.emoflon.gips.ihtc.runner.softcnstrtuning.IhtcInstancesGipsSoftCnstrTuningLoopRunner;

public class IhtcGipsTestingrunner {

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
