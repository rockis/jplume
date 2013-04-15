package test.jplume.urlresolver;

import jplume.view.annotations.Prefix;
import jplume.view.annotations.View;

@Prefix(regex="^/test")
public class TestClassPrefixAction {

	@View(regex="^/prefix$")
	public static String prefix() {
		return "prefixok";
	}

}
