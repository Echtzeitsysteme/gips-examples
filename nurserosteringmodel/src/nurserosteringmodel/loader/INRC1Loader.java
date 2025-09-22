package nurserosteringmodel.loader;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import nurserosteringmodel.Contract;
import nurserosteringmodel.CoverRequirement;
import nurserosteringmodel.Day;
import nurserosteringmodel.DayOffRequest;
import nurserosteringmodel.Employee;
import nurserosteringmodel.NurserosteringmodelFactory;
import nurserosteringmodel.NurserosteringmodelPackage;
import nurserosteringmodel.Root;
import nurserosteringmodel.Shift;
import nurserosteringmodel.ShiftOffRequest;
import nurserosteringmodel.ShiftType;
import nurserosteringmodel.Skill;

/**
 * This class can be used to load XML files of the NRC1 data set and transform
 * them to an EMF-based model instance.
 * 
 * The current state does not fully parse all fields completely correct. There
 * are some TODOs left in this class.
 */
public class INRC1Loader {

	private NurserosteringmodelFactory modelFactory = NurserosteringmodelFactory.eINSTANCE;
	private String startDate;
	private String endDate;
	private Set<Shift> shifts = new HashSet<Shift>();
	private Map<String, ShiftType> shiftTypes = new HashMap<String, ShiftType>();
	private Set<Skill> skills = new HashSet<Skill>();
	private String schedulingPeriodID;
	private Map<String, Set<String>> shiftNeedsSkill = new HashMap<>();
	private Map<String, Set<String>> employeeHasSkill = new HashMap<>();

	private Set<Day> days = new HashSet<Day>();
	private Set<Employee> employees = new HashSet<Employee>();

	// Map $dayName -> "shiftName" -> preferred no of employees
	private Map<String, Map<String, Integer>> coverRequirements = new HashMap<String, Map<String, Integer>>();

	private Map<String, String> employee2Contract = new HashMap<>();

	private Set<Contract> contracts = new HashSet<Contract>();

    private Set<DayOffRequest> dayOffRequests = new HashSet<DayOffRequest>();
	
    private Set<ShiftOffRequest> shiftOffRequests = new HashSet<ShiftOffRequest>();
    
	public static void main(final String[] args) {
		  try {
			    final String projectFolder = System.getProperty("user.dir");
			    final File resourcesDir = new File(projectFolder, "../nurserosteringmodel/resources");

			    // Erwartet z. B.: "sprint/sprint_hidden02" (ohne .xml) oder "sprint/sprint_hidden02.xml"
			    if (args == null || args.length == 0 || args[0] == null || args[0].isBlank()) {
			      throw new IllegalArgumentException(
			          "Bitte Instanz angeben, z.B. \"sprint/sprint_hidden02\" oder \"medium/medium01\"");
			    }

			    String instanceBase = args[0].replace('\\', '/'); // Windows \ -> /
			    if (!instanceBase.endsWith(".xml")) instanceBase += ".xml";

			    final File instanceXml = new File(resourcesDir, instanceBase);
			    if (!instanceXml.isFile()) {
			      throw new IllegalArgumentException("Instanz-XML nicht gefunden: " + instanceXml.getAbsolutePath());
			    }

			    final INRC1Loader loader = new INRC1Loader();
			    loader.loadFromXmlFile(instanceXml.getAbsolutePath());
			    final Root root = loader.transform();

			    save(root, new File(resourcesDir, "model.xmi").getAbsolutePath());
			    System.out.println("=> Loaded: " + instanceXml.getName());
			  } catch (Exception e) {
			    e.printStackTrace();
			  }
			}

