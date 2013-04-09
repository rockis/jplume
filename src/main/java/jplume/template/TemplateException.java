package jplume.template;

public class TemplateException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8918366965846365974L;

	
	public TemplateException(String message) {
		super(message);
	}

	public TemplateException(String message, Throwable e) {
		super(message, e);
	}

}
