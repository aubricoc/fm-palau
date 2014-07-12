package cat.aubricoc.palaudenoguera.festamajor.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.SparseArray;

public class MainFragmentManager {

	public static final String ARG_SECTION_NUMBER = "section_number";

	private static final SparseArray<Class<?>> FRAGMENTS_CLASS = new SparseArray<Class<?>>();
	static {
		FRAGMENTS_CLASS.put(1, ProgramaFragment.class);
		FRAGMENTS_CLASS.put(2, ProgramaFragment.class);
		FRAGMENTS_CLASS.put(3, ProgramaFragment.class);
	}

	public static Fragment newInstance(int sectionNumber) {
		try {
			Fragment fragment = (Fragment) FRAGMENTS_CLASS.get(sectionNumber)
					.newInstance();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
}
