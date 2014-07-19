package cat.aubricoc.palaudenoguera.festamajor.model;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class Photo implements Comparable<Photo> {

	private int resourceId;
	
	private String name;

	private Drawable drawable;

	public Photo(int resourceId, String name, Context context) {
		this.resourceId = resourceId;
		this.name = name;
		prepareDrawable(context);
	}

	public int getResourceId() {
		return resourceId;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	private void prepareDrawable(Context context) {
		drawable = context.getResources().getDrawable(resourceId);
	}

	@Override
	public int compareTo(Photo another) {
		return another.name.compareTo(name);
	}
}
