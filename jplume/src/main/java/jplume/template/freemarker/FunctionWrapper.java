package jplume.template.freemarker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class FunctionWrapper implements TemplateMethodModelEx {

	private Object object;
	private Method method;
	public FunctionWrapper(Object object, Method method) {
		this.object = object;
		this.method = method;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		try {
			return method.invoke(object, arguments.toArray(new Object[0]));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new TemplateModelException(e);
		}
	}

}
