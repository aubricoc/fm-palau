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

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.dao.InstagramDao;
import cat.aubricoc.palaudenoguera.festamajor.dao.InstagramUserDao;
import cat.aubricoc.palaudenoguera.festamajor.model.DataContainer;
import cat.aubricoc.palaudenoguera.festamajor.model.Instagram;
import cat.aubricoc.palaudenoguera.festamajor.model.InstagramUser;
import cat.aubricoc.palaudenoguera.festamajor.task.LoadImageViewAsyncTask;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class InstagramListAdapter extends RecyclerView.Adapter<InstagramListAdapter.Holder> {

	private List<Instagram> instagrams;

	public InstagramListAdapter(List<Instagram> instagrams) {
		this.instagrams = instagrams;
	}

	@Override
	public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_instagram, parent, false);
		return new Holder(view);
	}

	@Override
	public void onBindViewHolder(Holder holder, int position) {
		final Instagram instagram = instagrams.get(position);
		Drawable image = instagram.getImage();
		if (image == null) {
			if (DataContainer.getUserInstagramImages().isEmpty()) {
				DataContainer.prepareInstagramUserImages(Activity.CURRENT_CONTEXT);
			}
			image = DataContainer.getUserInstagramImages().get(instagram.getAlias());
		}

		if (image == null) {
			new LoadImageViewAsyncTask(holder.userImage, new LoadImageViewAsyncTask.OnReceivedImageListener() {
				@Override
				public void onReceivedImage(byte[] image, Drawable drawable) {
					InstagramUser instagramUser = new InstagramUser();
					instagramUser.setAlias(instagram.getAlias());
					instagramUser.setImage(image);
					InstagramUserDao.getInstance().createIfNotExists(instagramUser);
					instagram.setImage(drawable);
					DataContainer.getUserInstagramImages().put(instagram.getAlias(), drawable);
				}
			}).execute(instagram.getUserImage());
		} else {
			instagram.setImage(image);
			holder.userImage.setVisibility(View.VISIBLE);
			holder.userImage.setImageDrawable(instagram.getImage());
		}

		Drawable picture = instagram.getPictureThumbnailDrawable();
		if (picture == null && instagram.getPictureThumbnail() != null) {
			picture = Drawable.createFromStream(new ByteArrayInputStream(instagram.getPictureThumbnail()), null);
		}

		if (picture == null) {
			new LoadImageViewAsyncTask(holder.picture, new LoadImageViewAsyncTask.OnReceivedImageListener() {
				@Override
				public void onReceivedImage(byte[] image, Drawable drawable) {
					instagram.setPictureThumbnail(image);
					instagram.setPictureThumbnailDrawable(drawable);
					InstagramDao.getInstance().update(instagram);
				}
			}).execute(instagram.getPictureThumbnailUrl());
		} else {
			instagram.setPictureThumbnailDrawable(picture);
			holder.picture.setVisibility(View.VISIBLE);
			holder.picture.setImageDrawable(picture);
		}

		holder.user.setText(instagram.getUser());
		holder.userAlias.setText(instagram.getAlias());
		holder.message.setText(Html.fromHtml(instagram.getMessage()));
		holder.date.setText(getDateDifferenceFromNow(instagram.getDate()));

		holder.card.setTag(instagram);
	}

	@Override
	public int getItemCount() {
		return instagrams.size();
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

		ImageView picture;

		public Holder(View view) {
			super(view);
			card = (CardView) view.findViewById(R.id.instagram_card);
			userImage = (ImageView) view.findViewById(R.id.instagram_user_image);
			user = (TextView) view.findViewById(R.id.instagram_user);
			userAlias = (TextView) view.findViewById(R.id.instagram_user_alias);
			message = (TextView) view.findViewById(R.id.instagram_message);
			date = (TextView) view.findViewById(R.id.instagram_date);
			picture = (ImageView) view.findViewById(R.id.instagram_picture);
			card.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Instagram instagram = (Instagram) v.getTag();
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagram.getLink()));
					Activity.CURRENT_CONTEXT.startActivity(intent);
				}
			});
		}
	}
}
