package cat.aubricoc.palaudenoguera.festamajor.adapter;

import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;
import cat.aubricoc.palaudenoguera.festamajor.task.LoadImageViewAsyncTask;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

public class TwitterListAdapter extends ArrayAdapter<Tweet> {

	private LayoutInflater layoutInflater;

	public TwitterListAdapter(Context context, List<Tweet> tweets) {
		super(context, R.layout.list_item_tweet, tweets);
		this.layoutInflater = LayoutInflater.from(context);
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int iter, View view, ViewGroup parent) {

		Tweet tweet = getItem(iter);

		if (view == null) {
			view = layoutInflater.inflate(R.layout.list_item_tweet, null);

			new LoadImageViewAsyncTask(
					(ImageView) view.findViewById(R.id.tweet_user_image))
					.execute(tweet.getUserImage());

			((TextView) view.findViewById(R.id.tweet_user)).setText(tweet
					.getUser());

			((TextView) view.findViewById(R.id.tweet_user_alias)).setText(tweet
					.getAlias());

			((TextView) view.findViewById(R.id.tweet_message)).setText(Html
					.fromHtml(tweet.getMessage()));
		}

		((TextView) view.findViewById(R.id.tweet_date))
				.setText(getDateDifferenceFromNow(tweet.getDate()));

		return view;
	}

	private String getDateDifferenceFromNow(Date date) {
		String result = "";
		Date now = new Date();
		long diffSegs = (now.getTime() - date.getTime()) / 1000;
		if (diffSegs < 60) {
			result = "" + diffSegs;
			result += !result.equals("1") ? " segs" : " seg";
		} else if (diffSegs < 60 * 60) {
			result = "" + diffSegs / 60;
			result += !result.equals("1") ? " mins" : " min";
		} else if (diffSegs < 24 * 60 * 60) {
			result = "" + (diffSegs / (60 * 60));
			result += !result.equals("1") ? " hrs" : " hr";
		} else {
			result = DateFormat.getDateFormat(getContext()).format(date);
		}
		return result;
	}
}
