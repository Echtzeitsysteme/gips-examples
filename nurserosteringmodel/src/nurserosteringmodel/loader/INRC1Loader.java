package nurserosteringmodel.loader;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import nurserosteringmodel.Employee;
import nurserosteringmodel.NurserosteringmodelFactory;
import nurserosteringmodel.NurserosteringmodelPackage;
import nurserosteringmodel.Root;
import nurserosteringmodel.Shift;
import nurserosteringmodel.Skill;

public class INRC1Loader {

	private NurserosteringmodelFactory modelFactory = NurserosteringmodelFactory.eINSTANCE;
	private String startDate;
	private String endDate;
	private Set<Shift> shifts = new HashSet<Shift>();
	private Set<Skill> skills = new HashSet<Skill>();

	private Map<String, Set<String>> shiftNeedsSkill = new HashMap<>();
	private Map<String, Set<String>> employeeHasSkill = new HashMap<>();

	private Set<Day> days = new HashSet<Day>();
	private Set<Employee> employees = new HashSet<Employee>();

	// Map $dayName -> "shiftName" -> preferred no of employees
	private Map<String, Map<String, Integer>> coverRequirements = new HashMap<String, Map<String, Integer>>();

	private Map<String, String> employee2Contract = new HashMap<>();

	private Set<Contract> contracts = new HashSet<Contract>();

	public static void main(final String[] args) {
		final INRC1Loader loader = new INRC1Loader();
		final String projectFolder = System.getProperty("user.dir");
		try {
			loader.loadFromXmlFile(projectFolder + "/../nurserosteringmodel/resources/sprint/sprint01.xml");
		} catch (final ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		final Root root = loader.transform();
		try {
			save(root, "./resources/model.xmi");
		} catch (final IOException e) {
			e.printStackTrace();
		}
		System.out.println("=> Loaded INRC1 model from file.");
	}

	public void loadFromXmlFile(final String filepath) throws ParserConfigurationException, SAXException, IOException {
		if (filepath == null || filepath.isBlank()) {
			throw new IllegalArgumentException("Given file path was null or empty.");
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
				// TODO
			} else if (name.equals("ShiftOffRequests")) {
				// TODO
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
		case "E", "Early": {
			return "Early";
		}
		case "L", "Late": {
			return "Late";
		}
		case "D", "Day", "Day shift": {
			return "Day shift";
		}
		case "N", "Night": {
			return "Night";
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + shiftId);
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

		String contract = "";

		// parse attributes of the shift
		final NodeList children = employee.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node c = children.item(i);
			final String name = c.getNodeName();

			if (name.equals("ContractID")) {
				contract = c.getChildNodes().item(0).getNodeValue();
			} else if (name.equals("Name")) {
				e.setName(c.getChildNodes().item(0).getNodeValue());
			} else if (name.equals("Skills")) {
				final NodeList skills = c.getChildNodes().item(1).getChildNodes();
				for (int s = 0; s < skills.getLength(); s++) {
					final Node c2 = skills.item(s);
					final String skill = c2.getNodeValue();
					if (!this.employeeHasSkill.containsKey(e.getName())) {
						this.employeeHasSkill.put(e.getName(), new HashSet<String>());
					}
					this.employeeHasSkill.get(e.getName()).add(skill);
				}
			}
		}

		this.employees.add(e);
		this.employee2Contract.put(e.getName(), contract);
	}

	private void addContract(final Node contract) {
		// TODO
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
			} else if (name.equals("MinNumAssignments")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setMinimumNoOfAssignments(Integer.valueOf(value));
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
				con.setMaximumNoOfWorkWeekInFourWeeks(Integer.valueOf(value));
			} else if (name.equals("WeekendDefinition")) {
				// skipped for now
			} else if (name.equals("CompleteWeekends")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setCompleteWeekends(value.equals("true"));
			} else if (name.equals("IdenticalShiftTypesDuringWeekend")) {
				final String value = c.getChildNodes().item(0).getNodeValue();
				con.setIdenticalShiftTypesDuringTheWeekend(value.equals("true"));
			} else if (name.equals("NoNightShiftBeforeFreeWeekend")) {
				// skipped for now
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
				s.setName(c.getChildNodes().item(0).getNodeValue());
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
		root.getEmployees().addAll(employees);
		root.getContracts().addAll(contracts);

		return root;
	}

	private void createDays(final String startDate, final String endDate) {
		// Create all days from start date to end date (both inclusive)
		String currentDate = startDate;
		while (currentDate != null && !currentDate.equals(incrementDate(endDate))) {
			final Day day = modelFactory.createDay();
			day.setDate(currentDate);
			day.setDayOfWeek(findDayOfWeek(currentDate));
			day.setName(currentDate);
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
