package test.jplume.urlresolver;

import jplume.view.annotations.View;

public class TestAction4 {

	@View(pattern = "^/dynamic$")
	public String dynamic() {
		return "dynamic";
	}
}
