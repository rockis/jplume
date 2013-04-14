package jplume.template;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jplume.conf.Settings;
import jplume.conf.URLResolveProvider;
import jplume.conf.URLReverser;
import jplume.template.annotations.TemplateFunction;

public class Functions {

	private URLReverser urlreserver = null;
	
	public Functions() {
		urlreserver = new URLReverser(URLResolveProvider.create(Settings.get("URL_ROOTCONF")));
	}
	
	@TemplateFunction
	public Date date() {
		return new Date();
	}
	
	@SuppressWarnings("unchecked")
	@TemplateFunction
	public String url(List<Object> args) {
		String classMethodName = (String)args.get(0);
		int indexOfLastComma = classMethodName.indexOf(':');
		String className = classMethodName.substring(0, indexOfLastComma);
		String methodName = classMethodName.substring(indexOfLastComma + 1);
		if (args.size() == 1) {
			return urlreserver.reverse(className, methodName);
		}
		Object arg2 = args.get(1);
		if (arg2 instanceof Map) {
			Map<String, String> vars = new HashMap<String, String>();
			Map<Object, Object> map = (Map<Object, Object>)arg2;
			for(Map.Entry<Object, Object> e : map.entrySet()) {
				vars.put(e.getKey().toString(), e.getValue().toString());
			}
			return urlreserver.reverse(className, methodName, vars);
		}else{
			List<String> vars = new ArrayList<>();
			for (int i = 1; i < args.size(); i++) {
				vars.add(args.get(i).toString());
			}
			return urlreserver.reverse(className, methodName, vars.toArray(new String[0]));
		}
	}
}
