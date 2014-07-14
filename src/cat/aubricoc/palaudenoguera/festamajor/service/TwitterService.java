package cat.aubricoc.palaudenoguera.festamajor.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import cat.aubricoc.palaudenoguera.festamajor.activity.Activity;
import cat.aubricoc.palaudenoguera.festamajor.exception.ConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.exception.TwitterConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.fragment.Constants;
import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

public class TwitterService {

	private static final TwitterService INSTANCE = new TwitterService();
	private static final String TOKEN_URL = "https://api.twitter.com/oauth2/token";
	private static final String SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json?result_type=recent&q=";
	private static final String URL_TWEET = "https://twitter.com/%s/status/%s";

	private TwitterService() {
		super();
	}

	public static TwitterService getInstance() {
		return INSTANCE;
	}

	public List<Tweet> search(String... searches) {

		if (!isOnline()) {
			throw new ConnectionException();
		}

		try {
			String encodedUrl = SEARCH_URL
					+ URLEncoder.encode(searches[0], "UTF-8");

			String apiKey = Activity.CURRENT_CONTEXT
					.getString(R.string.twitter_api_key);
			String apiSecret = Activity.CURRENT_CONTEXT
					.getString(R.string.twitter_api_secret);

			String urlApiKey = URLEncoder.encode(apiKey, "UTF-8");
			String urlApiSecret = URLEncoder.encode(apiSecret, "UTF-8");

			String combined = urlApiKey + ":" + urlApiSecret;

			String base64Encoded = Base64.encodeToString(combined.getBytes(),
					Base64.NO_WRAP);

			HttpPost httpPost = new HttpPost(TOKEN_URL);
			httpPost.setHeader("Authorization", "Basic " + base64Encoded);
			httpPost.setHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
			String rawAuthorization = getResponseBody(httpPost);
			String accessToken = getAccessToken(rawAuthorization);

			HttpGet httpGet = new HttpGet(encodedUrl);

			httpGet.setHeader("Authorization", "Bearer " + accessToken);
			httpGet.setHeader("Content-Type", "application/json");

			String result = getResponseBody(httpGet);

			return parseTweets(result);
		} catch (UnsupportedEncodingException e) {
			throw new TwitterConnectionException(e);
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
				throw new TwitterConnectionException(e);
			}
		}
		throw new TwitterConnectionException();
	}

	private String getResponseBody(HttpRequestBase request) {
		StringBuilder sb = new StringBuilder();
		try {

			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					Constants.CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, Constants.SO_TIMEOUT);

			HttpClient httpClient = new DefaultHttpClient(httpParams);

			HttpResponse response = httpClient.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			String reason = response.getStatusLine().getReasonPhrase();

			if (statusCode == 200) {

				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();

				BufferedReader bReader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"), 8);
				String line = null;
				while ((line = bReader.readLine()) != null) {
					sb.append(line);
				}
				return sb.toString();
			} else {
				Log.w(Constants.PROJECT_NAME, "Request to Twitter return "
						+ statusCode + ": " + reason);
			}
		} catch (IOException e) {
			throw new TwitterConnectionException(e);
		}
		throw new TwitterConnectionException();
	}

	private List<Tweet> parseTweets(String json) {
		try {
			List<Tweet> tweets = new ArrayList<Tweet>();
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonMessages = jsonObject.getJSONArray("statuses");
			for (int iter = 0; iter < jsonMessages.length(); iter++) {
				JSONObject jsonMessage = jsonMessages.getJSONObject(iter);
				Tweet tweet = new Tweet();
				tweet.setId(getString(jsonMessage, "id_str"));
				tweet.setMessage(getString(jsonMessage, "text"));
				tweet.setDate(getTwitterDate(jsonMessage
						.getString("created_at")));
				JSONObject user = jsonMessage.getJSONObject("user");
				tweet.setUser(getString(user, "name"));
				tweet.setAlias(getString(user, "screen_name"));
				tweet.setUserImage(getString(user, "profile_image_url"));
				tweet.setLink(String.format(URL_TWEET, tweet.getAlias(),
						tweet.getId()));
				if (tweet.getAlias() != null) {
					tweet.setAlias("@" + tweet.getAlias());
				}
				tweets.add(tweet);
			}
			return tweets;
		} catch (JSONException e) {
			throw new TwitterConnectionException(e);
		}
	}

	private Date getTwitterDate(String date) {
		try {
			final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZ yyyy";
			SimpleDateFormat sf = new SimpleDateFormat(TWITTER, Locale.ENGLISH);
			sf.setLenient(true);
			return sf.parse(date);
		} catch (ParseException e) {
			throw new TwitterConnectionException(e);
		}
	}

	protected String getString(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}

	public static boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) Activity.CURRENT_CONTEXT
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
