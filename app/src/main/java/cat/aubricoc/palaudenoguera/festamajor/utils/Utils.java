package cat.aubricoc.palaudenoguera.festamajor.utils;

import com.canteratech.androidutils.Activity;

import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class Utils {

	public static int getComplementaryColor(int position) {
		int color = 0;
		switch (position % 4) {
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
				color = R.color.primary;
				break;
		}
		return Activity.CURRENT_CONTEXT.getResources().getColor(color);
	}
}
