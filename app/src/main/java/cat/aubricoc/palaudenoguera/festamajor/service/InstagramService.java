package cat.aubricoc.palaudenoguera.festamajor.service;

import android.util.Log;

import com.canteratech.androidutils.Activity;
import com.canteratech.androidutils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.dao.InstagramDao;
import cat.aubricoc.palaudenoguera.festamajor.exception.ConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.exception.InstagramConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.exception.TwitterConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.model.Instagram;
import cat.aubricoc.palaudenoguera.festamajor.utils.Constants;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class InstagramService {

	private static final InstagramService INSTANCE = new InstagramService();

	private static final String SEARCH_URL = "https://api.instagram.com/v1/tags/%s/media/recent";

	private String query;

	private InstagramService() {
		super();
		query = Activity.CURRENT_CONTEXT.getString(R.string.instagram_query);
	}

	public static InstagramService getInstance() {
		return INSTANCE;
	}

	public List<Instagram> getAll() {
		return InstagramDao.getInstance().getAll();
	}

	public List<Instagram> getNew() {
		Instagram firstInstagram = InstagramDao.getInstance().getFirstResult();
		String sinceId = null;
		if (firstInstagram != null) {
			sinceId = firstInstagram.getId();
		}
		return getNew(sinceId);
	}

	private List<Instagram> getNew(String sinceId) {
		List<Instagram> instagrams = search(sinceId, null);
		instagrams = saveInstagrams(instagrams);
		return instagrams;
	}

	public List<Instagram> getOld() {
		Instagram lastInstagram = InstagramDao.getInstance().getFirstResultOrderByIdAsc();
		String maxId = null;
		if (lastInstagram != null) {
			maxId = lastInstagram.getId();
		}
		return getOld(maxId);
	}

	private List<Instagram> getOld(String maxId) {
		List<Instagram> instagrams = search(null, maxId);
		instagrams = saveInstagrams(instagrams);
		return instagrams;
	}

	private List<Instagram> saveInstagrams(List<Instagram> instagrams) {
		List<Instagram> newInstagrams = new ArrayList<>();
		for (Instagram instagram : instagrams) {
			if (InstagramDao.getInstance().createIfNotExists(instagram) > -1) {
				newInstagrams.add(instagram);
			}
		}
		return newInstagrams;
	}

	private List<Instagram> search(String sinceId, String maxId) {

		if (!Utils.isOnline()) {
			throw new ConnectionException();
		}

		try {
			Log.i(Constants.PROJECT_NAME, "Searching instragrams '" + query + "'"
					+ (sinceId == null ? "" : ", sinceId=" + sinceId)
					+ (maxId == null ? "" : ", maxId=" + maxId));

			String apiKey = Activity.CURRENT_CONTEXT.getString(R.string.instagram_api_key);

			String encodedUrl = SEARCH_URL;
			encodedUrl = String.format(encodedUrl, URLEncoder.encode(query, "UTF-8"));
			encodedUrl += "?client_id=" + URLEncoder.encode(apiKey, "UTF-8");
			if (sinceId != null) {
				encodedUrl += "&min_tag_id=" + sinceId;
			}
			if (maxId != null) {
				encodedUrl += "&max_tag_id=" + maxId;
			}

			HttpGet httpGet = new HttpGet(encodedUrl);
			String result = getResponseBody(httpGet);

			return parseInstagrams(result);
		} catch (UnsupportedEncodingException e) {
			throw new InstagramConnectionException(e);
		}
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
				String line;
				while ((line = bReader.readLine()) != null) {
					sb.append(line);
				}
				return sb.toString();
			} else {
				Log.w(Constants.PROJECT_NAME, "Request to Twitter return "
						+ statusCode + ": " + reason);
			}
		} catch (IOException e) {
			throw new InstagramConnectionException(e);
		}
		throw new InstagramConnectionException();
	}

	private List<Instagram> parseInstagrams(String json) {
		try {
			List<Instagram> instagrams = new ArrayList<>();
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonMessages = jsonObject.getJSONArray("data");
			for (int iter = 0; iter < jsonMessages.length(); iter++) {
				JSONObject jsonMessage = jsonMessages.getJSONObject(iter);
				Instagram instagram = new Instagram();
				String id = getString(jsonMessage, "id");
				id = id.substring(0, id.indexOf("_"));
				instagram.setId(id);
				instagram.setMessage(getString(jsonMessage.getJSONObject("caption"), "text"));
				instagram.setDate(getInstagramDate(jsonMessage.getString("created_time")));
				JSONObject user = jsonMessage.getJSONObject("user");
				instagram.setUser(getString(user, "full_name"));
				instagram.setAlias(getString(user, "username"));
				instagram.setUserImage(getString(user, "profile_picture"));
				instagram.setLink(getString(jsonMessage, "link"));
				JSONObject images = jsonMessage.getJSONObject("images");
				instagram.setPictureThumbnailUrl(getString(images.getJSONObject("thumbnail"), "url"));
				instagram.setPictureLowUrl(getString(images.getJSONObject("low_resolution"), "url"));
				instagram.setPictureThumbnailUrl(getString(images.getJSONObject("standard_resolution"), "url"));
				instagrams.add(instagram);
			}
			return instagrams;
		} catch (JSONException e) {
			throw new TwitterConnectionException(e);
		}
	}

	private Date getInstagramDate(String text) {
		Date date = new Date();
		date.setTime(Long.parseLong(text) * 1000L);
		return date;
	}

	protected String getString(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}
}
