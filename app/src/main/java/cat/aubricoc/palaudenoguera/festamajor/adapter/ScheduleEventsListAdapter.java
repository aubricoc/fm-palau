package cat.aubricoc.palaudenoguera.festamajor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.model.Event;
import cat.aubricoc.palaudenoguera.festamajor.model.Show;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

class ScheduleEventsListAdapter {

	private final ViewGroup container;

	private final List<Event> events;

	public ScheduleEventsListAdapter(ViewGroup container, List<Event> events) {
		this.container = container;
		this.events = events;
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
		container.removeAllViews();
		for (Event event : events) {
			View view = onCreateView(container);
			Holder holder = new Holder(view);
			onBindViewHolder(holder, event);
			container.addView(view);
		}
	}

	private View onCreateView(ViewGroup parent) {
		return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_event_schedule, parent, false);
	}

	private void onBindViewHolder(Holder holder, Event event) {
		holder.time.setText(event.getTime());
		holder.name.setText(event.getName());
		holder.description.setText(event.getDescription());
		holder.shows.clear();
		holder.shows.addAll(event.getShows());
		holder.showsAdapter.notifyDataSetChanged();
	}

	class Holder {

		final TextView time;

		final TextView name;

		final TextView description;

		final ScheduleShowsListAdapter showsAdapter;

		final List<Show> shows = new ArrayList<>();

		Holder(View view) {
			time = (TextView) view.findViewById(R.id.schedule_event_time);
			name = (TextView) view.findViewById(R.id.schedule_event_name);
			description = (TextView) view.findViewById(R.id.schedule_event_description);

			ViewGroup showsView = (ViewGroup) view.findViewById(R.id.schedule_event_shows);
			showsAdapter = new ScheduleShowsListAdapter(showsView, shows);
		}
	}
}
