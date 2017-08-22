package cat.aubricoc.palaudenoguera.festamajor.exception;

import android.content.Context;

import cat.aubricoc.palaudenoguera.festamajor2017.R;

public class ConnectionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConnectionException(Context context) {
		this(null, context);
	}

	public ConnectionException(Throwable cause, Context context) {
		this(cause, context.getString(R.string.connection_error));
	}

	public ConnectionException(Throwable cause, String message) {
		super(message, cause);
	}
}
