package com.overflow.libs.core;

public class CoreException extends RuntimeException {
	public CoreException(String message) {
		super(message);
	}

	public CoreException(Throwable cause) {
		super(cause);
	}

	public CoreException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}

	@Override
	public String toString() {
		return getMessage();
	}

}
