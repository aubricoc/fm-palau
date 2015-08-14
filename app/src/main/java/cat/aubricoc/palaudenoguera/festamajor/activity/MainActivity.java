package cat.aubricoc.palaudenoguera.festamajor.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.canteratech.androidutils.Activity;

import java.util.ArrayList;
import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.fragment.InstagramFragment;
import cat.aubricoc.palaudenoguera.festamajor.fragment.ScheduleFragment;
import cat.aubricoc.palaudenoguera.festamajor.fragment.TwitterFragment;
import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class MainActivity extends Activity {

	private ViewPager viewPager;

	private ViewPagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.app_title);
		setSupportActionBar(toolbar);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		setupViewPager(viewPager);
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);
	}

	private void setupViewPager(ViewPager viewPager) {
		adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new ScheduleFragment(), R.string.title_programa);
		adapter.addFragment(new TwitterFragment(), R.string.title_twitter);
		adapter.addFragment(new InstagramFragment(), R.string.title_instagram);
		viewPager.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.info:
				showInfo();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showInfo() {
		Dialog dialog = new Dialog(Activity.CURRENT_CONTEXT);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		dialog.setContentView(R.layout.dialog_info);
		dialog.show();
	}

	class ViewPagerAdapter extends FragmentPagerAdapter {

		private final List<Fragment> mFragmentList = new ArrayList<>();

		private final List<String> mFragmentTitleList = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}
		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}
		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(Fragment fragment, int titleRes) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(getString(titleRes));
		}
		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}
}
