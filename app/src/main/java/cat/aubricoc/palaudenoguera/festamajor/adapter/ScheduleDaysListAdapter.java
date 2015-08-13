package cat.aubricoc.palaudenoguera.festamajor.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.model.Day;
import cat.aubricoc.palaudenoguera.festamajor.model.Event;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class ScheduleDaysListAdapter extends RecyclerView.Adapter<ScheduleDaysListAdapter.Holder> {

	private List<Day> days;

	public ScheduleDaysListAdapter(List<Day> days) {
		this.days = days;
	}

	@Override
	public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_day_schedule, parent, false);
		return new Holder(v);
	}

	@Override
	public void onBindViewHolder(Holder holder, int position) {
		Day day = days.get(position);
		holder.date.setText(day.getDate());
		holder.events.clear();
		holder.events.addAll(day.getEvents());
		holder.eventsAdapter.notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return days.size();
	}

	class Holder extends RecyclerView.ViewHolder {

		TextView date;

		ScheduleEventsListAdapter eventsAdapter;

		List<Event> events = new ArrayList<>();

		Holder(View view) {
			super(view);
			date = (TextView) view.findViewById(R.id.schedule_day);

			ViewGroup eventsView = (ViewGroup) view.findViewById(R.id.schedule_events);
			eventsAdapter = new ScheduleEventsListAdapter(eventsView, events);
		}
	}
}
