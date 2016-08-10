package cat.aubricoc.palaudenoguera.festamajor.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canteratech.androidutils.Activity;

import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.model.Show;
import cat.aubricoc.palaudenoguera.festamajor2016.R;

class ScheduleShowsListAdapter {

	private final ViewGroup container;

	private final List<Show> shows;

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

	private View onCreateView(ViewGroup parent) {
		return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_show_schedule, parent, false);
	}

	private void onBindViewHolder(Holder holder, Show show) {
		holder.name.setText(show.getName());
	}

	class Holder {

		final TextView name;

		Holder(View view) {
			name = (TextView) view;
			Typeface font = Typeface.createFromAsset(Activity.CURRENT_CONTEXT.getAssets(), "PermanentMarker.ttf");
			name.setTypeface(font);
		}
	}
}
