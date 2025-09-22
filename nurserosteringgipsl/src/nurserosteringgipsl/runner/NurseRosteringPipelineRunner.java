package nurserosteringgipsl.runner;

import java.io.File;

import nurserosteringmodel.loader.INRC1Loader;
import nurserosteringmodel.validator.NurseRosteringModelValidator;

public class NurseRosteringPipelineRunner {

  public static void main(final String[] args) {
    try {
    
      final String instanceFileName   = "medium/medium_late01.xml";
      final String schedulingPeriodID = instanceFileName
          .replace('\\','/')
          .replaceAll("^.*/", "")      // "sprint_hidden02.xml"
          .replaceFirst("\\.xml$", ""); // .xml weg -> "sprint_hidden02"

      //ResourcesOrdner bestimmen
      final String projectFolder = System.getProperty("user.dir");
      final File resourcesDir    = new File(projectFolder, "../nurserosteringmodel/resources");


      INRC1Loader.main(new String[] { instanceFileName }); 
      NurseRosteringRunner.main(null);                     
      NurseRosteringModelValidator.main(null);    

      // Lösung schreiben 
      final File solvedXmi   = new File(resourcesDir, "solved.xmi");
      final File solutionXml = new File("C:/Users/walid/bachelorthesis/solution.xml");

      SolutionXmlFromEmf.writeSolutionXml(solvedXmi, solutionXml, schedulingPeriodID); 

    } catch (Exception ex) {
      ex.printStackTrace();
      System.err.println("Fehler in der Pipeline");
    }
  }
}
