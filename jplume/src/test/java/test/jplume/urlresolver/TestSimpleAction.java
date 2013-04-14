package test.jplume.urlresolver;

import jplume.view.annotations.PathVar;

public class TestSimpleAction {

	public void index() {
		System.out.println("index");
	}
	
	public void indexedVars(@PathVar String arg1, @PathVar String arg2) {
		System.out.println("help " + arg1 + "," + arg2);
	}
	
	public void namedVars(@PathVar(name="arg1") String arg1, @PathVar(name="arg2") String arg2) {
		System.out.println("help " + arg1 + "," + arg2);
	}
	
	public void indexedNamedVars(@PathVar(name="arg1") String arg1, @PathVar(name="arg2") String arg2) {
		System.out.println("help " + arg1 + "," + arg2);
	}
}
