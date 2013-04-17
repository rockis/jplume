package jplume.conf;

import java.util.Map;
import java.util.regex.Pattern;

import jplume.view.View;

public interface URLVisitor<T>  {

	public T visit(Pattern pattern, String[] pathVars, Map<String, String> namedVars, View method, boolean matched);
}
