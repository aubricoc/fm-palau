package cat.aubricoc.palaudenoguera.festamajor.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.adapter.ScheduleDaysListAdapter;
import cat.aubricoc.palaudenoguera.festamajor.model.Day;
import cat.aubricoc.palaudenoguera.festamajor.model.Event;
import cat.aubricoc.palaudenoguera.festamajor.model.Show;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class ScheduleFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_schedule, parent, false);

		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.schedule_list);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		ScheduleDaysListAdapter mAdapter = new ScheduleDaysListAdapter(readDays());
		recyclerView.setAdapter(mAdapter);

		return view;
	}

	private List<Day> readDays() {
		List<Day> days = new ArrayList<>();
		int iterDay = 0;
		boolean noMore = false;
		while (!noMore) {
			iterDay++;
			String dayString = getScheduleString("day_" + iterDay);
			if (dayString == null) {
				noMore = true;
			} else {
				Day day = new Day();
				day.setDate(dayString);
				day.setEvents(readEvents(iterDay));
				days.add(day);
			}
		}
		return days;
	}

	private List<Event> readEvents(int iterDay) {
		List<Event> events = new ArrayList<>();
		int iterEvent = 0;
		boolean noMore = false;
		while (!noMore) {
			iterEvent++;
			String timeString = getScheduleString("day_" + iterDay + "_event_" + iterEvent + "_time");
			if (timeString == null) {
				noMore = true;
			} else {
				Event event = new Event();
				event.setTime(timeString);
				event.setName(getScheduleString("day_" + iterDay + "_event_" + iterEvent + "_name"));
				event.setDescription(getScheduleString("day_" + iterDay + "_event_" + iterEvent + "_description"));
				event.setShows(readShows(iterDay, iterEvent));
				events.add(event);
			}
		}
		return events;
	}

	private List<Show> readShows(int iterDay, int iterEvent) {
		List<Show> shows = new ArrayList<>();
		int iterShow = 0;
		boolean noMore = false;
		while (!noMore) {
			iterShow++;
			String showString = getScheduleString("day_" + iterDay + "_event_" + iterEvent + "_show_" + iterShow);
			if (showString == null) {
				noMore = true;
			} else {
				Show show = new Show();
				show.setName(showString);
				shows.add(show);
			}
		}
		return shows;
	}

	private String getScheduleString(String key) {
		try {
			Field field = R.string.class.getField(key);
			int id = (int) field.get(null);
			return getString(id);
		} catch (NoSuchFieldException e) {
			return null;
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new IllegalStateException(e);
		}
	}
}