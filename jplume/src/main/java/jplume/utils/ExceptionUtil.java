package jplume.utils;

public class ExceptionUtil {

	public static Throwable last(Throwable e) {
		while(e.getCause() != null) {
			e = e.getCause();
		}
		return e;
	}

}
