package cat.aubricoc.palaudenoguera.festamajor.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import cat.aubricoc.palaudenoguera.festamajor.adapter.FotosListAdapter;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

public class FotosFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fotos, container,
				false);

		GridView gridView = (GridView) rootView.findViewById(R.id.photos_grid);

		gridView.setAdapter(new FotosListAdapter(getActivity()));

		gridView.setVisibility(View.VISIBLE);
		return rootView;
	}

}