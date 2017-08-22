package cat.aubricoc.palaudenoguera.festamajor.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;

import cat.aubricoc.palaudenoguera.festamajor2017.R;

public class Utils {

	public static int getComplementaryColor(Context context, int position) {
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
		return ContextCompat.getColor(context, color);
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}
}
