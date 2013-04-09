package test.jplume;

import jplume.view.annotations.View;

public class TestAction4 {

	@View(pattern = "^/dynamic$")
	public void dynamic() {
		System.out.println("dynamic");
	}
}
