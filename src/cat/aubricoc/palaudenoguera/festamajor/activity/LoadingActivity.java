package cat.aubricoc.palaudenoguera.festamajor.activity;

import android.os.AsyncTask;
import cat.aubricoc.palaudenoguera.festamajor.model.DataContainer;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

public class LoadingActivity extends Activity {

	@Override
	protected void onCreate() {
		new LoadingTask().execute();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_loading;
	}
	
	class LoadingTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			DataContainer.preparePhotos(LoadingActivity.this);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			goToAndFinish(MainActivity.class);
		}
	}
}
