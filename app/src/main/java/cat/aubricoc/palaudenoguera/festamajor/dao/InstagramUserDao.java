package cat.aubricoc.palaudenoguera.festamajor.dao;

import com.canteratech.androidutils.Activity;
import com.canteratech.apa.Dao;

import cat.aubricoc.palaudenoguera.festamajor.db.DatabaseHelper;
import cat.aubricoc.palaudenoguera.festamajor.model.InstagramUser;

public class InstagramUserDao extends Dao<InstagramUser, String> {

	private static final InstagramUserDao INSTANCE = new InstagramUserDao();

	private InstagramUserDao() {
		super(new DatabaseHelper(Activity.CURRENT_CONTEXT), InstagramUser.class);
	}

	public static InstagramUserDao getInstance() {
		return INSTANCE;
	}
}
