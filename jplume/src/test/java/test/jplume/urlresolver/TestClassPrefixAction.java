package test.jplume.urlresolver;

import jplume.view.annotations.PathVar;
import jplume.view.annotations.Prefix;
import jplume.view.annotations.ViewMethod;

@Prefix(regex="^/test")
public class TestClassPrefixAction {

	@ViewMethod(regex="^/prefix$")
	public static String prefix() {
		return "prefixok";
	}

	@ViewMethod(regex="^/overload$")
	public static String overload() {
		return "empty";
	}
	
	@ViewMethod(regex="^/overload/([\\w]+)$")
	public static String overload(@PathVar String arg1) {
		return arg1;
	}
}
