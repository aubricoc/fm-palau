package cat.aubricoc.palaudenoguera.festamajor.fragment;

import java.util.Arrays;
import java.util.LinkedList;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import cat.aubricoc.palaudenoguera.festamajor.view.PullToRefreshListView;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

public class TwitterFragment extends Fragment {

	private LinkedList<String> mListItems;
	private SwipeRefreshLayout refreshLayout;
	private ListView listView;
	private ArrayAdapter<String> listAdapter;
	private int preLast;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_twitter, container,
				false);

		refreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.tweets_refresh);
		listView = (ListView) rootView.findViewById(R.id.tweets_list);
		
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				new GetDataTask().execute();
			}
		});
		
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				final int lastItem = firstVisibleItem + visibleItemCount;
		           if(lastItem == totalItemCount) {
		              if(preLast!=lastItem){ //to avoid multiple calls for last item
		            	  new GetDataLastTask().execute();
		                preLast = lastItem;
		              }
		           }
			}
		});
		

		mListItems = new LinkedList<String>();
		mListItems.addAll(Arrays.asList(mStrings));

		listAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, mListItems);

		listView.setAdapter(listAdapter);

		return rootView;
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {
			mListItems.addFirst("Added after refresh...");

			// Call onRefreshComplete when the list has been refreshed.
			listAdapter.notifyDataSetChanged();
			
			refreshLayout.setRefreshing(false);

			super.onPostExecute(result);
		}
	}
	
	private class GetDataLastTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {
			mListItems.addLast("Added after refresh...");

			// Call onRefreshComplete when the list has been refreshed.
			listAdapter.notifyDataSetChanged();
			
			super.onPostExecute(result);
		}
	}

	private String[] mStrings = { "Abbaye de Belloc",
			"Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu",
			"Airag", "Airedale", "Aisy Cendre", "Allgauer Emmentaler" };
}