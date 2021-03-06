package cat.aubricoc.palaudenoguera.festamajor.service;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cat.aubricoc.palaudenoguera.festamajor.dao.TweetDao;
import cat.aubricoc.palaudenoguera.festamajor.exception.ConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.exception.TwitterConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;
import cat.aubricoc.palaudenoguera.festamajor.utils.Constants;
import cat.aubricoc.palaudenoguera.festamajor.utils.IOUtils;
import cat.aubricoc.palaudenoguera.festamajor.utils.Utils;
import cat.aubricoc.palaudenoguera.festamajor2017.R;

public class TwitterService {

	private static final String TOKEN_URL = "https://api.twitter.com/oauth2/token";

	private static final String SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json";

	private static final String URL_TWEET = "https://twitter.com/%s/status/%s";

	private final String query;

	private Context context;

	private TweetDao tweetDao;

	private TwitterService(Context context) {
		super();
		this.query = context.getString(R.string.twitter_query);
		this.context = context;
		this.tweetDao = TweetDao.newInstance(context);
	}

	public static TwitterService newInstance(Context context) {
		return new TwitterService(context);
	}

	public List<Tweet> getAll() {
		return tweetDao.getAll();
	}

	public List<Tweet> getNew() {
		Tweet firstTweet = tweetDao.getFirstResult();
		String sinceId = null;
		if (firstTweet != null) {
			sinceId = firstTweet.getId();
		}
		return getNew(sinceId);
	}

	private List<Tweet> getNew(String sinceId) {
		List<Tweet> tweets = search(sinceId, null);
		sinceId = null;
		for (Tweet tweet : tweets) {
			if (sinceId == null && tweet.isRetweet()) {
				sinceId = tweet.getId();
			}
		}
		tweets = saveTweets(tweets);

		if (tweets.isEmpty() && sinceId != null) {
			Log.i(Constants.PROJECT_NAME, "Only retweets. Searching more...");
			tweets = getNew(sinceId);
		}
		return tweets;
	}

	public List<Tweet> getOld() {
		Tweet lastTweet = tweetDao.getFirstResultOrderByIdAsc();
		String maxId = null;
		if (lastTweet != null) {
			maxId = lastTweet.getId();
		}
		return getOld(maxId);
	}

	private List<Tweet> getOld(String maxId) {
		List<Tweet> tweets = search(null, maxId);
		maxId = null;
		for (Tweet tweet : tweets) {
			if (tweet.isRetweet()) {
				maxId = tweet.getId();
			}
		}
		tweets = saveTweets(tweets);

		if (tweets.isEmpty() && maxId != null) {
			Log.i(Constants.PROJECT_NAME, "Only retweets. Searching more...");
			tweets = getOld(maxId);
		}
		return tweets;
	}

	private List<Tweet> saveTweets(List<Tweet> tweets) {
		List<Tweet> newTweets = new ArrayList<>();
		for (Tweet tweet : tweets) {
			if (!tweet.isRetweet() && tweetDao.createIfNotExists(tweet) > -1) {
				newTweets.add(tweet);
			}
		}
		return newTweets;
	}

	private List<Tweet> search(String sinceId, String maxId) {

		if (!Utils.isOnline(context)) {
			throw new ConnectionException(context);
		}

		try {
			Log.i(Constants.PROJECT_NAME, "Searching tweets '" + query + "'"
					+ (sinceId == null ? "" : ", sinceId=" + sinceId)
					+ (maxId == null ? "" : ", maxId=" + maxId));

			String encodedUrl = SEARCH_URL;
			encodedUrl += "?result_type=recent";
			if (sinceId != null) {
				encodedUrl += "&since_id=" + sinceId;
			}
			if (maxId != null) {
				encodedUrl += "&max_id=" + maxId;
			}
			encodedUrl += "&q=" + URLEncoder.encode(query, "UTF-8");

			String apiKey = context.getString(R.string.twitter_api_key);
			String apiSecret = context.getString(R.string.twitter_api_secret);

			String urlApiKey = URLEncoder.encode(apiKey, "UTF-8");
			String urlApiSecret = URLEncoder.encode(apiSecret, "UTF-8");

			String combined = urlApiKey + ":" + urlApiSecret;

			String base64Encoded = Base64.encodeToString(combined.getBytes(),
					Base64.NO_WRAP);

			URL url = new URL(TOKEN_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
			conn.setReadTimeout(Constants.SO_TIMEOUT);
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", "Basic " + base64Encoded);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write("grant_type=client_credentials");
			writer.flush();
			writer.close();
			os.close();

			String json = IOUtils.toString(conn.getInputStream());
			String accessToken = getAccessToken(json);

			url = new URL(encodedUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
			conn.setReadTimeout(Constants.SO_TIMEOUT);
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
			conn.setRequestProperty("Content-Type", "application/json");

			json = IOUtils.toString(conn.getInputStream());

			return parseTweets(json);
		} catch (Exception e) {
			throw new TwitterConnectionException(e, context);
		}
	}

	private String getAccessToken(String rawAuthorizationJson) {
		if (rawAuthorizationJson != null && rawAuthorizationJson.length() > 0) {
			try {
				JSONObject jsonObject = new JSONObject(rawAuthorizationJson);
				String token_type = jsonObject.getString("token_type");
				if (token_type.equals("bearer")) {
					return jsonObject.getString("access_token");
				}
			} catch (JSONException e) {
				throw new TwitterConnectionException(e, context);
			}
		}
		throw new TwitterConnectionException(context);
	}

	private List<Tweet> parseTweets(String json) {
		try {
			List<Tweet> tweets = new ArrayList<>();
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonMessages = jsonObject.getJSONArray("statuses");
			for (int iter = 0; iter < jsonMessages.length(); iter++) {
				JSONObject jsonMessage = jsonMessages.getJSONObject(iter);
				Tweet tweet = new Tweet();
				tweet.setId(getString(jsonMessage, "id_str"));
				if (!jsonMessage.isNull("retweeted_status")) {
					tweet.setRetweet(true);
				} else {
					tweet.setRetweet(false);
				}
				tweet.setMessage(getString(jsonMessage, "text"));
				tweet.setDate(getTwitterDate(jsonMessage.getString("created_at")));
				JSONObject user = jsonMessage.getJSONObject("user");
				tweet.setUser(getString(user, "name"));
				tweet.setAlias(getString(user, "screen_name"));
				String imageUrl = getString(user, "profile_image_url");
				if (imageUrl != null) {
					tweet.setUserImage(imageUrl.replace("_normal", ""));
				}
				tweet.setLink(String.format(URL_TWEET, tweet.getAlias(),
						tweet.getId()));
				if (tweet.getAlias() != null) {
					tweet.setAlias("@" + tweet.getAlias());
				}
				tweets.add(tweet);
			}
			return tweets;
		} catch (JSONException e) {
			throw new TwitterConnectionException(e, context);
		}
	}

	private Date getTwitterDate(String date) {
		try {
			final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZ yyyy";
			SimpleDateFormat sf = new SimpleDateFormat(TWITTER, Locale.ENGLISH);
			sf.setLenient(true);
			return sf.parse(date);
		} catch (ParseException e) {
			throw new TwitterConnectionException(e, context);
		}
	}

	private String getString(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}
}
