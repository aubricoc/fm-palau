package cat.aubricoc.palaudenoguera.festamajor.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.canteratech.androidutils.Activity;

import java.util.Date;
import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.dao.TwitterUserDao;
import cat.aubricoc.palaudenoguera.festamajor.model.DataContainer;
import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;
import cat.aubricoc.palaudenoguera.festamajor.model.TwitterUser;
import cat.aubricoc.palaudenoguera.festamajor.task.LoadImageViewAsyncTask;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class TwitterListAdapter extends RecyclerView.Adapter<TwitterListAdapter.Holder> {

	private List<Tweet> tweets;

	public TwitterListAdapter(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	@Override
	public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tweet, parent, false);
		return new Holder(view);
	}

	@Override
	public void onBindViewHolder(Holder holder, int position) {
		final Tweet tweet = tweets.get(position);
		Drawable image = tweet.getImage();
		if (image == null) {
			if (DataContainer.getUserTwitterImages().isEmpty()) {
				DataContainer.prepareTwitterUserImages(Activity.CURRENT_CONTEXT);
			}
			image = DataContainer.getUserTwitterImages().get(tweet.getAlias());
		}

		if (image == null) {
			new LoadImageViewAsyncTask(holder.userImage, new LoadImageViewAsyncTask.OnReceivedImageListener() {
				@Override
				public void onReceivedImage(byte[] image, Drawable drawable) {
					TwitterUser twitterUser = new TwitterUser();
					twitterUser.setAlias(tweet.getAlias());
					twitterUser.setImage(image);
					TwitterUserDao.getInstance().createIfNotExists(twitterUser);
					tweet.setImage(drawable);
					DataContainer.getUserTwitterImages().put(tweet.getAlias(), drawable);
				}
			}).execute(tweet.getUserImage());
		} else {
			tweet.setImage(image);
			holder.userImage.setVisibility(View.VISIBLE);
			holder.userImage.setImageDrawable(tweet.getImage());
		}

		holder.user.setText(tweet.getUser());
		holder.userAlias.setText(tweet.getAlias());
		holder.message.setText(Html.fromHtml(tweet.getMessage()));
		holder.date.setText(getDateDifferenceFromNow(tweet.getDate()));

		holder.card.setTag(tweet);
	}

	@Override
	public int getItemCount() {
		return tweets.size();
	}

	private String getDateDifferenceFromNow(Date date) {
		String result;
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
			result = DateFormat.getDateFormat(Activity.CURRENT_CONTEXT).format(date);
		}
		return result;
	}

	class Holder extends RecyclerView.ViewHolder {

		CardView card;

		TextView date;

		TextView message;

		TextView userAlias;

		TextView user;

		ImageView userImage;

		public Holder(View view) {
			super(view);
			card = (CardView) view.findViewById(R.id.tweet_card);
			userImage = (ImageView) view.findViewById(R.id.tweet_user_image);
			user = (TextView) view.findViewById(R.id.tweet_user);
			userAlias = (TextView) view.findViewById(R.id.tweet_user_alias);
			message = (TextView) view.findViewById(R.id.tweet_message);
			date = (TextView) view.findViewById(R.id.tweet_date);
			card.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Tweet tweet = (Tweet) v.getTag();
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweet.getLink()));
					Activity.CURRENT_CONTEXT.startActivity(intent);
				}
			});
		}
	}
}
