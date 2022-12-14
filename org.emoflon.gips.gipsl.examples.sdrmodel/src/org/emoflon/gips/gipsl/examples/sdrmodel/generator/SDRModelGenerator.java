package org.emoflon.gips.gipsl.examples.sdrmodel.generator;

import java.io.File;
import java.io.IOException;
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
	protected SdrmodelFactory factory = SdrmodelFactory.eINSTANCE;
	
	protected Map<String, CPU> cpus = new LinkedHashMap<>();
	protected Map<String, Core> cores = new LinkedHashMap<>();
	protected Map<String, sdrmodel.Thread> threads = new LinkedHashMap<>();
	
	protected Map<String, Job> jobs = new LinkedHashMap<>();
	protected Map<String, Block> blocks = new LinkedHashMap<>();
	
	protected Root root;
	
	protected Random rnd;
	
	public static void main(String[] args) {
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		File iF = new File(instancesFolder);
		if(!iF.exists()) {
			iF.mkdirs();
		}
		
		SDRModelGenerator gen = new SDRModelGenerator("FunSeed123".hashCode());
//		Root root = gen.generateSimpleRndModel(15, 100, 50, true);
		Root root = gen.generateSimpleUniformModel(8, 50, 20, true);
		
		try {
			save(root, instancesFolder + "/CPU_4_8-B8_C50_R20_UNI.xmi");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Root generateSimpleRndModel(int maxNumOfBlocks, int maxCycles, int maxRate, boolean directed) {
		addCPU("CPU", 4, 2);
		Job job = addJob("Job");
		int numOfBlocks = rnd.nextInt(maxNumOfBlocks);
		LinkedList<Block> blocks = new LinkedList<>();
		for(int i=0; i<numOfBlocks; i++) {
			blocks.add(addBlock(job, job.getName() + "_Block#" + i, rnd.nextInt(maxCycles)));
		}
		
		if(directed) {
			Block current = blocks.poll();
			while(!blocks.isEmpty()) {
				Block next = blocks.poll();
				addFlow(job, current, next, rnd.nextInt(maxRate));
				current = next;
			}
		} else {
			for(Block block1 : blocks) {
				for(Block block2 : blocks) {
					if(block1.equals(block2))
						continue;
					
					addFlow(job, block1, block2, rnd.nextInt(maxRate));
				}
			}
		}
		
		return generate();
	}
	
	public Root generateSimpleUniformModel(int maxNumOfBlocks, int maxCycles, int maxRate, boolean directed) {
		addCPU("CPU", 4, 2);
		Job job = addJob("Job");
		int numOfBlocks = maxNumOfBlocks;
		LinkedList<Block> blocks = new LinkedList<>();
		for(int i=0; i<numOfBlocks; i++) {
			blocks.add(addBlock(job, job.getName() + "_Block#" + i, maxCycles));
		}
		
		if(directed) {
			Block current = blocks.poll();
			while(!blocks.isEmpty()) {
				Block next = blocks.poll();
				addFlow(job, current, next, maxRate);
				current = next;
			}
		} else {
			for(Block block1 : blocks) {
				for(Block block2 : blocks) {
					if(block1.equals(block2))
						continue;
					
					addFlow(job, block1, block2, maxRate);
				}
			}
		}
		
		return generate();
	}
	
	public SDRModelGenerator(int seed) {
		rnd = new Random(seed);
	}
	
	public Root generate() {
		root = factory.createRoot();
		root.getCpus().addAll(cpus.values());
		root.getJobs().addAll(jobs.values());
		
		return root;
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
	
	public CPU addCPU(String name, int numOfCores, int threadsPerCore) {
		CPU cpu = factory.createCPU();
		cpu.setName(name);
		cpu.setThreadsPerCore(threadsPerCore);
		cpu.setNumOfThreads(threadsPerCore*numOfCores);
		cpu.setInverseThreadCount(1.0/cpu.getNumOfThreads());
		
		for(int i = 0; i<numOfCores; i++) {
			String cName = name + "_core#" + i;
			addCore(cpu, cName, threadsPerCore);
		}
		
		for(sdrmodel.Thread thread1 : threads.values()) {
			for(sdrmodel.Thread thread2 : threads.values()) {
				if(thread1.equals(thread2))
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
		for(int i = 0; i<numOfThreads; i++) {
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
	
	public Job addJob(String name) {
		Job job = factory.createJob();
		job.setName(name);
		jobs.put(name, job);
		
		return job;
	}
	
	public Block addBlock(final Job job, String name, int cycles) {
		if(!(jobs.containsKey(job.getName()) && jobs.get(job.getName()).equals(job)))
			return null;
		
		Block block = factory.createBlock();
		block.setName(name);
		block.setCycles(cycles);
		job.getBlocks().add(block);
		blocks.put(name, block);
		
		return block;
	}
	
	public Flow addFlow(final Job job, final Block src, final Block trg, int rate) {
		if(!(jobs.containsKey(job.getName()) && jobs.get(job.getName()).equals(job)))
			return null;
		
		if(!(blocks.containsKey(src.getName()) && blocks.get(src.getName()).equals(src)))
			return null;
		
		if(!(blocks.containsKey(trg.getName()) && blocks.get(trg.getName()).equals(trg)))
			return null;
		
		Flow flow = factory.createFlow();
		flow.setRate(rate);
		flow.setSource(src);
		flow.setTarget(trg);
		job.getFlows().add(flow);
		
		return flow;
	}
	
}
