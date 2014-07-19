package cat.aubricoc.palaudenoguera.festamajor.activity;

import android.view.MenuItem;
import android.widget.ImageView;
import cat.aubricoc.palaudenoguera.festamajor.utils.Constants;
import cat.aubricoc.palaudenoguera.festamajor2014.R;

public class FotoActivity extends Activity {

	@Override
	protected void onCreate() {

		setTitle(R.string.app_title);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		int resourceId = getIntent().getIntExtra(Constants.EXTRA_RESOURCE_ID,
				-1);
		ImageView imageView = (ImageView) findViewById(R.id.foto_image);
		imageView.setImageResource(resourceId);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_foto;
	}
}
