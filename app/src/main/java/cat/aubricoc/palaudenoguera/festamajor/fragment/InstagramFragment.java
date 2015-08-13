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

import com.canteratech.androidutils.Activity;

import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.adapter.InstagramListAdapter;
import cat.aubricoc.palaudenoguera.festamajor.exception.ConnectionException;
import cat.aubricoc.palaudenoguera.festamajor.model.DataContainer;
import cat.aubricoc.palaudenoguera.festamajor.model.Instagram;
import cat.aubricoc.palaudenoguera.festamajor.service.InstagramService;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class InstagramFragment extends Fragment {

	private SwipeRefreshLayout refreshLayout;

	private InstagramListAdapter listAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_instagram, container,
				false);

		refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.instagrams_refresh);
		RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.instagrams_list);
		final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(layoutManager);

		refreshLayout.setColorSchemeResources(R.color.maroon, R.color.green, R.color.maroon, R.color.green);

		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new GetNewInstagramsTask().execute();
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
						new GetOldInstagramsTask().execute();
					}
				}
			}
		});

		List<Instagram> instagrams = DataContainer.getInstagrams();
		boolean searchNew = false;
		if (instagrams.isEmpty()) {
			instagrams = InstagramService.getInstance().getAll();
			DataContainer.setInstagrams(instagrams);
			searchNew = true;
		}

		listAdapter = new InstagramListAdapter(instagrams);
		recyclerView.setAdapter(listAdapter);

//		recyclerView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//									int position, long id) {
//				Instagram instagram = listAdapter.getItem(position);
//				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagram
//						.getLink()));
//				startActivity(intent);
//			}
//		});

		if (searchNew) {
			new GetNewInstagramsTask().execute();
		}

		return rootView;
	}

	private class GetNewInstagramsTask extends AsyncTask<Void, Void, List<Instagram>> {

		protected String error;

		@Override
		protected void onPreExecute() {
			refreshLayout.setRefreshing(true);
		}

		@Override
		protected List<Instagram> doInBackground(Void... params) {
			try {
				return searchInstagrams();
			} catch (ConnectionException e) {
				error = e.getMessage();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Instagram> result) {
			if (result == null) {
				if (DataContainer.getInstagrams().isEmpty()) {
					Toast.makeText(Activity.CURRENT_CONTEXT, R.string.instagram_connection_error, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(Activity.CURRENT_CONTEXT, error, Toast.LENGTH_SHORT).show();
				}
			} else {
				if (DataContainer.getInstagrams().isEmpty() && result.isEmpty()) {
					Toast.makeText(Activity.CURRENT_CONTEXT, R.string.info_instagram, Toast.LENGTH_SHORT).show();
				}
				addInstagrams(result);
				listAdapter.notifyDataSetChanged();
			}
			refreshLayout.setRefreshing(false);
		}

		protected List<Instagram> searchInstagrams() {
			return InstagramService.getInstance().getNew();
		}

		protected void addInstagrams(List<Instagram> result) {
			for (int iter = result.size() - 1; iter >= 0; iter--) {
				Instagram instagram = result.get(iter);
				DataContainer.getInstagrams().add(0, instagram);
			}
		}
	}

	private class GetOldInstagramsTask extends GetNewInstagramsTask {

		@Override
		protected List<Instagram> searchInstagrams() {
			return InstagramService.getInstance().getOld();
		}

		@Override
		protected void addInstagrams(List<Instagram> result) {
			for (Instagram instagram : result) {
				DataContainer.getInstagrams().add(instagram);
			}
		}
	}
}