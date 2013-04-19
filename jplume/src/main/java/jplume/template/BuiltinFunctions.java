package jplume.template;

import java.util.Date;
import java.util.List;
import java.util.Map;

import jplume.core.Environ;
import jplume.template.annotations.TemplateFunctionObject;
import jplume.utils.ClassUtil;

@TemplateFunctionObject
public class BuiltinFunctions {

	public BuiltinFunctions() {
	}
	
	public Date date() {
		return new Date();
	}
	
	public Date date(long offset) {
		long tt = new Date().getTime() + offset;
		return new Date(tt);
	}
	
	public String url(String classMethodName) throws ClassNotFoundException{
		int indexOfLastComma = classMethodName.indexOf(':');
		String className = classMethodName.substring(0, indexOfLastComma);
		String methodName = classMethodName.substring(indexOfLastComma + 1);
		return Environ.reverseURL(ClassUtil.forName(className), methodName);
	}
	
	public String urli(String classMethodName, List<String> args) throws ClassNotFoundException {
		int indexOfLastComma = classMethodName.indexOf(':');
		String className = classMethodName.substring(0, indexOfLastComma);
		String methodName = classMethodName.substring(indexOfLastComma + 1);
		return Environ.reverseURL(ClassUtil.forName(className), methodName, args.toArray(new String[0]));
	}
	
	public String urln(String classMethodName, Map<String, String> namedArgs) throws ClassNotFoundException {
		int indexOfLastComma = classMethodName.indexOf(':');
		String className = classMethodName.substring(0, indexOfLastComma);
		String methodName = classMethodName.substring(indexOfLastComma + 1);
		return Environ.reverseURL(ClassUtil.forName(className), methodName, namedArgs);
	}
}
