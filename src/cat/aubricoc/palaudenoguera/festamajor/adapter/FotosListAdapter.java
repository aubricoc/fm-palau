package cat.aubricoc.palaudenoguera.festamajor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import cat.aubricoc.palaudenoguera.festamajor.model.DataContainer;

public class FotosListAdapter extends BaseAdapter {

	private Context context;

	public FotosListAdapter(Context c) {
		context = c;
	}

	public int getCount() {
		return DataContainer.getPhotos().size();
	}

	public Object getItem(int position) {
		return DataContainer.getPhotos().get(position);
	}

	public long getItemId(int position) {
		return DataContainer.getPhotos().get(position).getResourceId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(
					LayoutParams.MATCH_PARENT, 300));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			imageView = (ImageView) convertView;
		}

//		imageView.setImageBitmap(DataContainer.getPhotos().get(position).getBitmap());
//		imageView.setImageResource(DataContainer.getPhotos().get(position).getResourceId());
		imageView.setImageDrawable(DataContainer.getPhotos().get(position).getDrawable());
		
		return imageView;
	}
}