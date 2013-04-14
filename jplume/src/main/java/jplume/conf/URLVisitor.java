package jplume.conf;

import java.util.Map;
import java.util.regex.Pattern;

import jplume.view.ViewMethod;

public interface URLVisitor<T>  {

	public T visit(Pattern pattern, String[] pathVars, Map<String, String> namedVars, ViewMethod method, boolean matched);
}
