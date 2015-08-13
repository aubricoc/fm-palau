package cat.aubricoc.palaudenoguera.festamajor.model;

import java.util.List;

public class Day {

	private String date;

	private List<Event> events;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
}
