package cat.aubricoc.palaudenoguera.festamajor.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Photo implements Comparable<Photo> {

	private int resourceId;
	
	private String name;

	private Bitmap bitmap;

	public Photo(int resourceId, String name, Context context) {
		this.resourceId = resourceId;
		this.name = name;
		prepareBitmap(context);
	}

	public int getResourceId() {
		return resourceId;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	private void prepareBitmap(Context context) {
		Bitmap full = BitmapFactory.decodeResource(context.getResources(),
				resourceId);
		int nh = (int) (full.getHeight() * (256.0 / full.getWidth()));
		bitmap = Bitmap.createScaledBitmap(full, 256, nh, true);
	}

	@Override
	public int compareTo(Photo another) {
		return another.name.compareTo(name);
	}
}
