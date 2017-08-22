package cat.aubricoc.palaudenoguera.festamajor.model;

import java.util.ArrayList;
import java.util.List;

public class Event {

	private String time;

	private String name;

	private String description;

	private List<Show> shows = new ArrayList<>();

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Show> getShows() {
		return shows;
	}

	public void setShows(List<Show> shows) {
		this.shows = shows;
	}
}
