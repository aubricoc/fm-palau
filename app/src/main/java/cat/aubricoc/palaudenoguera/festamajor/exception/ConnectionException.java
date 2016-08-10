package cat.aubricoc.palaudenoguera.festamajor.exception;

import com.canteratech.androidutils.Activity;

import cat.aubricoc.palaudenoguera.festamajor2016.R;

public class ConnectionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConnectionException() {
		this(null);
	}

	public ConnectionException(Throwable cause) {
		this(cause, R.string.connection_error);
	}
	
	public ConnectionException(Throwable cause, int messageId) {
		super(Activity.CURRENT_CONTEXT.getString(messageId),
				cause);
	}
}
