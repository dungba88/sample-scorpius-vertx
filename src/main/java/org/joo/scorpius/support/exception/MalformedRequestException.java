package org.joo.scorpius.support.exception;

public class MalformedRequestException extends Exception {

	private static final long serialVersionUID = 5651707579614655859L;
	
	public MalformedRequestException(String msg) {
		super(msg);
	}

	public MalformedRequestException(Throwable cause) {
		super(cause);
	}

	public MalformedRequestException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
