package org.emoflon.gips.gipsl.examples.sdrmodel.generator;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import sdrmodel.Block;
import sdrmodel.CPU;
import sdrmodel.Core;
import sdrmodel.Flow;
import sdrmodel.Interthreadcom;
import sdrmodel.Job;
import sdrmodel.Root;
import sdrmodel.SdrmodelFactory;
import sdrmodel.SdrmodelPackage;

public class SDRModelGenerator {
	public static final DecimalFormat df = new DecimalFormat("0.00");

	public static String projectFolder = System.getProperty("user.dir");
	public static String instancesFolder = projectFolder + "/instances";

	protected SdrmodelFactory factory = SdrmodelFactory.eINSTANCE;

	protected Map<String, CPU> cpus = new LinkedHashMap<>();
	protected Map<String, Core> cores = new LinkedHashMap<>();
	protected Map<String, sdrmodel.Thread> threads = new LinkedHashMap<>();

	protected Map<String, Job> jobs = new LinkedHashMap<>();
	protected Map<String, Block> blocks = new LinkedHashMap<>();

	protected Root root;

	protected Random rnd;

	public static void main(String[] args) {
		initFileSystem();
//		generateSimpleUniformModel(4, 2, 8, 1, 1, true);
		generateSimpleChainedModel(4, 2, 8, 1, 2, 1, 10, 0.5);
	}

	public SDRModelGenerator(int seed) {
		rnd = new Random(seed);
	}

	public static void initFileSystem() {
		File iF = new File(instancesFolder);
		if (!iF.exists()) {
			iF.mkdirs();
		}
	}

