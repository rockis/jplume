package jplume.conf;

public class InvalidConfigException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7424718432708557333L;

	public InvalidConfigException(String message){
		super(message);
	}
	
	public InvalidConfigException(String message, Throwable e){
		super(message, e);
	}
}