	public void loadFromXmlFile(final String filepath) throws ParserConfigurationException, SAXException, IOException {
		if (filepath == null || filepath.isBlank()) {
			throw new IllegalArgumentException("Given"
					+ " file path was null or empty.");
		}

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//	    factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);
		final DocumentBuilder builder = factory.newDocumentBuilder();
		final File file = new File(filepath);
		final Document doc = builder.parse(file);
		doc.normalize();

		// Get root node (the scheduling period node)
		final var root = doc.getElementsByTagName("SchedulingPeriod").item(0);
		final NodeList children = root.getChildNodes();
		
		// SchedulingPeriod-ID aus Attribut lesen (z. B. "sprint01")
		if (root instanceof org.w3c.dom.Element) {
		    org.w3c.dom.Element spEl = (org.w3c.dom.Element) root;

		    
		    if (spEl.hasAttribute("ID")) {
		        this.schedulingPeriodID = spEl.getAttribute("ID");
		    } else if (spEl.hasAttribute("Id")) {
		        this.schedulingPeriodID = spEl.getAttribute("Id");
		    } else if (spEl.hasAttribute("id")) {
		        this.schedulingPeriodID = spEl.getAttribute("id");
		    }

		    if (this.schedulingPeriodID == null || this.schedulingPeriodID.isBlank()) {
		       
		        this.schedulingPeriodID = "UNKNOWN";
		    }
		} else {
		    this.schedulingPeriodID = "UNKNOWN";
		}


		// Parse all relevant child nodes
		for (int i = 0; i < children.getLength(); i++) {
			final Node n = children.item(i);
			final String name = n.getNodeName();

			if (name.equals("StartDate")) {
				this.startDate = n.getTextContent();
			} else if (name.equals("EndDate")) {
				this.endDate = n.getTextContent();
			} else if (name.equals("ShiftTypes")) {
				for (int shiftNo = 0; shiftNo < n.getChildNodes().getLength(); shiftNo++) {
					addShift(n.getChildNodes().item(shiftNo));
				}
			} else if (name.equals("Contracts")) {
				for (int contractNo = 0; contractNo < n.getChildNodes().getLength(); contractNo++) {
					addContract(n.getChildNodes().item(contractNo));
				}
			} else if (name.equals("Employees")) {
				for (int employeeNo = 0; employeeNo < n.getChildNodes().getLength(); employeeNo++) {
					addEmployee(n.getChildNodes().item(employeeNo));
				}
			} else if (name.equals("DayOffRequests")) {
				for(int dayOffNo = 0; dayOffNo < n.getChildNodes().getLength(); dayOffNo++) {
					addDayOffRequest(n.getChildNodes().item(dayOffNo));
				
					
				}
			} else if (name.equals("ShiftOffRequests")) {
				for(int shiftOffNo = 0; shiftOffNo < n.getChildNodes().getLength(); shiftOffNo++) {
					addShiftOffRequest(n.getChildNodes().item(shiftOffNo));
				}
			} else if (name.equals("Skills")) {
				for (int skillNo = 0; skillNo < n.getChildNodes().getLength(); skillNo++) {
					addSkill(n.getChildNodes().item(skillNo));
				}
			} else if (name.equals("CoverRequirements")) {
				for (int crNo = 0; crNo < n.getChildNodes().getLength(); crNo++) {
					addCoverRequirement(n.getChildNodes().item(crNo));
				}
			}
		}
		for (Employee employee : employees) {
			for (Skill skill : employee.getSkills()) {
				System.out.println("employee " + employee.getEmployeeId() + "hat skill " +  skill);
			}
			
			
		}
	}

	private void addCoverRequirement(final Node coverRequirement) {
		if (coverRequirement.getNodeName().equals("#text")) {
			return;
		}

		String day = null;
		final Map<String, Integer> dayReqs = new HashMap<String, Integer>();
		final NodeList children = coverRequirement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node n = children.item(i);
			if (n.getNodeName().equals("Day")) {
				day = n.getChildNodes().item(0).getNodeValue();
			} else if (n.getNodeName().equals("Cover")) {
				final String shiftId = n.getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
				final String preferred = n.getChildNodes().item(3).getChildNodes().item(0).getNodeValue();
				dayReqs.put(shiftId, Integer.valueOf(preferred));
			}

		}

		// Save found values to look-up data structure
		if (!this.coverRequirements.containsKey(day)) {
			this.coverRequirements.put(day, new HashMap<String, Integer>());
		}