	public static Root generateSimpleUniformModel(int cores, int threadsPerCore, int blocks, double blockComplexity,
			double rate, boolean directed) {
		SDRModelGenerator gen = new SDRModelGenerator("FunSeed123".hashCode());
		gen.generateSimpleCPUModel(cores, threadsPerCore);
		gen.generateSimpleUniformJobModel(blocks, blockComplexity, rate, directed);
		Root root = gen.generate();

		StringBuilder fileName = new StringBuilder();
		fileName.append("/CPU_");
		fileName.append(cores);
		fileName.append("_");
		fileName.append(cores * threadsPerCore);
		fileName.append("@B");
		fileName.append(blocks);
		fileName.append("_C");
		fileName.append(df.format(blockComplexity).replace(",", "-"));
		fileName.append("_R");
		fileName.append(df.format(rate).replace(",", "-"));
		fileName.append("_UNI.xmi");

		try {
			save(root, instancesFolder + fileName.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return root;
	}

	public static Root generateSimpleChainedModel(int cores, int threadsPerCore, int chains, double inputRate, double m,
			int kF, double fC, double n) {
		SDRModelGenerator gen = new SDRModelGenerator("FunSeed123".hashCode());
		gen.generateSimpleCPUModel(cores, threadsPerCore);
		gen.generateChainedJobModel(chains, inputRate, m, kF, fC, n);
		Root root = gen.generate();

		StringBuilder fileName = new StringBuilder();
		fileName.append("/CPU_");
		fileName.append(cores);
		fileName.append("_");
		fileName.append(cores * threadsPerCore);
		fileName.append("@m");
		fileName.append(df.format(m).replace(",", "-"));
		fileName.append("_kF");
		fileName.append(kF);
		fileName.append("_fC");
		fileName.append(df.format(fC).replace(",", "-"));
		fileName.append("_N");
		fileName.append(df.format(n).replace(",", "-"));
		fileName.append("_SimpleChain.xmi");

		try {
			save(root, instancesFolder + fileName.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return root;
	}

	public void generateSimpleCPUModel(int numOfCores, int threadsPerCore) {
		addCPU("CPU", numOfCores, threadsPerCore);
	}

	/*
	 * Creates a chain that consists of: (1) one repeat block (1 < multiplier == m,
	 * complexity == 1), (2) kF filters (multiplier == 1, complexity == fC) and (3)
	 * one keep-N Block (1 > multiplier == n > 0, complexity == 1)
	 * 
	 */
	public void generateChainedJobModel(int chains, double inputRate, double m, int kF, double fC, double n) {
		Job job = addJob("Job(M,kFc,N)", inputRate);
		LinkedList<Block> chain = null;
		double outputRate = 0.0;
		for (int i = 0; i < chains; i++) {
			if (chain == null) {
				chain = generateChain_M_kFc_kN(job, inputRate, m, kF, fC, n);
			} else {
				Block kN = chain.getLast();
				chain = generateChain_M_kFc_kN(job, outputRate, m, kF, fC, n);
				Block M = chain.getFirst();
				addFlow(job, kN, M, outputRate);

			}
			outputRate = calcChainOutput(chain);
		}
	}

	public LinkedList<Block> generateChain_M_kFc_kN(final Job job, double inputRate, double m, int kF, double fC,
			double n) {
		LinkedList<Block> chain = new LinkedList<>();
		Block M = addBlock(job, "M(" + df.format(m) + ")#" + blocks.size(), 1, m);
		double mOutputRate = inputRate * m;
		M.setInputRate(inputRate);
		M.setOutputRate(mOutputRate);
		chain.add(M);

		Block kN = addBlock(job, "kN(" + df.format(n) + ")#" + blocks.size() + kF + 1, 1, n);

		for (int i = 0; i < kF; i++) {
			Block F = addBlock(job, "fC(" + df.format(fC) + ")#" + blocks.size(), fC, 1);
			chain.add(F);
			F.setInputRate(mOutputRate);
			F.setOutputRate(mOutputRate);

			kN.setInputRate(kN.getInputRate() + F.getOutputRate());

			addFlow(job, M, F, mOutputRate);
			addFlow(job, F, kN, mOutputRate);
		}

		kN.setOutputRate(kN.getInputRate() * n);
		chain.add(kN);
		return chain;
	}

	public void generateSimpleRndJobModel(int maxNumOfBlocks, double minComplexity, double maxComplexity,
			double minRate, double maxRate, boolean directed) {
		Job job = addJob("Job", 1);
		int numOfBlocks = rnd.nextInt(maxNumOfBlocks);
		LinkedList<Block> blocks = new LinkedList<>();
		for (int i = 0; i < numOfBlocks; i++) {
			blocks.add(addBlock(job, job.getName() + "_Block#" + i, rnd.nextDouble(minComplexity, maxComplexity), 1.0));
		}

		if (directed) {
			Block current = blocks.poll();
			while (!blocks.isEmpty()) {
				Block next = blocks.poll();
				addFlow(job, current, next, rnd.nextDouble(minRate, maxRate));
				current = next;
			}
		} else {
			for (Block block1 : blocks) {
				for (Block block2 : blocks) {
					if (block1.equals(block2))
						continue;

					addFlow(job, block1, block2, rnd.nextDouble(minRate, maxRate));
				}
			}
		}
	}

	public void generateSimpleUniformJobModel(int maxNumOfBlocks, double complexity, double rate, boolean directed) {
		Job job = addJob("Job", 1);
		int numOfBlocks = maxNumOfBlocks;
		LinkedList<Block> blocks = new LinkedList<>();
		for (int i = 0; i < numOfBlocks; i++) {
			blocks.add(addBlock(job, job.getName() + "_Block#" + i, complexity, 1.0));
		}

		if (directed) {
			Block current = blocks.poll();
			while (!blocks.isEmpty()) {
				Block next = blocks.poll();
				addFlow(job, current, next, rate);
				current = next;
			}
		} else {
			for (Block block1 : blocks) {
				for (Block block2 : blocks) {
					if (block1.equals(block2))
						continue;

					addFlow(job, block1, block2, rate);
				}
			}
		}
	}

	public Root generate() {
		root = factory.createRoot();
		root.getCpus().addAll(cpus.values());
		root.getJobs().addAll(jobs.values());

		return root;
	}

	public CPU addCPU(String name, int numOfCores, int threadsPerCore) {
		CPU cpu = factory.createCPU();
		cpu.setName(name);
		cpu.setThreadsPerCore(threadsPerCore);
		cpu.setNumOfThreads(threadsPerCore * numOfCores);
		cpu.setInverseThreadCount(1.0 / cpu.getNumOfThreads());

		for (int i = 0; i < numOfCores; i++) {
			String cName = name + "_core#" + i;
			addCore(cpu, cName, threadsPerCore);
		}

		for (sdrmodel.Thread thread1 : threads.values()) {
			for (sdrmodel.Thread thread2 : threads.values()) {
				if (thread1.equals(thread2))
					continue;

				addInterthreadCom(cpu, thread1, thread2);
			}
		}
		cpus.put(name, cpu);
		return cpu;
	}

	public Core addCore(final CPU parent, String name, int numOfThreads) {
		Core core = factory.createCore();
		core.setName(name);
		for (int i = 0; i < numOfThreads; i++) {
			String tName = name + "_thread#" + i;
			addThread(core, tName);
		}

		cores.put(name, core);
		parent.getCores().add(core);
		return core;
	}

	public sdrmodel.Thread addThread(final Core parent, String name) {
		sdrmodel.Thread thread = factory.createThread();
		thread.setName(name);
		threads.put(name, thread);
		parent.getThreads().add(thread);

		return thread;
	}

	public Interthreadcom addInterthreadCom(final CPU parent, final sdrmodel.Thread src, sdrmodel.Thread trg) {
		Interthreadcom itc = factory.createInterthreadcom();
		itc.setName(src.getName() + "-(itc)->" + trg.getName());
		itc.setSource(src);
		itc.setTarget(trg);
		parent.getIntercoms().add(itc);

		return itc;
	}

	public Job addJob(String name, double inputRate) {
		Job job = factory.createJob();
		job.setName(name);
		job.setInputRate(inputRate);
		jobs.put(name, job);

		return job;
	}

	public Block addBlock(final Job job, String name, double complexity, double rateMultiplier) {
		if (!(jobs.containsKey(job.getName()) && jobs.get(job.getName()).equals(job)))
			return null;

		Block block = factory.createBlock();
		block.setName(name);
		block.setRelativeComplexity(complexity);
		block.setOutputRateMultiplier(rateMultiplier);
		job.getBlocks().add(block);
		blocks.put(name, block);

		return block;
	}

	public Flow addFlow(final Job job, final Block src, final Block trg, double rate) {
		if (!(jobs.containsKey(job.getName()) && jobs.get(job.getName()).equals(job)))
			return null;

		if (!(blocks.containsKey(src.getName()) && blocks.get(src.getName()).equals(src)))
			return null;

		if (!(blocks.containsKey(trg.getName()) && blocks.get(trg.getName()).equals(trg)))
			return null;

		Flow flow = factory.createFlow();
		flow.setRate(rate);
		flow.setSource(src);
		flow.setTarget(trg);
		job.getFlows().add(flow);

		return flow;
	}

	public static double calcChainOutput(LinkedList<Block> chain) {
		Block M = chain.getFirst();
		Block kN = chain.getLast();
		return M.getOutputs().stream().map(flow -> flow.getRate()).reduce(0.0, (sum, rate) -> sum + rate)
				* kN.getOutputRateMultiplier();
	}

	public static void save(Root model, String path) throws IOException {
		URI uri = URI.createFileURI(path);
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(SdrmodelPackage.eNS_URI, SdrmodelPackage.eINSTANCE);
		Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
		r.unload();
	}

	public static Resource saveAndReturn(Root model, String path) throws IOException {
		URI uri = URI.createFileURI(path);
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(SdrmodelPackage.eNS_URI, SdrmodelPackage.eINSTANCE);
		Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
		return r;
	}
}
