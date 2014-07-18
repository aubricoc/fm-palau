package cat.aubricoc.palaudenoguera.festamajor.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

public class DataContainer {

	private static List<Tweet> tweets = new ArrayList<Tweet>();

	private static List<Photo> photos;

	private static Map<String, Drawable> userImages = new HashMap<String, Drawable>();

	public static List<Tweet> getTweets() {
		return tweets;
	}

	public static void setTweets(List<Tweet> tweetList) {
		tweets = tweetList;
	}

	public static List<Photo> getPhotos() {
		return photos;
	}

	public static Map<String, Drawable> getUserImages() {
		return userImages;
	}

	public static void preparePhotos(Context context) {
		if (photos != null) {
			return;
		}
		photos = new ArrayList<Photo>();
		try {
			Field[] fields = R.drawable.class.getFields();
			for (Field field : fields) {
				if (field.getName().startsWith("img_")) {
					photos.add(new Photo((Integer) field.get(null), field
							.getName(), context));
				}
			}for (Field field : fields) {
				if (field.getName().startsWith("img_")) {
					photos.add(new Photo((Integer) field.get(null), field
							.getName(), context));
				}
			}for (Field field : fields) {
				if (field.getName().startsWith("img_")) {
					photos.add(new Photo((Integer) field.get(null), field
							.getName(), context));
				}
			}for (Field field : fields) {
				if (field.getName().startsWith("img_")) {
					photos.add(new Photo((Integer) field.get(null), field
							.getName(), context));
				}
			}for (Field field : fields) {
				if (field.getName().startsWith("img_")) {
					photos.add(new Photo((Integer) field.get(null), field
							.getName(), context));
				}
			}
			Collections.sort(photos);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e);
		}
	}
}
