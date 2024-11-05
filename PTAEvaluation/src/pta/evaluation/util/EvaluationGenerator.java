package pta.evaluation.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import PersonTaskAssignments.PersonTaskAssignmentModel;

import pta.generator.PTAModelGenerator;
import pta.scenario.ScenarioGenerator;
import pta.scenario.house.HouseConstructionBatchA;
import pta.scenario.house.HouseConstructionBatchB;
import pta.scenario.house.HouseConstructionBatchC;
import pta.scenario.house.HouseConstructionBatchD;
import pta.scenario.house.HouseConstructionBatchE;
import pta.scenario.house.HouseConstructionIncF;
import pta.scenario.house.HouseConstructionIncG;

public class EvaluationGenerator {
	
	public static String projectFolder = System.getProperty("user.dir");
	public static String instancesFolder = projectFolder + "/instances/evaluation/scenarios";
	public static String solutionFolder = projectFolder + "/instances/evaluation/solutions";
	public static String csvFolder = projectFolder + "/instances/evaluation/results";
	public static String scriptFolder = projectFolder + "/scripts";
	public static String execScriptFolder = scriptFolder + "/execScripts";
	public static String dispatchScript = scriptFolder + "/script-dispatcher.sh";
	public static String runScript = scriptFolder + "/start-args-gips.sh";
	public static String envScript = scriptFolder + "/env.sh";
	
	public static String gurobiHome = "/opt/gurobi1103/linux64/";
	public static String gurobiLib = "/opt/gurobi1103/linux64/lib/";
	public static String gurobiBin = "/opt/gurobi1103/linux64/bin/";

	public static String[] batchRunnerTypes = {
			HouseConstructionBatchA.TYPE,
			HouseConstructionBatchB.TYPE,
			HouseConstructionBatchC.TYPE,
			HouseConstructionBatchD.TYPE,
			HouseConstructionBatchE.TYPE,
	};
	public static String[] incRunnerTypes = {
			HouseConstructionBatchA.TYPE,
			HouseConstructionIncF.TYPE,
			HouseConstructionIncG.TYPE
	};
	

	public static void main(String[] args) {
		final Options options = new Options();
		
		// License file to use
		final Option licFile = new Option("l", "license", true, "GUROBI license file.");
		licFile.setRequired(true);
		options.addOption(licFile);
		
		// JAR to run
		final Option jarFile = new Option("j", "jar", true, "Executable JAR file.");
		jarFile.setRequired(true);
		options.addOption(jarFile);
		
		final CommandLineParser parser = new DefaultParser();
		final HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException e) {
			System.err.println(e.getMessage());
			formatter.printHelp("CLI parameters", options);
			System.exit(1);
		}
		
		String gurobiLic = cmd.getOptionValue("license");
		String jarLocation = cmd.getOptionValue("jar");
		
		File project = new File(projectFolder);
		if (!project.exists()) {
			project.mkdirs();
		}
		
		File instances = new File(instancesFolder);
		if (!instances.exists()) {
			instances.mkdirs();
		}
		File solutions = new File(solutionFolder);
		if (!solutions.exists()) {
			solutions.mkdirs();
		}
		File csvs = new File(csvFolder);
		if (!csvs.exists()) {
			csvs.mkdirs();
		}
		File scripts = new File(scriptFolder);
		if (!scripts.exists()) {
			scripts.mkdirs();
		}
		File execs = new File(execScriptFolder);
		if (!execs.exists()) {
			execs.mkdirs();
		}
		
		String csvPrefix = csvFolder + "/results";
		List<String> generatedScripts = new LinkedList<>();
		
		for(int i = 1; i<=10; i++) {
			String src = instancesFolder + "/Batch"+i+".xmi";
			String trg = solutionFolder + "/Batch"+i+"_solved.xmi";
			PersonTaskAssignmentModel model = generateBatch(i);
			try {
				PTAModelGenerator.save(model, src);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(String type : batchRunnerTypes) {
				String id = type+"-batch-"+i;
				String script = EvaluationScriptGenerator.genExecutionScript(runScript, src, trg, id, type);
				String scriptPath = execScriptFolder+"/"+id+"-run.sh";
				generatedScripts.add(scriptPath);
				try {
					Files.writeString(new File(scriptPath).toPath(), script);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			src = instancesFolder + "/Inc"+i+".xmi";
			trg = solutionFolder + "/Inc"+i+"_solved.xmi";
			model = generateIncremental(i);
			try {
				PTAModelGenerator.save(model, instancesFolder + "/Inc"+i+".xmi");
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(String type : incRunnerTypes) {
				String id = type+"-inc-"+i;
				String script = EvaluationScriptGenerator.genExecutionScript(runScript, src, trg, id, type);
				String scriptPath = execScriptFolder+"/"+id+"-run.sh";
				generatedScripts.add(scriptPath);
				try {
					Files.writeString(new File(scriptPath).toPath(), script);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		String script = EvaluationScriptGenerator.genDispatchScript(jarLocation, csvPrefix, generatedScripts);
		try {
			Files.writeString(new File(dispatchScript).toPath(), script);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		script = EvaluationScriptGenerator.genStartScript(jarLocation, envScript);
		try {
			Files.writeString(new File(runScript).toPath(), script);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		script = EvaluationScriptGenerator.genEnvScript(gurobiLic, gurobiHome, gurobiLib, gurobiBin);
		try {
			Files.writeString(new File(envScript).toPath(), script);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static PersonTaskAssignmentModel generateBatch(int size) {
		ScenarioGenerator generator = new ScenarioGenerator();
		generator.offerSplitRate = 0.5;
		generator.additionalOfferRate = 0.2;
		generator.scale(size, 1, 0, 6, 0, 5, 0);
		return generator.generate("EpicSeed".hashCode());
	}
	
	public static PersonTaskAssignmentModel generateIncremental(int size) {
		ScenarioGenerator generator = new ScenarioGenerator();
		generator.offerSplitRate = 0.0;
		generator.additionalOfferRate = 0.4;
		generator.scale(size, 1, 0, 10, 0, 15, 0);
		return generator.generate("EpicSeed".hashCode());
	}
	
}