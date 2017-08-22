package cat.aubricoc.palaudenoguera.festamajor.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.adapter.TwitterListAdapter;
import cat.aubricoc.palaudenoguera.festamajor.exception.ConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.model.DataContainer;
import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;
import cat.aubricoc.palaudenoguera.festamajor.service.TwitterService;
import cat.aubricoc.palaudenoguera.festamajor2017.R;

public class TwitterFragment extends Fragment {

	private SwipeRefreshLayout refreshLayout;

	private TwitterListAdapter listAdapter;

	private TwitterService twitterService;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		twitterService = TwitterService.newInstance(getContext());
		
		View rootView = inflater.inflate(R.layout.fragment_twitter, container,
				false);

		refreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.tweets_refresh);
		RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.tweets_list);
		final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(layoutManager);

		refreshLayout.setColorSchemeResources(R.color.primary, R.color.secondary, R.color.primary, R.color.secondary);

		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new GetNewTweetsTask().execute();
			}
		});

		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

			private int pastVisibleItems;

			private int visibleItemCount;

			private int totalItemCount;

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				visibleItemCount = layoutManager.getChildCount();
				totalItemCount = layoutManager.getItemCount();
				pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

				if (!refreshLayout.isRefreshing()) {
					if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
						new GetOldTweetsTask().execute();
					}
				}
			}
		});

		List<Tweet> tweets = DataContainer.getTweets();
		boolean searchNew = false;
		if (tweets.isEmpty()) {
			tweets = twitterService.getAll();
			DataContainer.setTweets(tweets);
			searchNew = true;
		}

		listAdapter = new TwitterListAdapter(getContext(), tweets);
		recyclerView.setAdapter(listAdapter);

//		recyclerView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//									int position, long id) {
//				Tweet tweet = listAdapter.getItem(position);
//				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweet
//						.getLink()));
//				startActivity(intent);
//			}
//		});

		if (searchNew) {
			new GetNewTweetsTask().execute();
		}

		return rootView;
	}

	private class GetNewTweetsTask extends AsyncTask<Void, Void, List<Tweet>> {

		String error;

		@Override
		protected void onPreExecute() {
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
					Toast.makeText(getContext(), R.string.twitter_connection_error, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
				}
			} else {
				if (DataContainer.getTweets().isEmpty() && result.isEmpty()) {
					Toast.makeText(getContext(), R.string.no_tweets, Toast.LENGTH_SHORT).show();
				}
				addTweets(result);
				listAdapter.notifyDataSetChanged();
			}
			refreshLayout.setRefreshing(false);
		}

		List<Tweet> searchTweets() {
			return twitterService.getNew();
		}

		void addTweets(List<Tweet> result) {
			for (int iter = result.size() - 1; iter >= 0; iter--) {
				Tweet tweet = result.get(iter);
				DataContainer.getTweets().add(0, tweet);
			}
		}
	}

	private class GetOldTweetsTask extends GetNewTweetsTask {

		@Override
		List<Tweet> searchTweets() {
			return twitterService.getOld();
		}

		@Override
		void addTweets(List<Tweet> result) {
			for (Tweet tweet : result) {
				DataContainer.getTweets().add(tweet);
			}
		}
	}
}