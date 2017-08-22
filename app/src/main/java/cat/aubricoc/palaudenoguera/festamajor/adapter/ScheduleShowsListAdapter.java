package cat.aubricoc.palaudenoguera.festamajor.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.model.Show;
import cat.aubricoc.palaudenoguera.festamajor2017.R;

class ScheduleShowsListAdapter {

	private final ViewGroup container;
	private final List<Show> shows;
	private Context context;

	public ScheduleShowsListAdapter(Context context, ViewGroup container, List<Show> shows) {
		this.context = context;
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
		holder.name.setText(Html.fromHtml(show.getName()));
	}

	class Holder {

		final TextView name;

		Holder(View view) {
			name = (TextView) view;
			Typeface font = Typeface.createFromAsset(context.getAssets(), "PermanentMarker.ttf");
			name.setTypeface(font);
		}
	}
}
