package cat.aubricoc.palaudenoguera.festamajor.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;
import cat.aubricoc.palaudenoguera.festamajor.activity.Activity;
import cat.aubricoc.palaudenoguera.festamajor.adapter.TwitterListAdapter;
import cat.aubricoc.palaudenoguera.festamajor.exception.ConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;
import cat.aubricoc.palaudenoguera.festamajor.service.TwitterService;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

public class TwitterFragment extends Fragment {

	private List<Tweet> tweetsList;

	private SwipeRefreshLayout refreshLayout;

	private ListView listView;

	private TwitterListAdapter listAdapter;

	private int preLast;

	private String twitterQuery;
	
	private Tweet last;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		twitterQuery = getActivity().getString(R.string.twitter_query);

		View rootView = inflater.inflate(R.layout.fragment_twitter, container,
				false);

		refreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.tweets_refresh);
		listView = (ListView) rootView.findViewById(R.id.tweets_list);

		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new GetDataTask().execute();
			}
		});

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				final int lastItem = firstVisibleItem + visibleItemCount;
				if (lastItem == totalItemCount) {
					if (preLast != lastItem) {
						new GetDataLastTask().execute(last.getId());
						preLast = lastItem;
					}
				}
			}
		});

		tweetsList = new ArrayList<Tweet>();

		listAdapter = new TwitterListAdapter(getActivity(), tweetsList);

		listView.setAdapter(listAdapter);

		new GetDataTask().execute();

		return rootView;
	}

	private class GetDataTask extends AsyncTask<String, Void, List<Tweet>> {

		protected String error;

		@Override
		protected List<Tweet> doInBackground(String... params) {
			try {
				List<Tweet> tweets = TwitterService.getInstance().search(
						twitterQuery, params.length == 0 ? null : params[0]);
				
				return tweets;
			} catch (ConnectionException e) {
				error = e.getMessage();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Tweet> result) {
			if (result == null) {
				Toast.makeText(Activity.CURRENT_CONTEXT, error,
						Toast.LENGTH_SHORT).show();
			} else {
				int news = 0;
				int olds = 0;
				for (int iter = result.size() - 1; iter >= 0; iter--) {
					Tweet tweet = result.get(iter);
					if (last == null) {
						last = tweet;
					}
					if (tweetsList.contains(tweet)) {
						Tweet tweetOld = tweetsList.get(tweetsList.indexOf(tweet));
						tweetOld.setDate(tweet.getDate());
						olds++;
					} else {
						tweetsList.add(0, tweet);
						news++;
					}
				}
				Log.i(Constants.PROJECT_NAME, "New tweets: " + news + ". Old: " + olds + ". Total: " + tweetsList.size());
				listAdapter.notifyDataSetChanged();
			}
			refreshLayout.setRefreshing(false);
		}
	}

	private class GetDataLastTask extends GetDataTask {

		@Override
		protected void onPostExecute(List<Tweet> result) {
			if (result == null) {
				Toast.makeText(Activity.CURRENT_CONTEXT, error,
						Toast.LENGTH_SHORT).show();
			} else {
				int news = 0;
				int olds = 0;
				for (Tweet tweet : result) {
					if (tweetsList.contains(tweet)) {
						Tweet tweetOld = tweetsList.get(tweetsList.indexOf(tweet));
						tweetOld.setDate(tweet.getDate());
						olds++;
					} else {
						tweetsList.add(tweet);
						last = tweet;
						news++;
					}
				}
				Log.i(Constants.PROJECT_NAME, "New tweets: " + news + ". Old: " + olds + ". Total: " + tweetsList.size());
				listAdapter.notifyDataSetChanged();
			}
			refreshLayout.setRefreshing(false);
		}
	}
}