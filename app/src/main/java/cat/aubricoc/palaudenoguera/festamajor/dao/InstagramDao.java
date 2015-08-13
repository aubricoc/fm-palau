package cat.aubricoc.palaudenoguera.festamajor.dao;

import com.canteratech.androidutils.Activity;
import com.canteratech.apa.Dao;

import cat.aubricoc.palaudenoguera.festamajor.db.DatabaseHelper;
import cat.aubricoc.palaudenoguera.festamajor.model.Instagram;

public class InstagramDao extends Dao<Instagram, String> {

	private static final InstagramDao INSTANCE = new InstagramDao();

	private InstagramDao() {
		super(new DatabaseHelper(Activity.CURRENT_CONTEXT), Instagram.class);
	}

	public static InstagramDao getInstance() {
		return INSTANCE;
	}

	public Instagram getFirstResultOrderByIdAsc() {
		return getFirstResult("id");
	}
}
