package cat.aubricoc.palaudenoguera.festamajor.model;

import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cat.aubricoc.palaudenoguera.festamajor.dao.TwitterUserDao;

public class DataContainer {

	private static final Map<String, Drawable> userTwitterImages = new HashMap<>();

	private static List<Tweet> tweets = new ArrayList<>();

	public static List<Tweet> getTweets() {
		return tweets;
	}

	public static void setTweets(List<Tweet> tweetList) {
		tweets = tweetList;
	}

	public static Map<String, Drawable> getUserTwitterImages() {
		return userTwitterImages;
	}

	public static void prepareTwitterUserImages() {
		List<TwitterUser> twitterUsers = TwitterUserDao.getInstance().getAll();
		for (TwitterUser user : twitterUsers) {
			Drawable drawable = Drawable.createFromStream(new ByteArrayInputStream(user.getImage()), null);
			userTwitterImages.put(user.getAlias(), drawable);
		}
	}
}
