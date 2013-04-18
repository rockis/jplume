package jplume.conf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jplume.view.ArgumentBuilder;
import jplume.view.ArgumentBuilder.PathNamedArgument;
import jplume.view.View;

public class URLResolver extends URLResolveProvider {

	private Pattern pattern;

	private String regex;

	private View view;

	public URLResolver(String regex, View view) {
		this.regex = regex;
		this.view = view;
		this.pattern = Pattern.compile(regex);
	}

	public void addRegexPrefix(String regexPrefix) {
		if (this.regex.charAt(0) == '^') {
			this.regex = regexPrefix + this.regex.substring(1);
			this.pattern = Pattern.compile(this.regex);
		} else {
			this.pattern = Pattern.compile(regexPrefix + this.regex);
		}
	}

	public <T> T visit(String path, URLVisitor<T> visitor)
			throws IllegalURLPattern {

		Matcher matcher = pattern.matcher(path);

		// System.out.println(String.format("%s %s", pattern.toString(), path));
		Map<String, String> emptyNamedVars = Collections.emptyMap();
		if (!matcher.matches()) {
			return visitor.visit(pattern, new String[0], emptyNamedVars, view,
					false);
		}
		List<String> indexedVars = new ArrayList<>();
		Map<String, String> namedVars = new HashMap<>();

		ArgumentBuilder argBuilder = view.getArgBuilder();
		if (argBuilder.getPathNamedArgs().size() > 0) {
			for (PathNamedArgument arg : argBuilder.getPathNamedArgs()) {
				String argName = arg.getArgName();
				String argVal = matcher.group(argName);
				if (argVal == null) {
					return visitor.visit(pattern, new String[0],
							emptyNamedVars, view, false);
				}
				namedVars
						.put(arg.getArgName(), matcher.group(arg.getArgName()));
			}
		} else {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				indexedVars.add(matcher.group(i));
			}
		}

		if (!argBuilder.validate(indexedVars.toArray(new String[0]),
				Collections.unmodifiableMap(namedVars))) {
			return visitor.visit(pattern, indexedVars.toArray(new String[0]),
					Collections.unmodifiableMap(namedVars), view, false);
		}

		return visitor.visit(pattern, indexedVars.toArray(new String[0]),
				Collections.unmodifiableMap(namedVars), view, true);
	}

	public String toString() {
		return "<" + this.view + " " + this.regex + ">";
	}
}