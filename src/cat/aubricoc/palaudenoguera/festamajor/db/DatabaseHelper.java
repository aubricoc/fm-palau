package cat.aubricoc.palaudenoguera.festamajor.db;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import cat.aubricoc.palaudenoguera.festamajor.model.Tweet;
import cat.aubricoc.palaudenoguera.festamajor.model.TwitterUser;
import cat.aubricoc.palaudenoguera.festamajor.utils.Constants;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context) {
		super(context, Constants.DATABASE_NAME, null,
				Constants.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(Constants.PROJECT_NAME, "Create DB...");

		List<String> createTables = DatabaseReflection.getInstance()
				.prepareCreateTables(Tweet.class, TwitterUser.class);

		for (String sql : createTables) {
			db.execSQL(sql);
		}

		Log.i(Constants.PROJECT_NAME, "Create DB...OK");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(Constants.PROJECT_NAME, "Upgrade DB from " + oldVersion + " to "
				+ newVersion + "...");

		if (oldVersion < 2) {
			EntityInfo tweetInfo = EntityInfo.getEntityInfo(Tweet.class);
			EntityInfo twitterUserInfo = EntityInfo.getEntityInfo(TwitterUser.class);
			db.delete(tweetInfo.getTableName(), null, null);
			db.delete(twitterUserInfo.getTableName(), null, null);
		}

		Log.i(Constants.PROJECT_NAME, "Upgrade DB from " + oldVersion + " to "
				+ newVersion + "...OK");
	}
}
