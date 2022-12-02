package pta.example.house;

import java.io.File;
import java.io.IOException;

import PersonTaskAssignments.Offer;
import PersonTaskAssignments.Person;
import PersonTaskAssignments.PersonTaskAssignmentModel;
import PersonTaskAssignments.PersonTaskAssignmentsFactory;
import PersonTaskAssignments.PersonTaskAssignmentsPackage;
import PersonTaskAssignments.Project;
import PersonTaskAssignments.Requirement;
import PersonTaskAssignments.Skill;
import PersonTaskAssignments.SkillType;
import PersonTaskAssignments.Task;
import PersonTaskAssignments.Week;
import pta.generator.PTAModelGenerator;

public class HouseConstructionGenerator extends PTAModelGenerator{
	
	final static double HOURS_APPRENTICE = 16;
	final static double HOURS_JOURNYMAN = 38;
	final static double HOURS_MASTER = 50;
	
	final static double SALARY_APPRENTICE = 30;
	final static double SALARY_JOURNYMAN = 65;
	final static double SALARY_MASTER = 100;
	
	final static double BONUS_APPRENTICE = 65;
	final static double BONUS_JOURNYMAN = 100;
	final static double BONUS_MASTER = 150;
	
	public static void main(String[] args) {
		String projectFolder = System.getProperty("user.dir");
		String instancesFolder = projectFolder + "/instances";
		File iF = new File(instancesFolder);
		if(!iF.exists()) {
			iF.mkdirs();
		}
		
		PersonTaskAssignmentModel model = new HouseConstructionGenerator().constructProject1();
		
		try {
			save(model, instancesFolder + "/ConstructionProject1.xmi");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PersonTaskAssignmentModel constructProject1(){
		addWeeks(1, 52);
		final String projectName = "ConstructHouse";
		addProject("ConstructHouse", 500000, 25, 10000, 1);
		Task aushub = addTask(projectName, "Aushub", new String[0]);
		SkillType baggern = addSkillType("Baggerfahren");
		SkillType graben = addSkillType("Erdarbeiten");
		addRequirement(projectName, aushub.getName(), 30, 2, baggern.getName(), SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, aushub.getName(), 30, 1, graben.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
			
		Task kanal = addTask(projectName, "Kanalarbeiten", new String[0]);
		SkillType klempnern = addSkillType("Klempnern");
		addRequirement(projectName, kanal.getName(), 8, 2, baggern.getName(), SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, kanal.getName(), 8, 1, graben.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, kanal.getName(), 8, 2, klempnern.getName(), SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, kanal.getName(), 4, 3, klempnern.getName(), SALARY_MASTER, BONUS_MASTER);
		
		Task dachstuhl = addTask(projectName, "Dachstuhl", new String[0]);
		SkillType schreinern = addSkillType("Schreinern");
		addRequirement(projectName, dachstuhl.getName(), 120, 1, schreinern.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, dachstuhl.getName(), 120, 2, schreinern.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, dachstuhl.getName(), 40, 3, schreinern.getName(), SALARY_MASTER, BONUS_MASTER);
		
		Task fundament = addTask(projectName, "Fundament", aushub.getName(), kanal.getName());
		SkillType fundamente = addSkillType("Fundamentarbeiten");
		SkillType noop = addSkillType("NO_OP");
		addRequirement(projectName, fundament.getName(), 20, 2, fundamente.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, fundament.getName(), 8, 3, fundamente.getName(), SALARY_MASTER, BONUS_MASTER);
		addRequirement(projectName, fundament.getName(), 20*24, 1, noop.getName(), 0, 0);
		
		Task rohbau = addTask(projectName, "Rohbau", fundament.getName());
		SkillType mauern = addSkillType("Mauern");
		addRequirement(projectName, rohbau.getName(), 40, 1, mauern.getName(), SALARY_APPRENTICE, BONUS_APPRENTICE);
		addRequirement(projectName, rohbau.getName(), 100, 2, mauern.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, rohbau.getName(), 40, 3, mauern.getName(), SALARY_MASTER, BONUS_MASTER);
		
		Task dach = addTask(projectName, "Dachdecken", rohbau.getName(), dachstuhl.getName());
		SkillType dachdecken = addSkillType("Dachdecken");
		addRequirement(projectName, dach.getName(), 70, 2, dachdecken.getName(),  SALARY_JOURNYMAN, BONUS_JOURNYMAN);
		addRequirement(projectName, dach.getName(), 20, 3, dachdecken.getName(), SALARY_MASTER, BONUS_MASTER);
		
		Task gws = addTask(projectName, "GasWasserAbwasser", dach.getName());
		Task strom = addTask(projectName, "Strom", dach.getName());
		Task fenster = addTask(projectName, "Fenster", dach.getName());
		Task innen = addTask(projectName, "Innenausbau", gws.getName(), strom.getName(), fenster.getName());
		Task daemmung = addTask(projectName, "Daemmung", gws.getName(), strom.getName(), fenster.getName());
		Task putz = addTask(projectName, "Verputzen", daemmung.getName());
		Task abnahme = addTask(projectName, "Abnahme", innen.getName(), putz.getName());
	
		return generate();
	}
}
