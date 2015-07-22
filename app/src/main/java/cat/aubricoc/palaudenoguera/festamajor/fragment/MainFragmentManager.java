package cat.aubricoc.palaudenoguera.festamajor.fragment;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

public class MainFragmentManager {

	private static final SparseArray<Class<?>> FRAGMENTS_CLASS = new SparseArray<Class<?>>();
	static {
		FRAGMENTS_CLASS.put(1, ProgramaFragment.class);
		FRAGMENTS_CLASS.put(2, TwitterFragment.class);
		FRAGMENTS_CLASS.put(3, FotosFragment.class);
	}

	public static Fragment newInstance(int sectionNumber) {
		try {
			Fragment fragment = (Fragment) FRAGMENTS_CLASS.get(sectionNumber)
					.newInstance();
			return fragment;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
}
