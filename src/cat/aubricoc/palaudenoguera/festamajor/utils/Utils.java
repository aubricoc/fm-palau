package cat.aubricoc.palaudenoguera.festamajor.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import cat.aubricoc.palaudenoguera.festamajor.activity.Activity;

public class Utils {

	public static boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) Activity.CURRENT_CONTEXT
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