		for (final String k : dayReqs.keySet()) {
			this.coverRequirements.get(day).put(translateShiftIdToName(k), Integer.valueOf(dayReqs.get(k)));
		}
	}

	// TODO: This method should use non-hard-coded values read from the XMI file
	private String translateShiftIdToName(final String shiftId) {
		switch (shiftId) {
		case "E", "Early", "Early shift type": {
			//return "Early";
			return "E";
		}
		case "L", "Late", "Late shift type": {
			//return "Late";
			return "L";
		}
		case "D", "Day", "Day shift", "Day shift type": {
			//return "Day shift";
			return "D";
		}
		case "N", "Night", "Night shift": {
			//return "Night";
			return "N";
		}
		case "DH", "Head nurse", "Head nurse day shift type", "Head Nurse Day Shift": {
			//return "Head Nurse Day";
			return "DH";
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + shiftId);
		}
	}
	
	private int translateShiftNameToShiftTypeId(final String shiftName) {
		switch (shiftName) {
		case "Early", "Early shift type", "E": {
			return 1;
		}
		case "Late", "Late shift type", "L": {
			return 3;
		}
		case "Day shift", "Day", "Day shift type", "D": {
			return 2;
		}
		case "Night", "Night shift", "Night shift type", "N": {
			return 0;
		}
		case "Head nurse", "Head nurse day shift type", "Head Nurse Day Shift", "DH": {
			return 4;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + shiftName);
		}
	}
	
	
	private void addSkill(final Node skill) {
		final Skill s = modelFactory.createSkill();

		// if shift is not a skill type, skip it
		if (skill.getNodeName().equals("#text")) {
			return;
		}

		// parse the name attribute of the shift
		final NodeList children = skill.getChildNodes();
		s.setName(children.item(0).getNodeValue());
		this.skills.add(s);
	}

	private void addEmployee(final Node employee) {
		final Employee e = modelFactory.createEmployee();

		if (employee.getNodeName().equals("#text")) {
			return;
		}

		e.setEmployeeId(Integer.valueOf(employee.getAttributes().item(0).getNodeValue()));

		
		String contract = "";

		// parse attributes of the shift
		final NodeList children = employee.getChildNodes();
		Set<String> tmpSkills = new LinkedHashSet<>();
		for (int i = 0; i < children.getLength(); i++) {
			final Node c = children.item(i);
			final String name = c.getNodeName();

			if (name.equals("ContractID")) {
				contract = c.getChildNodes().item(0).getNodeValue();
			} else if (name.equals("Name")) {
				e.setName(c.getChildNodes().item(0).getNodeValue());
			} else if (name.equals("Skills")) {
				NodeList kids = c.getChildNodes();
			    for (int s = 0; s < kids.getLength(); s++) {
			        Node k = kids.item(s);
			        if (k.getNodeType() == Node.ELEMENT_NODE && "Skill".equals(k.getNodeName())) {
			            String skill = k.getTextContent().trim();  // NICHT getNodeValue()
			            if (!skill.isEmpty()) {
			                tmpSkills.add(skill);
			            }
				}
			}
			    
		}

		this.employees.add(e);
		this.employee2Contract.put(e.getName(), contract);}
		if (e.getName() != null && !e.getName().isEmpty() && !tmpSkills.isEmpty()) {
		    employeeHasSkill
		        .computeIfAbsent(e.getName(), k -> new HashSet<>())
		        .addAll(tmpSkills);}
	}
	
	public String getSchedulingPeriodID() {
	    return schedulingPeriodID;
	}
	
	private void addDayOffRequest(final Node dayOffRequest) {
		
		final DayOffRequest dor = modelFactory.createDayOffRequest();
		
		if(dayOffRequest.getNodeName().equals("#text"))
			return;
		
		 final Node weightAttr = dayOffRequest.getAttributes().getNamedItem("weight");
		    if (weightAttr != null) {
		        final String weightValue = weightAttr.getNodeValue();
		        dor.setWeight(Integer.valueOf(weightValue));
		    }
		
		final NodeList children = dayOffRequest.getChildNodes();
		
		for(int i = 0; i < children.getLength(); i++) {
			
			final Node d = children.item(i);
			
			final String name = d.getNodeName();
			
			if (name.equals("EmployeeID")) {
				final String value = d.getChildNodes().item(0).getNodeValue();
				dor.setEmployeeId(Integer.valueOf(value));
			}
			
			else if (name.equals("Date")) {
				final String value = d.getChildNodes().item(0).getNodeValue();
				dor.setDate(value);
				dor.setDateNumber(translateDateToNumber(value));
			}
		}
		this.dayOffRequests.add(dor);
	}

