package cat.aubricoc.palaudenoguera.festamajor.dao;

import cat.aubricoc.palaudenoguera.festamajor.db.DatabaseHelper;
import cat.aubricoc.palaudenoguera.festamajor.model.TwitterUser;

import com.canteratech.androidutils.Activity;
import com.canteratech.apa.Dao;

public class TwitterUserDao extends Dao<TwitterUser, String> {

	private static final TwitterUserDao INSTANCE = new TwitterUserDao();

	private TwitterUserDao() {
		super(new DatabaseHelper(Activity.CURRENT_CONTEXT), TwitterUser.class);
	}

	public static TwitterUserDao getInstance() {
		return INSTANCE;
	}
}
