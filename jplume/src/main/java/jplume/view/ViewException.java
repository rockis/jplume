package jplume.view;

public class ViewException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8582849250653434929L;

	public ViewException(String message) {
		super(message);
	}
	
	public ViewException(String message, Throwable e) {
		super(message, e);
	}
	
	public ViewException(Throwable e) {
		super(e);
	}
}
