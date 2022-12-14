package org.emoflon.gips.gipsl.examples.sdrmodel.generator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import sdrmodel.Block;
import sdrmodel.Flow;
import sdrmodel.Interthreadcom;
import sdrmodel.Root;
import sdrmodel.SdrmodelPackage;


public class SDRModelValidator {
	public static void main(String[] args) {
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/../org.emoflon.gips.gipsl.examples.sdr.extended";
		String file = instancesFolder + "/model-out3.xmi";
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
		.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(SdrmodelPackage.eNS_URI, SdrmodelPackage.eINSTANCE);
		URI fileURI = URI.createFileURI(file);
		Resource r = rs.getResource(fileURI, true);
		final Root root = (Root) r.getContents().get(0);
		boolean valid = new SDRModelValidator().validate(root);
		
		if(valid) {
			System.out.println("INFO: Model is valid!");
		} else {
			System.out.println("INFO: Model is not valid!");
		}
	}

	public boolean validate(Root root) {
		boolean valid = true;
		root.getJobs().stream().flatMap(job -> job.getBlocks().stream()).map(block -> {
			boolean isValid = true;
			isValid &= block.getHost() != null;
			if(block.getOutputs() != null && !block.getOutputs().isEmpty()) {
				for(Flow flow : block.getOutputs()) {
					Block trg = flow.getTarget();
					if(trg.getHost() == null) {
						isValid = false;
						break;
					}
					
					if(block.getHost().equals(trg.getHost())) {
						isValid &= flow.getHost().equals(block.getHost());
					} else {
						Interthreadcom itc = getInterthreadCom(root, block.getHost(), trg.getHost());
						isValid &= flow.getHost().equals(itc);
					}
				}
			}
			return isValid;
		});
		
		// Calc Jain's fairness index
		// Step 1: square of sums
		double sqOfSums = root.getCpus().stream()
				.flatMap(cpu -> cpu.getCores().stream()
						.flatMap(core -> core.getThreads().stream()))
				.flatMap(thread -> thread.getGuests().stream())
				.map(block -> (double) block.getCycles())
				.reduce(0.0, (sum, cycles) -> sum + cycles);
		sqOfSums = Math.pow(sqOfSums, 2);
		
		// Step 2: sumOfSquares
		double sumOfSqs = root.getCpus().stream()
				.flatMap(cpu -> cpu.getCores().stream()
						.flatMap(core -> core.getThreads().stream()))
				.map(thread -> Math.pow(thread.getGuests().stream()
						.map(block -> (double) block.getCycles())
						.reduce(0.0, (sum, cycles) -> sum + cycles), 2))
				.reduce(0.0, (sum, cycles) -> sum + cycles);
		
		// Step 2: numOf
		long numOfThreads = root.getCpus().stream()
				.flatMap(cpu -> cpu.getCores().stream()
						.flatMap(core -> core.getThreads().stream())).count();
		
		double jfi = sqOfSums / (numOfThreads * sumOfSqs);
		
		System.out.println("Jain's fairness index value: " + jfi);
		
		if(valid) {
			System.out.println("Solution of given SDR model is valid. :)");
		} else {
			System.out.println("Solution of given SDR model is invalid. :(");
		}
		
		return valid;
	}
	
	public static Interthreadcom getInterthreadCom(final Root root, final sdrmodel.Thread src, final sdrmodel.Thread trg) {
		return root.getCpus().stream().flatMap(cpu -> cpu.getIntercoms().stream()).filter(itc -> itc.getSource().equals(src) && itc.getTarget().equals(trg)).findFirst().get();
	}
}
