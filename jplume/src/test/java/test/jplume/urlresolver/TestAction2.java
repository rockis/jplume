package test.jplume.urlresolver;

import jplume.view.annotations.PathVar;

public class TestAction2 {

	public void helloworld() {
		System.out.println("helloworld");
	}
	
	public void helloworld2(String arg, @PathVar int arg2) {
		System.out.println("helloworld");
	}
}
