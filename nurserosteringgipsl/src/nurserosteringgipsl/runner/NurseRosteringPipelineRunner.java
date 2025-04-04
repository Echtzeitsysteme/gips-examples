package nurserosteringgipsl.runner;

import nurserosteringmodel.loader.INRC1Loader;
import nurserosteringmodel.validator.NurseRosteringModelValidator;

public class NurseRosteringPipelineRunner {

	public static void main(final String[] args) {
		INRC1Loader.main(null);
		NurseRosteringRunner.main(null);
		NurseRosteringModelValidator.main(null);
	}

}
