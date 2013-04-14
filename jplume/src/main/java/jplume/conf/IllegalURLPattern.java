package jplume.conf;

public class IllegalURLPattern extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3998734598921148842L;

	public IllegalURLPattern(String msg) {
		super(msg);
	}
	
	public IllegalURLPattern(String msg, Throwable e) {
		super(msg, e);
	}
}