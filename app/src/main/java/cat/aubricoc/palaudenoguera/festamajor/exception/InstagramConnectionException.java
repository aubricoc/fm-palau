package cat.aubricoc.palaudenoguera.festamajor.exception;

import cat.aubricoc.palaudenoguera.festamajor2015.R;

public class InstagramConnectionException extends ConnectionException {

	private static final long serialVersionUID = 1L;

	public InstagramConnectionException() {
		this(null);
	}

	public InstagramConnectionException(Throwable cause) {
		super(cause, R.string.instagram_connection_error);
	}
}
