package jplume.conf;

public class URLReserveException extends RuntimeException{

	private static final long serialVersionUID = 3998734598921148842L;

	public URLReserveException(String msg) {
		super(msg);
	}
	
	public URLReserveException(String msg, Throwable e) {
		super(msg, e);
	}

}
