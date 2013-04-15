package jplume.template;

import java.util.Date;
import java.util.List;
import java.util.Map;

import jplume.conf.Settings;
import jplume.conf.URLResolveProvider;
import jplume.conf.URLReverser;
import jplume.template.annotations.TemplateFunctionObject;

@TemplateFunctionObject
public class BuiltinFunctions {

	private URLReverser urlreserver = null;
	
	public BuiltinFunctions() {
		urlreserver = new URLReverser(URLResolveProvider.create(Settings.get("ROOT_URLCONF")));
	}
	
	public Date date() {
		return new Date();
	}
	
	public Date date(long offset) {
		long tt = new Date().getTime() + offset;
		return new Date(tt);
	}
	
	public String url(String classMethodName) {
		int indexOfLastComma = classMethodName.indexOf(':');
		String className = classMethodName.substring(0, indexOfLastComma);
		String methodName = classMethodName.substring(indexOfLastComma + 1);
		return urlreserver.reverse(className, methodName);
	}
	
	public String urli(String classMethodName, List<String> args) {
		int indexOfLastComma = classMethodName.indexOf(':');
		String className = classMethodName.substring(0, indexOfLastComma);
		String methodName = classMethodName.substring(indexOfLastComma + 1);
		return urlreserver.reverse(className, methodName, args.toArray(new String[0]));
	}
	
	public String urln(String classMethodName, Map<String, String> namedArgs) {
		int indexOfLastComma = classMethodName.indexOf(':');
		String className = classMethodName.substring(0, indexOfLastComma);
		String methodName = classMethodName.substring(indexOfLastComma + 1);
		return urlreserver.reverse(className, methodName, namedArgs);
	}
}
