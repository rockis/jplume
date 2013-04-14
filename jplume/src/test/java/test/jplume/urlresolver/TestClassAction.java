package test.jplume.urlresolver;

import jplume.view.annotations.PathVar;
import jplume.view.annotations.View;

public class TestClassAction {

	public String helloworld() {
		return "hello world";
	}
	
	@View(alias="hello")
	public String helloworld(@PathVar String arg) {
		return "hello " + arg;
	}
	
}