private void addShiftOffRequest(final Node shiftOffRequest) {
		
		final ShiftOffRequest sor = modelFactory.createShiftOffRequest();
		
		if(shiftOffRequest.getNodeName().equals("#text")) {
			return;
		}
			
		 final Node weightAttr = shiftOffRequest.getAttributes().getNamedItem("weight");
		    if (weightAttr != null) {
		        final String weightValue = weightAttr.getNodeValue();
		        sor.setWeight(Integer.valueOf(weightValue));
		    }
		
		final NodeList children = shiftOffRequest.getChildNodes();
		
		for(int i = 0; i < children.getLength(); i++) {
		
			final Node s = children.item(i);
			
			final String name = s.getNodeName();
			
			if (name.equals("ShiftTypeID")) {
				final String value = s.getChildNodes().item(0).getNodeValue();
				sor.setShiftTypeID(translateShiftNameToShiftTypeId(translateShiftIdToName(value)));
				
			}
			
			else if (name.equals("EmployeeID")) {
				final String value = s.getChildNodes().item(0).getNodeValue();
				sor.setEmployeeID(Integer.valueOf(value));
			}
			
			else if (name.equals("Date")) {
				
				final String value = s.getChildNodes().item(0).getNodeValue();
				sor.setDate(value);
				sor.setDateNumber(translateDateToNumber(value));
			}
		}
		this.shiftOffRequests.add(sor);
	}

	
	private void addContract(final Node contract) {
		// TODO: Some of the fields will not be parsed with the current state of this
		// method's implementation.
		final Contract con = modelFactory.createContract();
		

		if (contract.getNodeName().equals("#text")) {
			return;
		}

		con.setName(contract.getAttributes().item(0).getNodeValue());

		final NodeList children = contract.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node c = children.item(i);
			final String name = c.getNodeName();

			if (name.equals("Description")) {
				// skipped for now
			} else if (name.equals("SingleAssignmentPerDay")) {
				// skipped for now
			} else if (name.equals("MaxNumAssignments")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setMaximumNoOfAssignments(Integer.valueOf(value));
				
				final Node weightAttr = c.getAttributes().getNamedItem("weight");
			    if (weightAttr != null) {
			        final String weightValue = weightAttr.getNodeValue();
			        con.setMaxNoOfAssignmentsWeight(Integer.valueOf(weightValue));
			    }
			} else if (name.equals("MinNumAssignments")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setMinimumNoOfAssignments(Integer.valueOf(value));
				
				final Node weightAttr = c.getAttributes().getNamedItem("weight");
			    if (weightAttr != null) {
			        final String weightValue = weightAttr.getNodeValue();
			        con.setMinNoOfAssignmentsWeight(Integer.valueOf(weightValue));
			    }
			} else if (name.equals("MaxConsecutiveWorkingDays")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setMaximumNoOfConsWorkDays(Integer.valueOf(value));
			} else if (name.equals("MinConsecutiveWorkingDays")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setMinimumNoOfConsWorkDays(Integer.valueOf(value));
			} else if (name.equals("MaxConsecutiveFreeDays")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setMaximumNoOfConsFreeDays(Integer.valueOf(value));
			} else if (name.equals("MinConsecutiveFreeDays")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setMinimumNoOfConsFreeDays(Integer.valueOf(value));
			} else if (name.equals("MaxConsecutiveWorkingWeekends")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setMaximumNoOfConsWorkWeekends(Integer.valueOf(value));
			} else if (name.equals("MinConsecutiveWorkingWeekends")) {
				// skipped for now
			} else if (name.equals("MaxWorkingWeekendsInFourWeeks")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setMaximumNoOfWorkWeekendsInFourWeeks(Integer.valueOf(value));
				
				final Node weightAttr = c.getAttributes().getNamedItem("weight");
			    if (weightAttr != null) {
			        final String weightValue = weightAttr.getNodeValue();
			        con.setMaxNoOfWorkWeekendsInFourWeeksWeight(Integer.valueOf(weightValue));
			    }
			} else if (name.equals("WeekendDefinition")) {
				// skipped for now
			} else if (name.equals("CompleteWeekends")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setCompleteWeekends(value.equals("true"));
				
				final Node weightAttr = c.getAttributes().getNamedItem("weight");
			    if (weightAttr != null) {
			        final String weightValue = weightAttr.getNodeValue();
			        con.setCompleteWeekendsWeight(Integer.valueOf(weightValue));
			    }
			} else if (name.equals("IdenticalShiftTypesDuringWeekend")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setIdenticalShiftTypesDuringTheWeekend(value.equals("true"));
				
				final Node weightAttr = c.getAttributes().getNamedItem("weight");
			    if (weightAttr != null) {
			        final String weightValue = weightAttr.getNodeValue();
			        con.setIdenticalShiftTypesDuringWeekendWeight(Integer.valueOf(weightValue));
			    }
			} else if (name.equals("NoNightShiftBeforeFreeWeekend")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setNoNightShiftBeforeFreeWeekend(value.equals("true"));
				
				final Node weightAttr = c.getAttributes().getNamedItem("weight");
			    if (weightAttr != null) {
			        final String weightValue = weightAttr.getNodeValue();
			        con.setNoNightShiftBeforeFreeWeekendWeight(Integer.valueOf(weightValue));
			    }
			} else if (name.equals("AlternativeSkillCategory")) {
				// skipped for now
			} else if (name.equals("UnwantedPatterns")) {
				// skipped for now
			}
		}

		this.contracts.add(con);
	}

	private void addShift(final Node shift) {
		final Shift s = modelFactory.createShift();

		// if shift is not a shift type, skip it
		if (shift.getNodeName().equals("#text")) {
			return;
	}
		s.setName(shift.getAttributes().getNamedItem("ID").getNodeValue());
		// parse attributes of the shift
		final NodeList children = shift.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node c = children.item(i);
			final String name = c.getNodeName();

			if (name.equals("StartTime")) {
				final String start = c.getChildNodes().item(0).getNodeValue();
				s.setStartTime(Integer.valueOf(start.replaceAll(":", "")));
			} else if (name.equals("EndTime")) {
				final String end = c.getChildNodes().item(0).getNodeValue();
				s.setEndTime(Integer.valueOf(end.replaceAll(":", "")));
			} else if (name.equals("Description")) {
				//s.setName(c.getChildNodes().item(0).getNodeValue());
			} else if (name.equals("Skills")) {
				for (int skillNo = 0; skillNo < c.getChildNodes().getLength(); skillNo++) {
					if (c.getChildNodes().item(skillNo).getNodeName().equals("#text")) {
						continue;
					}

					final Node skill = c.getChildNodes().item(skillNo);
					if (!this.shiftNeedsSkill.containsKey(s.getName())) {
						this.shiftNeedsSkill.put(s.getName(), new HashSet<>());
					}
					this.shiftNeedsSkill.get(s.getName()).add(skill.getChildNodes().item(0).getNodeValue());
				}
			}
		}

		// add newly created model object to set of created shifts
		this.shifts.add(s);
	}

	public Root transform() {
		resolveSkillToShiftMappings();
		createShiftTypes();
		createDays(startDate, endDate);
		resolveSkillToEmployeeMappings();
		resolveEmployeeToContractMappings();

		final Root root = modelFactory.createRoot();
		root.setName("Hospital");

		root.getSkills().addAll(skills);

		root.setStartDate(startDate);
		root.setEndDate(endDate);

		root.getDays().addAll(days);
//		root.getShifts().addAll(shifts);
		root.getShiftTypes().addAll(shiftTypes.values());
		root.getEmployees().addAll(employees);
		root.getContracts().addAll(contracts);
		root.getDayOffRequests().addAll(dayOffRequests);
		root.getShiftOffRequests().addAll(shiftOffRequests);
		return root;
	}

	private void createShiftTypes() {
		shifts.forEach(shift -> {
			final ShiftType st = modelFactory.createShiftType();
			st.setName(shift.getName());
			//TODO: check if ShiftTypeId is set correctly
			st.setShiftTypeId(translateShiftNameToShiftTypeId(shift.getName()));
			this.shiftTypes.put(shift.getName(), st);
			 
			
			
		});
	}

	private void createDays(final String startDate, final String endDate) {
		// Create all days from start date to end date (both inclusive)
		String currentDate = startDate;
		while (currentDate != null && !currentDate.equals(incrementDate(endDate))) {
			final Day day = modelFactory.createDay();
			day.setDate(currentDate);
			day.setDayOfWeek(findDayOfWeek(currentDate));
			day.setDayOfWeekNumeric(dayNameToNumeric(day.getDayOfWeek()));
			day.setName(currentDate);
			day.setNumber(translateDateToNumber(currentDate));
			currentDate = incrementDate(currentDate);

			// Add all cover requirements
			this.coverRequirements.get(findDayOfWeek(day.getDate())).forEach((k, v) -> {
				final CoverRequirement cv = modelFactory.createCoverRequirement();
				// Set preferred
				cv.setPreferred(v);

				// Set shift
				final String shiftName = translateShiftIdToName(k);
				final Shift shift = cloneShift(getShift(shiftName));
				shift.setDay(day);
				cv.setShift(shift);

				// Set type of the shift
				shift.setType(shiftTypes.get(shiftName));

				// Save to day
				day.getRequirements().add(cv);
			});

			this.days.add(day);
		}
	}

	private Shift cloneShift(final Shift shift) {
		final Shift newShift = modelFactory.createShift();
		newShift.setName(shift.getName());
		newShift.setStartTime(shift.getStartTime());
		newShift.setEndTime(shift.getEndTime());
		newShift.getSkills().addAll(shift.getSkills());
		newShift.getAssignedEmployees().addAll(shift.getAssignedEmployees());
		return newShift;
	}

	private String incrementDate(final String dateString) {
		return LocalDate.parse(dateString).plusDays(1).toString();
	}
	
	private int translateDateToNumber(final String datestring) {
		 try {
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            Date date = sdf.parse(datestring);

	            Calendar cal = Calendar.getInstance();
	            cal.setTime(date);

	            return cal.get(Calendar.DAY_OF_YEAR);
	        } catch (ParseException e) {
	            throw new IllegalArgumentException("Ungültiges Datumsformat: " + datestring, e);
	        }
	}

	private String findDayOfWeek(final String dateString) {
		final Calendar cal = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-M-dd").parse(dateString);
		} catch (final ParseException e) {
			e.printStackTrace();
		}

		cal.setTime(date);
		final int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		switch (dayOfWeek) {
		case 2: {
			return "Monday";
		}
		case 3: {
			return "Tuesday";
		}
		case 4: {
			return "Wednesday";
		}
		case 5: {
			return "Thursday";
		}
		case 6: {
			return "Friday";
		}
		case 7: {
			return "Saturday";
		}
		case 1: {
			return "Sunday";
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + dayOfWeek);
		}
	}

	private int dayNameToNumeric(final String dayName) {
		switch (dayName) {
		case "Monday": {
			return 1;
		}
		case "Tuesday": {
			return 2;
		}
		case "Wednesday": {
			return 3;
		}
		case "Thursday": {
			return 4;
		}
		case "Friday": {
			return 5;
		}
		case "Saturday": {
			return 6;
		}
		case "Sunday": {
			return 7;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + dayName);
		}
	}

	private void resolveSkillToShiftMappings() {
		this.shifts.forEach(shift -> {
			final Set<String> skillNames = shiftNeedsSkill.get(shift.getName());
			for (final String skillName : skillNames) {
				final Skill skill = getSkill(skillName);
				shift.getSkills().add(skill);
			}
		});
	}

	private void resolveSkillToEmployeeMappings() {
		this.employees.forEach(employee -> {
			final Set<String> skillNames = employeeHasSkill.get(employee.getName());
			for (final String skillName : skillNames) {
				final Skill skill = getSkill(skillName);
				employee.getSkills().add(skill);
			}
		});
	}

	private void resolveEmployeeToContractMappings() {
		this.employees.forEach(employee -> {
			final String contractName = employee2Contract.get(employee.getName());
			final Contract contract = getContract(contractName);
			employee.setContract(contract);
		});
	}

	private Contract getContract(final String contractName) {
		final Iterator<Contract> it = contracts.iterator();
		while (it.hasNext()) {
			final Contract c = it.next();
			if (c.getName() != null && c.getName().equals(contractName)) {
				return c;
			}
		}
		return null;
	}

	private Skill getSkill(final String name) {
		final Iterator<Skill> it = skills.iterator();
		while (it.hasNext()) {
			final Skill s = it.next();
			if (s.getName() != null && s.getName().equals(name)) {
				return s;
			}
		}
		return null;
	}

	private Shift getShift(final String name) {
		final Iterator<Shift> it = shifts.iterator();
		while (it.hasNext()) {
			final Shift s = it.next();
			if (s.getName() != null && s.getName().equals(name)) {
				return s;
			}
		}
		return null;
	}

	public static void save(final Root model, final String path) throws IOException {
		// TODO: Checks

		final URI uri = URI.createFileURI(path);
		final ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SmartEMFResourceFactoryImpl("../"));
		rs.getPackageRegistry().put(NurserosteringmodelPackage.eNS_URI, NurserosteringmodelPackage.eINSTANCE);
		final Resource r = rs.createResource(uri);
		r.getContents().add(model);
		r.save(null);
	}

}
