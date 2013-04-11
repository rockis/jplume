package test.jplume;

import jplume.view.annotations.PathVar;

public class TestAction3 {

	public void include() {
		System.out.println("include");
	}
	
	/**
	 * test for "/param/(\d+)/([\w]+)/(\d+)"
	 * @param p1
	 * @param p2
	 */
	public void param(@PathVar int p1, @PathVar String p2, @PathVar long p3) {
		System.out.println("index=" + p1);
		System.out.println("name=" + p2);
	}
}
