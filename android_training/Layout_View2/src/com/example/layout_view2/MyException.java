package com.example.layout_view2;

public class MyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7314848564861183743L;

	public MyException() {
		super();
	}

	public MyException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public MyException(String detailMessage) {
		super(detailMessage);
	}

	public MyException(Throwable throwable) {
		super(throwable);
	}

}
