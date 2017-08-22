package cat.aubricoc.palaudenoguera.festamajor.dao;

import android.content.Context;

import com.canteratech.apa.Dao;

import cat.aubricoc.palaudenoguera.festamajor.db.DatabaseHelper;
import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;

public class TweetDao extends Dao<Tweet, String> {

	private TweetDao(Context context) {
		super(new DatabaseHelper(context), Tweet.class);
	}

	public static TweetDao newInstance(Context context) {
		return new TweetDao(context);
	}

	public Tweet getFirstResultOrderByIdAsc() {
		return getFirstResult("id");
	}
}
