package test.jplume.urlresolver;

import jplume.view.annotations.PathVar;
import jplume.view.annotations.ViewMethod;

public class TestClassAction {

	public static String helloworld() {
		return "hello world";
	}
	
	@ViewMethod(alias="hello")
	public static String helloworld(@PathVar String arg) {
		return "hello " + arg;
	}
	
}
