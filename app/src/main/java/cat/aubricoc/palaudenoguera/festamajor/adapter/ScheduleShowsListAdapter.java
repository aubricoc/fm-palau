package cat.aubricoc.palaudenoguera.festamajor.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canteratech.androidutils.Activity;

import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.model.Show;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class ScheduleShowsListAdapter {

	private ViewGroup container;

	private List<Show> shows;

	public ScheduleShowsListAdapter(ViewGroup container, List<Show> shows) {
		this.container = container;
		this.shows = shows;
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
		container.removeAllViews();
		for (Show show : shows) {
			View view = onCreateView(container);
			Holder holder = new Holder(view);
			onBindViewHolder(holder, show);
			container.addView(view);
		}
	}

	public View onCreateView(ViewGroup parent) {
		return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_show_schedule, parent, false);
	}

	public void onBindViewHolder(Holder holder, Show show) {
		holder.name.setText(show.getName());
	}

	class Holder {

		TextView name;

		Holder(View view) {
			name = (TextView) view;
			Typeface font = Typeface.createFromAsset(Activity.CURRENT_CONTEXT.getAssets(), "PermanentMarker.ttf");
			name.setTypeface(font);
		}
	}
}
