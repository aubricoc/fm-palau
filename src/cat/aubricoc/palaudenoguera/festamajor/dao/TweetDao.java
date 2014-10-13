package cat.aubricoc.palaudenoguera.festamajor.dao;

import cat.aubricoc.palaudenoguera.festamajor.activity.Activity;
import cat.aubricoc.palaudenoguera.festamajor.db.DatabaseHelper;
import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;

import com.canteratech.apa.Dao;

public class TweetDao extends Dao<Tweet, String> {

	private static final TweetDao INSTANCE = new TweetDao();

	private TweetDao() {
		super(new DatabaseHelper(Activity.CURRENT_CONTEXT), Tweet.class);
	}

	public static TweetDao getInstance() {
		return INSTANCE;
	}

	public Tweet getFirstResultOrderByIdAsc() {
		return getFirstResult("id");
	}
}
