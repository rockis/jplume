package jplume.core;

public class URLDispatchException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3998734598921148842L;

	public URLDispatchException(String msg) {
		super(msg);
	}
	
	public URLDispatchException(String msg, Throwable e) {
		super(msg, e);
	}
}