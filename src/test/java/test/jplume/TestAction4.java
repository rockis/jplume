package test.jplume;

import jplume.annotations.View;

public class TestAction4 {

	@View(pattern = "^/dynamic$")
	public void dynamic() {
		System.out.println("dynamic");
	}
}
