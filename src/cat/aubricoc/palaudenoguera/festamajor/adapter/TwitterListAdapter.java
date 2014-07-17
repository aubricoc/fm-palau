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

		ViewHolder holder;
		if (view == null) {

			view = layoutInflater.inflate(R.layout.list_item_tweet, null);
			holder = new ViewHolder();
			holder.userImage = (ImageView) view
					.findViewById(R.id.tweet_user_image);
			holder.user = (TextView) view.findViewById(R.id.tweet_user);
			holder.userAlias = (TextView) view
					.findViewById(R.id.tweet_user_alias);
			holder.message = (TextView) view.findViewById(R.id.tweet_message);
			holder.date = (TextView) view.findViewById(R.id.tweet_date);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if (tweet.getImage() == null) {
			new LoadImageViewAsyncTask(holder.userImage, tweet).execute(tweet
					.getUserImage());
		} else {
			holder.userImage.setImageDrawable(tweet.getImage());
		}

		holder.user.setText(tweet.getUser());
		holder.userAlias.setText(tweet.getAlias());
		holder.message.setText(Html.fromHtml(tweet.getMessage()));
		holder.date.setText(getDateDifferenceFromNow(tweet.getDate()));

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

	private class ViewHolder {
		TextView date;
		TextView message;
		TextView userAlias;
		TextView user;
		ImageView userImage;
	}
}
