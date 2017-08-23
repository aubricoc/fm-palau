package cat.aubricoc.palaudenoguera.festamajor.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;

import cat.aubricoc.palaudenoguera.festamajor2017.R;

public class Utils {

	private static int[] COMPLEMENTARY_COLORS = {
			R.color.complementary_1,
			R.color.complementary_2,
			R.color.complementary_3,
			R.color.complementary_4
	};

	public static int getComplementaryColor(Context context, int position) {
		int color = COMPLEMENTARY_COLORS[position % COMPLEMENTARY_COLORS.length];
		return ContextCompat.getColor(context, color);
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}
}
