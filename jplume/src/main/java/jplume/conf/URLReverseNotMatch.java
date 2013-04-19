package jplume.conf;

public class URLReverseNotMatch extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2796831747746014663L;

	public URLReverseNotMatch(){
		super();
	}
	
	public URLReverseNotMatch(String message) {
		super(message);
	}
}
