package cat.aubricoc.palaudenoguera.festamajor.utils;

import android.support.v4.content.ContextCompat;

import com.canteratech.androidutils.Activity;

import cat.aubricoc.palaudenoguera.festamajor2016.R;

public class Utils {

	public static int getComplementaryColor(int position) {
		int color = 0;
		switch (position % 6) {
			case 0:
				color = R.color.complementary_1;
				break;
			case 1:
				color = R.color.complementary_2;
				break;
			case 2:
				color = R.color.complementary_3;
				break;
			case 3:
				color = R.color.complementary_4;
				break;
			case 4:
				color = R.color.complementary_5;
				break;
			case 5:
				color = R.color.complementary_6;
				break;
		}
		return ContextCompat.getColor(Activity.CURRENT_CONTEXT, color);
	}
}
