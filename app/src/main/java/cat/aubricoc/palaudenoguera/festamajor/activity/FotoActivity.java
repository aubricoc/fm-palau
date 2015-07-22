package cat.aubricoc.palaudenoguera.festamajor.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.canteratech.androidutils.Activity;

import cat.aubricoc.palaudenoguera.festamajor.utils.Constants;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class FotoActivity extends Activity {

	private int resourceId;

	@Override
	protected void onCreate() {

		setTitle(R.string.app_title);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		resourceId = getIntent().getIntExtra(Constants.EXTRA_RESOURCE_ID, -1);
		ImageView imageView = (ImageView) findViewById(R.id.foto_image);
		imageView.setImageResource(resourceId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_foto, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.share:
			sharePhoto();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void sharePhoto() {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setType("image/*");
		Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
				+ getResources().getResourcePackageName(resourceId) + '/'
				+ getResources().getResourceTypeName(resourceId) + '/'
				+ getResources().getResourceEntryName(resourceId));
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(shareIntent,
				getText(R.string.send_to)));
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_foto;
	}
}
