package test.jplume.urlresolver;

import jplume.view.annotations.Prefix;
import jplume.view.annotations.View;

@Prefix(regex="^/includeclass")
public class TestClassIncludeAction {

	@View(regex="^/prefix$")
	public static String prefix2() {
		return "prefixok";
	}

}

