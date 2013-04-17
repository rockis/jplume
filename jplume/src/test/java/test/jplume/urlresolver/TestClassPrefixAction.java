package test.jplume.urlresolver;

import jplume.view.annotations.Prefix;
import jplume.view.annotations.ViewMethod;

@Prefix(regex="^/test")
public class TestClassPrefixAction {

	@ViewMethod(regex="^/prefix$")
	public static String prefix() {
		return "prefixok";
	}

}
