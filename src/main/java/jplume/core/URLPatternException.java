package jplume.core;

public class URLPatternException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 487320529528224560L;

	public URLPatternException(String msg) {
		super(msg);
	}
	
	public URLPatternException(String msg, Throwable e) {
		super(msg, e);
	}
}
