package jplume.conf;

import java.util.regex.Pattern;

import jplume.view.ViewMethod;

public interface URLVisitor<T>  {

	public T visit(Pattern pattern, String[] pathVars, ViewMethod method);
}
