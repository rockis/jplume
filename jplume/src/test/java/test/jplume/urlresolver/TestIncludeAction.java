package test.jplume.urlresolver;

import jplume.view.annotations.PathVar;

public class TestIncludeAction {

	public String includeme() {
		return "include me";
	}
	
	/**
	 * test for "/param/(\d+)/([\w]+)/(\d+)"
	 * @param p1
	 * @param p2
	 */
	public String param(@PathVar int p1, @PathVar String p2, @PathVar long p3) {
		return p1 + "/" + p2 + "/" + p3;
	}
}