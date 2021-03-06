package cat.aubricoc.palaudenoguera.festamajor.adapter;

import android.content.Context;
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
import cat.aubricoc.palaudenoguera.festamajor2017.R;

public class ScheduleDaysListAdapter {

	private final ViewGroup container;
	private final List<Day> days;
	private Context context;

	public ScheduleDaysListAdapter(Context context, ViewGroup container, List<Day> days) {
		this.context = context;
		this.container = container;
		this.days = days;
		notifyDataSetChanged();
	}

	private void notifyDataSetChanged() {
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

	private View onCreateView(ViewGroup parent) {
		return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_day_schedule, parent, false);
	}

	private void onBindViewHolder(Holder holder, Day day, int position) {
		holder.card.setCardBackgroundColor(Utils.getComplementaryColor(context, position));
		holder.date.setText(day.getDate());
		holder.events.clear();
		holder.events.addAll(day.getEvents());
		holder.eventsAdapter.notifyDataSetChanged();
	}

	class Holder {

		final CardView card;

		final TextView date;

		final ScheduleEventsListAdapter eventsAdapter;

		final List<Event> events = new ArrayList<>();

		Holder(View view) {
			card = (CardView) view.findViewById(R.id.schedule_card);
			date = (TextView) view.findViewById(R.id.schedule_date);

			ViewGroup showsView = (ViewGroup) view.findViewById(R.id.schedule_events);
			eventsAdapter = new ScheduleEventsListAdapter(context, showsView, events);
		}
	}
}
