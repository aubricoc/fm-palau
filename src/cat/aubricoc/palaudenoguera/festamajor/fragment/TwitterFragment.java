package cat.aubricoc.palaudenoguera.festamajor.fragment;

import java.util.List;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cat.aubricoc.palaudenoguera.festamajor.activity.Activity;
import cat.aubricoc.palaudenoguera.festamajor.adapter.TwitterListAdapter;
import cat.aubricoc.palaudenoguera.festamajor.exception.ConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.model.DataContainer;
import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;
import cat.aubricoc.palaudenoguera.festamajor.service.TwitterService;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

public class TwitterFragment extends Fragment {

	private SwipeRefreshLayout refreshLayout;

	private ListView listView;

	private TwitterListAdapter listAdapter;

	private Button retryButton;

	private TextView noConnectionText;

	private TextView noTweetsText;

	private View loading;

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
		retryButton = (Button) rootView.findViewById(R.id.retry);
		noConnectionText = (TextView) rootView.findViewById(R.id.no_connection);
		noTweetsText = (TextView) rootView.findViewById(R.id.no_tweets);
		loading = rootView.findViewById(R.id.loading);

		retryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				retryButton.setVisibility(View.GONE);
				new GetDataTask().execute();
			}
		});

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

		listAdapter = new TwitterListAdapter(getActivity(),
				DataContainer.getTweets());

		listView.setAdapter(listAdapter);

		if (DataContainer.getTweets().isEmpty()) {
			new GetDataTask().execute();
		} else {
			last = DataContainer.getTweets().get(
					DataContainer.getTweets().size() - 1);
			showTweets();
		}

		return rootView;
	}

	private void showError() {
		refreshLayout.setVisibility(View.GONE);
		noConnectionText.setVisibility(View.VISIBLE);
		retryButton.setVisibility(View.VISIBLE);
		noTweetsText.setVisibility(View.GONE);
	}

	private void showTweets() {
		refreshLayout.setVisibility(View.VISIBLE);
		noConnectionText.setVisibility(View.GONE);
		retryButton.setVisibility(View.GONE);
		noTweetsText.setVisibility(View.GONE);
	}

	private void showNoTweets() {
		refreshLayout.setVisibility(View.GONE);
		noConnectionText.setVisibility(View.GONE);
		retryButton.setVisibility(View.GONE);
		noTweetsText.setVisibility(View.VISIBLE);
	}

	private class GetDataTask extends AsyncTask<String, Void, List<Tweet>> {

		protected String error;

		@Override
		protected void onPreExecute() {
			loading.setVisibility(View.VISIBLE);
		}

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
				if (DataContainer.getTweets().isEmpty()) {
					showError();
				} else {
					Toast.makeText(Activity.CURRENT_CONTEXT, error,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				if (DataContainer.getTweets().isEmpty() && result.isEmpty()) {
					showNoTweets();
				} else {
					showTweets();
				}
				addTweets(result);
				listAdapter.notifyDataSetChanged();
			}
			refreshLayout.setRefreshing(false);
			loading.setVisibility(View.GONE);
		}

		protected void addTweets(List<Tweet> result) {
			for (int iter = result.size() - 1; iter >= 0; iter--) {
				Tweet tweet = result.get(iter);
				if (last == null) {
					last = tweet;
				}
				if (DataContainer.getTweets().contains(tweet)) {
					Tweet tweetOld = DataContainer.getTweets().get(
							DataContainer.getTweets().indexOf(tweet));
					tweetOld.setDate(tweet.getDate());
				} else {
					DataContainer.getTweets().add(0, tweet);
				}
			}
		}
	}

	private class GetDataLastTask extends GetDataTask {

		@Override
		protected void addTweets(List<Tweet> result) {
			for (Tweet tweet : result) {
				if (DataContainer.getTweets().contains(tweet)) {
					Tweet tweetOld = DataContainer.getTweets().get(
							DataContainer.getTweets().indexOf(tweet));
					tweetOld.setDate(tweet.getDate());
				} else {
					DataContainer.getTweets().add(tweet);
					last = tweet;
				}
			}
		}
	}
}