package cat.aubricoc.palaudenoguera.festamajor.task;

import java.io.ByteArrayInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import cat.aubricoc.palaudenoguera.festamajor.utils.Utils;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

public class LoadImageViewAsyncTask extends AsyncTask<String, Void, byte[]> {

	private ImageView imageView;

	public LoadImageViewAsyncTask(ImageView imageView) {
		this.imageView = imageView;
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

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(params[0]);

		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);

			int statusCode = httpResponse.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = httpResponse.getEntity();
				if (entity != null) {
					return EntityUtils.toByteArray(entity);
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	@Override
	protected void onPostExecute(byte[] result) {
		if (result != null) {
			imageView.setImageDrawable(Drawable.createFromStream(
					new ByteArrayInputStream(result), null));
			imageView.setVisibility(View.VISIBLE);
		}
	}
}
