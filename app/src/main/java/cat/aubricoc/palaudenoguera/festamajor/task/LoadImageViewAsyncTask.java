package cat.aubricoc.palaudenoguera.festamajor.task;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.canteratech.androidutils.IOUtils;
import com.canteratech.androidutils.Utils;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cat.aubricoc.palaudenoguera.festamajor.utils.Constants;

public class LoadImageViewAsyncTask extends AsyncTask<String, Void, byte[]> {

	private final ImageView imageView;

	private final OnReceivedImageListener onReceivedImageListener;

	public LoadImageViewAsyncTask(ImageView imageView, OnReceivedImageListener onReceivedImageListener) {
		this.imageView = imageView;
		this.onReceivedImageListener = onReceivedImageListener;
	}

	@Override
	protected void onPreExecute() {
		imageView.setVisibility(View.INVISIBLE);
	}

	@Override
	protected byte[] doInBackground(String... params) {

		if (!Utils.isOnline()) {
			return null;
		}

		try {
			URL url = new URL(params[0]);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
			conn.setReadTimeout(Constants.SO_TIMEOUT);

			int statusCode = conn.getResponseCode();

			if (statusCode == 200) {
				return IOUtils.toByteArray(conn.getInputStream());

			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	@Override
	protected void onPostExecute(byte[] result) {
		if (result != null) {
			Drawable drawable = Drawable.createFromStream(new ByteArrayInputStream(result), null);
			imageView.setImageDrawable(drawable);
			imageView.setVisibility(View.VISIBLE);
			if (onReceivedImageListener != null) {
				onReceivedImageListener.onReceivedImage(result, drawable);
			}
		}
	}

	public interface OnReceivedImageListener {

		void onReceivedImage(byte[] image, Drawable drawable);
	}
}
