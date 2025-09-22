package nurserosteringgipsl.runner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
// SmartEMF wie beim Speichern verwenden
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;

import nurserosteringmodel.CoverRequirement;
import nurserosteringmodel.Day;
import nurserosteringmodel.Employee;
import nurserosteringmodel.NurserosteringmodelPackage;
import nurserosteringmodel.Root;
import nurserosteringmodel.Shift;
import nurserosteringmodel.ShiftType;

public class SolutionXmlFromEmf {

  // Auf true setzen, wenn Live-Assignments in die Konsole ausgeben werden sollen
  private static final boolean DEBUG_LOG_ASSIGNMENTS = false;

  /**
   * Erzeugt eine INRC-I-konforme solution.xml aus dem gelösten EMF-Modell.
   * @param solvedXmi           gelöstes XMI (z. B. .../resources/solved.xmi)
   * @param outSolutionXml      Zieldatei (z. B. C:/Users/walid/bachelorthesis/solution.xml)
   * @param schedulingPeriodID  z. B. "sprint01"
   */
  public static void writeSolutionXml(File solvedXmi, File outSolutionXml, String schedulingPeriodID) throws IOException {
    if (solvedXmi == null || !solvedXmi.isFile()) {
      throw new IOException("solvedXmi nicht gefunden: " + (solvedXmi == null ? "<null>" : solvedXmi.getAbsolutePath()));
    }

    // 1) EMF registrieren + SmartEMF-Factory für *.xmi (wie beim Speichern)
    ResourceSet rs = new ResourceSetImpl();
    rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
      .put("xmi", new SmartEMFResourceFactoryImpl("../"));
    rs.getPackageRegistry().put(NurserosteringmodelPackage.eNS_URI, NurserosteringmodelPackage.eINSTANCE);

    Resource r = rs.getResource(URI.createFileURI(solvedXmi.getAbsolutePath()), true);
    if (r.getContents().isEmpty() || !(r.getContents().get(0) instanceof Root)) {
      throw new IOException("Unerwarteter XMI-Inhalt: Root nicht gefunden.");
    }
    Root root = (Root) r.getContents().get(0);

    // 2) solution.xml schreiben
    int assignCount = 0;
    try (BufferedWriter w = new BufferedWriter(new FileWriter(outSolutionXml, false))) {
      w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      w.write("<Solution>\n");
      w.write("  <SchedulingPeriodID>" + esc(nvl(schedulingPeriodID)) + "</SchedulingPeriodID>\n");
      w.write("  <Competitor>auto-converted</Competitor>\n");

      for (Day day : nvlList(root.getDays())) {
        final String date = nvl(day.getDate());
        if (date.isBlank()) continue;

        for (CoverRequirement cr : nvlList(day.getRequirements())) {
          final Shift sh = cr.getShift();
          if (sh == null) continue;

          final ShiftType type = sh.getType();
          final String shiftTypeName = type != null ? nvl(type.getName()).trim() : "";
          if (shiftTypeName.isEmpty()) continue;

          for (Employee emp : nvlList(sh.getAssignedEmployees())) {
            final String employeeValue = employeeValue(emp);
            if (employeeValue.isEmpty()) continue;

            if (DEBUG_LOG_ASSIGNMENTS) {
              System.out.printf("ASSIGN %s  %s  empId=%s (name=%s)%n",
                  date, shiftTypeName, employeeValue, nvl(emp.getName()));
            }

            w.write("  <Assignment>\n");
            w.write("    <Date>" + esc(date) + "</Date>\n");
            w.write("    <Employee>" + esc(employeeValue) + "</Employee>\n");
            w.write("    <ShiftType>" + esc(shiftTypeName) + "</ShiftType>\n");
            w.write("  </Assignment>\n");
            assignCount++;
          }
        }
      }

      w.write("</Solution>\n");
    }

    if (DEBUG_LOG_ASSIGNMENTS) {
      System.out.println("TOTAL assignments (EMF traversed) = " + assignCount);
    }
    System.out.println("solution.xml geschrieben: " + outSolutionXml.getAbsolutePath());
  }

  /** Bevorzuge immer die numerische Employee-ID; fallback auf name falls nötig. */
  private static String employeeValue(Employee emp) {
    if (emp == null) return "";
    // Viele deiner Modelle haben name == "0..9". Nimm das, wenn numerisch.
    final String name = emp.getName();
    if (name != null && name.matches("\\d+")) return name;
    // Sonst echte ID-Eigenschaft verwenden:
    try {
      return String.valueOf(emp.getEmployeeId());
    } catch (Throwable t) {
      // letzter Fallback, falls das Attribut im Modell abweicht:
      return nvl(name);
    }
  }

  private static String nvl(String s) { return s == null ? "" : s; }

  private static <T> EList<T> nvlList(EList<T> l) {
    return (l != null) ? l : ECollections.<T>emptyEList();
  }

  private static String esc(String s) {
    if (s == null) return "";
    return s.replace("&","&amp;")
            .replace("<","&lt;")
            .replace(">","&gt;")
            .replace("\"","&quot;")
            .replace("'","&apos;");
  }
}
