package jplume.core;

public class NoReverseMatch extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7922641789074919411L;
	
	public NoReverseMatch(String message, Exception e) {
		super(message, e);
	}
}