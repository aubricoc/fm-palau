package cat.aubricoc.palaudenoguera.festamajor.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;

import cat.aubricoc.palaudenoguera.festamajor.activity.FotoActivity;
import cat.aubricoc.palaudenoguera.festamajor.adapter.FotosListAdapter;
import cat.aubricoc.palaudenoguera.festamajor.model.DataContainer;
import cat.aubricoc.palaudenoguera.festamajor.model.Photo;
import cat.aubricoc.palaudenoguera.festamajor.utils.Constants;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class FotosFragment extends Fragment {

	private GridView gridView;
	private View loading;
	private View noPhotos;
	private ListAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fotos, container,
				false);

		gridView = (GridView) rootView.findViewById(R.id.photos_grid);
		loading = rootView.findViewById(R.id.loading_fotos);
		noPhotos = rootView.findViewById(R.id.no_photos);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Photo photo = (Photo) adapter.getItem(position);
				Intent intent = new Intent(getActivity(), FotoActivity.class);
				intent.putExtra(Constants.EXTRA_RESOURCE_ID, photo.getResourceId());
				getActivity().startActivity(intent);
			}
		});
		
		new LoadingFotosTask().execute();
		
		return rootView;
	}

	class LoadingFotosTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			loading.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			return DataContainer.preparePhotos(getActivity());
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			loading.setVisibility(View.GONE);
			if (result) {
				noPhotos.setVisibility(View.GONE);
				gridView.setVisibility(View.VISIBLE);
				adapter = new FotosListAdapter(getActivity());
				gridView.setAdapter(adapter);
			}
		}
	}
}