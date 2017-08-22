package cat.aubricoc.palaudenoguera.festamajor.dao;

import android.content.Context;

import com.canteratech.apa.Dao;

import cat.aubricoc.palaudenoguera.festamajor.db.DatabaseHelper;
import cat.aubricoc.palaudenoguera.festamajor.model.TwitterUser;

public class TwitterUserDao extends Dao<TwitterUser, String> {

	private TwitterUserDao(Context context) {
		super(new DatabaseHelper(context), TwitterUser.class);
	}

	public static TwitterUserDao newInstance(Context context) {
		return new TwitterUserDao(context);
	}
}
