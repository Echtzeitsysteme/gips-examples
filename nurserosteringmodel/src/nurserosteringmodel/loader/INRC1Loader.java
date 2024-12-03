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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import nurserosteringmodel.Day;
import nurserosteringmodel.NurserosteringmodelFactory;
import nurserosteringmodel.Root;
import nurserosteringmodel.Shift;
import nurserosteringmodel.Skill;

public class INRC1Loader {

	private NurserosteringmodelFactory modelFactory = NurserosteringmodelFactory.eINSTANCE;
	private String startDate;
	private String endDate;
	private Set<Shift> shifts = new HashSet<Shift>();
	private Set<Skill> skills = new HashSet<Skill>();
	private Map<String, String> shiftNeedsSkill = new HashMap<>();
	private Set<Day> days = new HashSet<Day>();

	public static void main(final String[] args) {
		final INRC1Loader loader = new INRC1Loader();
		try {
			loader.loadFromXmlFile("./resources/sprint/sprint01.xml");
		} catch (final ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		final Root root = loader.transform();
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
//			System.out.println(n.getNodeName());
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
			} else if (name.equals("CoverRequirements")) {

			} else if (name.equals("DayOffRequests")) {

			} else if (name.equals("ShiftOffRequests")) {

			} else if (name.equals("Skills")) {
				for (int skillNo = 0; skillNo < n.getChildNodes().getLength(); skillNo++) {
					addSkill(n.getChildNodes().item(skillNo));
				}
			} else if (name.equals("CoverRequirements")) {

			}
		}

//		System.out.println(children.getLength());
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

	}

	private void addContract(final Node contract) {

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
					this.shiftNeedsSkill.put(s.getName(), skill.getChildNodes().item(0).getNodeValue());
				}
			}
		}

		// add newly created model object to set of created shifts
		this.shifts.add(s);
	}

	public Root transform() {
		resolveSkillToShiftMappings();
		createDays(startDate, endDate);

		final Root root = modelFactory.createRoot();

		root.getSkills().addAll(skills);
		// TODO
//		root.getNurses().addAll();
//		root.getDays().addAll();

		root.setStartDate(startDate);
		root.setEndDate(endDate);

		root.getDays().addAll(days);

		return root;
	}

	private void createDays(final String startDate, final String endDate) {
		// Create all days from start date to end date (both inclusive)
		String currentDate = startDate;
		while (currentDate != null && !currentDate.equals(incrementDate(endDate))) {
			final Day day = modelFactory.createDay();
			day.setDate(currentDate);
			day.setDayOfWeek(findDayOfWeek(currentDate));
			currentDate = incrementDate(currentDate);
			// TODO: Add shifts
			this.days.add(day);
		}
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
			final String skillName = shiftNeedsSkill.get(shift.getName());
			final Skill skill = getSkill(skillName);
			shift.getSkills().add(skill);
		});

		this.shiftNeedsSkill.forEach((shift, skill) -> {

		});
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

}
