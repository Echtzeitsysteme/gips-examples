package org.gips.nodevalue.runner;

import nodevaluemetamodel.generator.NodeValueModelGenerator;
import nodevaluemetamodel.validator.NodeValueModelValidator;

public class NodeValuePipelineRunner {

	public static void main(final String[] args) {
		NodeValueModelGenerator.main(null);
		NodeValueRunner.main(null);
		NodeValueModelValidator.main(null);
	}

}
