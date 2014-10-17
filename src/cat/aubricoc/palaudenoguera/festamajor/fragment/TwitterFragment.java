package cat.aubricoc.palaudenoguera.festamajor.fragment;

import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cat.aubricoc.palaudenoguera.festamajor.adapter.TwitterListAdapter;
import cat.aubricoc.palaudenoguera.festamajor.exception.ConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.model.DataContainer;
import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;
import cat.aubricoc.palaudenoguera.festamajor.service.TwitterService;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

import com.canteratech.androidutils.Activity;

public class TwitterFragment extends Fragment {

	private SwipeRefreshLayout refreshLayout;

	private ListView listView;

	private TwitterListAdapter listAdapter;

	private Button retryButton;

	private TextView noConnectionText;

	private TextView noTweetsText;

	private View loading;
	
	private View messageContainer;

	private int preLast;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_twitter, container,
				false);

		refreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.tweets_refresh);
		listView = (ListView) rootView.findViewById(R.id.tweets_list);
		retryButton = (Button) rootView.findViewById(R.id.retry);
		noConnectionText = (TextView) rootView.findViewById(R.id.no_connection);
		noTweetsText = (TextView) rootView.findViewById(R.id.no_tweets);
		loading = rootView.findViewById(R.id.loading);
		messageContainer = rootView.findViewById(R.id.twitter_message_container);

		refreshLayout.setColorScheme(R.color.maroon,
				R.color.green, R.color.maroon, R.color.green);

		retryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				retryButton.setVisibility(View.INVISIBLE);
				new GetNewTweetsTask().execute();
			}
		});

		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new GetNewTweetsTask().execute();
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
						new GetOldTweetsTask().execute();
						preLast = lastItem;
					}
				}
			}
		});

		List<Tweet> tweets = DataContainer.getTweets();
		boolean searchNew = false;
		if (tweets.isEmpty()) {
			tweets = TwitterService.getInstance().getAll();
			DataContainer.setTweets(tweets);
			searchNew = true;
		}

		listAdapter = new TwitterListAdapter(getActivity(), tweets);
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Tweet tweet = listAdapter.getItem(position);
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweet
						.getLink()));
				startActivity(intent);
			}
		});

		if (searchNew) {
			new GetNewTweetsTask().execute();
		} else {
			showTweets();
		}

		return rootView;
	}

	private void showError() {
		refreshLayout.setVisibility(View.GONE);
		noConnectionText.setVisibility(View.VISIBLE);
		retryButton.setVisibility(View.VISIBLE);
		noTweetsText.setVisibility(View.GONE);
		messageContainer.setVisibility(View.VISIBLE);
	}

	private void showTweets() {
		refreshLayout.setVisibility(View.VISIBLE);
		noConnectionText.setVisibility(View.GONE);
		retryButton.setVisibility(View.GONE);
		noTweetsText.setVisibility(View.GONE);
		messageContainer.setVisibility(View.GONE);
	}

	private void showNoTweets() {
		refreshLayout.setVisibility(View.GONE);
		noConnectionText.setVisibility(View.GONE);
		retryButton.setVisibility(View.GONE);
		noTweetsText.setVisibility(View.VISIBLE);
		messageContainer.setVisibility(View.VISIBLE);
	}

	private class GetNewTweetsTask extends AsyncTask<Void, Void, List<Tweet>> {

		protected String error;

		@Override
		protected void onPreExecute() {
			loading.setVisibility(View.VISIBLE);
			refreshLayout.setRefreshing(true);
		}

		@Override
		protected List<Tweet> doInBackground(Void... params) {
			try {
				return searchTweets();
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
					showTweets();
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

		protected List<Tweet> searchTweets() {
			return TwitterService.getInstance().getNew();
		}

		protected void addTweets(List<Tweet> result) {
			for (int iter = result.size() - 1; iter >= 0; iter--) {
				Tweet tweet = result.get(iter);
				DataContainer.getTweets().add(0, tweet);
			}
		}
	}

	private class GetOldTweetsTask extends GetNewTweetsTask {

		@Override
		protected List<Tweet> searchTweets() {
			return TwitterService.getInstance().getOld();
		}

		@Override
		protected void addTweets(List<Tweet> result) {
			for (Tweet tweet : result) {
				DataContainer.getTweets().add(tweet);
			}
		}
	}
}