package pta.scenario;

import java.util.LinkedList;
import java.util.List;

public class ValidationLogger {
	private List<ValidationEvent> events = new LinkedList<>();
	private boolean errors = false;

	public void addInfo(final String msg) {
		events.add(new ValidationEvent(EventType.INFO, msg));
	}

	public void addError(final String msg) {
		events.add(new ValidationEvent(EventType.ERROR, msg));
		errors = true;
	}

	public boolean hasErrors() {
		return errors;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		events.forEach(e -> sb.append(e.toString() + "\n"));
		return sb.toString();
	}
}

class ValidationEvent {

	public final EventType type;
	public final String message;

	public ValidationEvent(final EventType type, final String message) {
		this.type = type;
		this.message = message;
	}

	@Override
	public String toString() {
		return type + ": " + message;
	}

}

enum EventType {
	INFO("Info"), ERROR("Error");

	public final String description;

	private EventType(final String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}
}
