package cat.aubricoc.palaudenoguera.festamajor.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cat.aubricoc.palaudenoguera.festamajor.dao.InstagramUserDao;
import cat.aubricoc.palaudenoguera.festamajor.dao.TwitterUserDao;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class DataContainer {

	private static List<Tweet> tweets = new ArrayList<>();

	private static List<Instagram> instagrams = new ArrayList<>();

	private static List<Photo> photos;

	private static Map<String, Drawable> userTwitterImages = new HashMap<>();

	private static Map<String, Drawable> userInstagramImages = new HashMap<>();

	public static List<Tweet> getTweets() {
		return tweets;
	}

	public static void setTweets(List<Tweet> tweetList) {
		tweets = tweetList;
	}

	public static List<Instagram> getInstagrams() {
		return instagrams;
	}

	public static void setInstagrams(List<Instagram> instagramList) {
		instagrams = instagramList;
	}

	public static List<Photo> getPhotos() {
		return photos;
	}

	public static Map<String, Drawable> getUserTwitterImages() {
		return userTwitterImages;
	}

	public static Map<String, Drawable> getUserInstagramImages() {
		return userInstagramImages;
	}

	public static boolean preparePhotos(Context context) {
		if (photos != null) {
			return !photos.isEmpty();
		}
		photos = new ArrayList<>();
		try {
			Field[] fields = R.drawable.class.getFields();
			for (Field field : fields) {
				if (field.getName().startsWith("img_")) {
					photos.add(new Photo((Integer) field.get(null), field
							.getName(), context));
				}
			}
			if (photos.isEmpty()) {
				return false;
			}
			Collections.sort(photos);
			return true;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static void prepareTwitterUserImages(Context context) {
		List<TwitterUser> twitterUsers = TwitterUserDao.getInstance().getAll();
		for (TwitterUser user : twitterUsers) {
			Drawable drawable = Drawable.createFromStream(
					new ByteArrayInputStream(user.getImage()), null);
			userTwitterImages.put(user.getAlias(), drawable);
		}
	}

	public static void prepareInstagramUserImages(Context context) {
		List<InstagramUser> instagramUsers = InstagramUserDao.getInstance().getAll();
		for (InstagramUser user : instagramUsers) {
			Drawable drawable = Drawable.createFromStream(
					new ByteArrayInputStream(user.getImage()), null);
			userInstagramImages.put(user.getAlias(), drawable);
		}
	}
}
