package cat.aubricoc.palaudenoguera.festamajor.exception;

import android.content.Context;

import cat.aubricoc.palaudenoguera.festamajor2017.R;

public class TwitterConnectionException extends ConnectionException {

	private static final long serialVersionUID = 1L;

	public TwitterConnectionException(Context context) {
		this(null, context);
	}

	public TwitterConnectionException(Throwable cause, Context context) {
		super(cause, context.getString(R.string.twitter_connection_error));
	}
}
