package cat.aubricoc.palaudenoguera.festamajor.dao;

import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;

public class TweetDao extends Dao<Tweet, String> {

	private static final TweetDao INSTANCE = new TweetDao();

	private TweetDao() {
		super(Tweet.class);
	}

	public static TweetDao getInstance() {
		return INSTANCE;
	}

	public Tweet getFirstResultOrderByIdAsc() {
		return getFirstResult("id");
	}
}
