package cat.aubricoc.palaudenoguera.festamajor.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.canteratech.androidutils.IOUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cat.aubricoc.palaudenoguera.festamajor.adapter.ScheduleDaysListAdapter;
import cat.aubricoc.palaudenoguera.festamajor.model.Day;
import cat.aubricoc.palaudenoguera.festamajor.model.Schedule;
import cat.aubricoc.palaudenoguera.festamajor2016.R;

public class ScheduleFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_schedule, parent, false);

		ViewGroup scheduleList = (ViewGroup) view.findViewById(R.id.schedule_list);
		List<Day> days = readDays();
		new ScheduleDaysListAdapter(scheduleList, days);

		return view;
	}

	private List<Day> readDays() {
		try {
			InputStream inputStream = getResources().openRawResource(R.raw.schedule);
			String json = IOUtils.toString(inputStream);
			Gson gson = new Gson();
			Schedule schedule = gson.fromJson(json, Schedule.class);
			return schedule.getDays();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}