package test.jplume.urlresolver;

import jplume.view.annotations.Prefix;
import jplume.view.annotations.ViewMethod;

@Prefix(regex="^/includeclass")
public class TestClassIncludeAction {

	@ViewMethod(regex="^/prefix$")
	public static String prefix2() {
		return "prefixok";
	}

	
}

