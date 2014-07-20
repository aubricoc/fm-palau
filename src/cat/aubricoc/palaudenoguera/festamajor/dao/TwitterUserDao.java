package cat.aubricoc.palaudenoguera.festamajor.dao;

import cat.aubricoc.palaudenoguera.festamajor.model.TwitterUser;

public class TwitterUserDao extends Dao<TwitterUser, String> {

	private static final TwitterUserDao INSTANCE = new TwitterUserDao();

	private TwitterUserDao() {
		super(TwitterUser.class);
	}

	public static TwitterUserDao getInstance() {
		return INSTANCE;
	}
}
