package cat.aubricoc.palaudenoguera.festamajor.fragment;

import java.util.LinkedList;
import java.util.List;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cat.aubricoc.palaudenoguera.festamajor.activity.Activity;
import cat.aubricoc.palaudenoguera.festamajor.exception.ConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;
import cat.aubricoc.palaudenoguera.festamajor.service.TwitterService;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

public class TwitterFragment extends Fragment {

	private LinkedList<Tweet> tweetsList;

	private SwipeRefreshLayout refreshLayout;

	private ListView listView;

	private ArrayAdapter<Tweet> listAdapter;

	private int preLast;

	private String twitterQuery;

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
						new GetDataLastTask().execute();
						preLast = lastItem;
					}
				}
			}
		});

		tweetsList = new LinkedList<Tweet>();

		listAdapter = new ArrayAdapter<Tweet>(getActivity(),
				android.R.layout.simple_list_item_1, tweetsList);

		listView.setAdapter(listAdapter);

		return rootView;
	}

	private class GetDataTask extends AsyncTask<Void, Void, List<Tweet>> {

		private String error;

		@Override
		protected List<Tweet> doInBackground(Void... params) {
			try {
				List<Tweet> tweets = TwitterService.getInstance().search(
						twitterQuery);
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
				for (int iter = result.size() - 1; iter >= 0; iter--) {
					Tweet tweet = result.get(iter);
					if (!tweetsList.contains(tweet)) {
						tweetsList.addFirst(tweet);
					}
				}
				listAdapter.notifyDataSetChanged();
			}
			refreshLayout.setRefreshing(false);
		}
	}

	private class GetDataLastTask extends AsyncTask<Void, Void, List<Tweet>> {

		private String error;

		@Override
		protected List<Tweet> doInBackground(Void... params) {
			try {
				List<Tweet> tweets = TwitterService.getInstance().search(
						twitterQuery);
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
				for (Tweet tweet : result) {
					if (!tweetsList.contains(tweet)) {
						tweetsList.add(tweet);
					}
				}
				listAdapter.notifyDataSetChanged();
			}
			refreshLayout.setRefreshing(false);
		}
	}
}