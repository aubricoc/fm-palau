package cat.aubricoc.palaudenoguera.festamajor.adapter;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.model.Day;
import cat.aubricoc.palaudenoguera.festamajor.model.Event;
import cat.aubricoc.palaudenoguera.festamajor.utils.Utils;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class ScheduleDaysListAdapter {

	private ViewGroup container;

	private List<Day> days;

	public ScheduleDaysListAdapter(ViewGroup container, List<Day> days) {
		this.container = container;
		this.days = days;
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
		container.removeAllViews();
		int iter = 0;
		for (Day day : days) {
			View view = onCreateView(container);
			Holder holder = new Holder(view);
			onBindViewHolder(holder, day, iter);
			container.addView(view);
			iter++;
		}
	}

	public View onCreateView(ViewGroup parent) {
		return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_day_schedule, parent, false);
	}

	public void onBindViewHolder(Holder holder, Day day, int position) {
		holder.card.setCardBackgroundColor(Utils.getComplementaryColor(position));
		holder.date.setText(day.getDate());
		holder.events.clear();
		holder.events.addAll(day.getEvents());
		holder.eventsAdapter.notifyDataSetChanged();
	}

	class Holder {

		CardView card;

		TextView date;

		ScheduleEventsListAdapter eventsAdapter;

		List<Event> events = new ArrayList<>();

		Holder(View view) {
			card = (CardView) view.findViewById(R.id.schedule_card);
			date = (TextView) view.findViewById(R.id.schedule_date);

			ViewGroup showsView = (ViewGroup) view.findViewById(R.id.schedule_events);
			eventsAdapter = new ScheduleEventsListAdapter(showsView, events);
		}
	}
}
