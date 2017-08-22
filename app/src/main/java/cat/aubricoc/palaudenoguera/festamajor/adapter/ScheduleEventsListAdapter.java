package cat.aubricoc.palaudenoguera.festamajor.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.model.Event;
import cat.aubricoc.palaudenoguera.festamajor.model.Show;
import cat.aubricoc.palaudenoguera.festamajor2017.R;

class ScheduleEventsListAdapter {

	private final ViewGroup container;
	private final List<Event> events;
	private Context context;

	public ScheduleEventsListAdapter(Context context, ViewGroup container, List<Event> events) {
		this.context = context;
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
		setText(holder.time, event.getTime(), "00:00");
		setText(holder.name, event.getName(), null);
		setText(holder.description, event.getDescription(), null);
		holder.shows.clear();
		holder.shows.addAll(event.getShows());
		holder.showsAdapter.notifyDataSetChanged();
	}

	private void setText(TextView textView, String text, String defaultText) {
		if (TextUtils.isEmpty(text)) {
			textView.setVisibility(TextUtils.isEmpty(defaultText) ? View.GONE : View.INVISIBLE);
			textView.setText(defaultText);
		} else {
			textView.setVisibility(View.VISIBLE);
			textView.setText(text);
		}
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
			showsAdapter = new ScheduleShowsListAdapter(context, showsView, shows);
		}
	}
}
