package cat.aubricoc.palaudenoguera.festamajor.exception;

import cat.aubricoc.palaudenoguera.festamajor2014.R;

public class TwitterConnectionException extends ConnectionException {

	private static final long serialVersionUID = 1L;

	public TwitterConnectionException() {
		this(null);
	}

	public TwitterConnectionException(Throwable cause) {
		super(cause, R.string.twitter_connection_error);
	}
}
