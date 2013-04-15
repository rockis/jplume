package jplume.template.freemarker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.Collections12;

public class FunctionWrapper implements TemplateMethodModelEx,
		TemplateSequenceModel {

	private final String name;
	private final Object object;
	private final Map<String, Method> methods = new HashMap<>();
	private final BeansWrapper wrapper;

	public FunctionWrapper(Object object, String name, Method[] methods,
			BeansWrapper wrapper) {
		this.name = name;
		this.object = object;
		this.wrapper = wrapper;
		for (Method method : methods) {
			int argc = method.getParameterTypes().length;
			String nameArgc = name + ":" + argc;
			if (this.methods.containsKey(nameArgc)) {
				throw new IllegalArgumentException("Ambiguous method '" + name + "' with " + argc + " + arguments");
			}
			this.methods.put(nameArgc, method);
		}
	}

	@Override
	public TemplateModel get(int index) throws TemplateModelException {
		return (TemplateModel) exec(Collections12
				.singletonList(new SimpleNumber(new Integer(index))));
	}

	@Override
	public int size() throws TemplateModelException {
		throw new TemplateModelException("?size is unsupported for: "
				+ getClass().getName());
	}

	public String toString() {
		return name;
	}

	@Override
	public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException {
		Method method = null;
		try {
			method = methods.get(name + ":" + arguments.size());
			if (method == null) {
				throw new TemplateModelException("No such method '" + name
						+ "(" + arguments.size() + ")");
			}
			return method.invoke(
					object,
					unwrapArguments(arguments, method.getParameterTypes(),
							wrapper));
		} catch (TemplateModelException e) {
			throw e;
		} catch (Exception e) {
			while (e instanceof InvocationTargetException) {
				Throwable t = ((InvocationTargetException) e)
						.getTargetException();
				if (t instanceof Exception) {
					e = (Exception) t;
				} else {
					break;
				}
			}
			if ((method.getModifiers() & Modifier.STATIC) != 0) {
				throw new TemplateModelException("Method " + method
						+ " threw an exception", e);
			} else {
				throw new TemplateModelException("Method " + method
						+ " threw an exception when invoked on " + object, e);
			}
		}
	}

	static Object[] unwrapArguments(@SuppressWarnings("rawtypes") List args, Class<?>[] types, BeansWrapper w)
			throws TemplateModelException {
		if (args == null)
			return null;

		Object[] unwrappedArgs = new Object[types.length];
		int argIndex = 0;
		for (Object arg : args) {
			unwrappedArgs[argIndex] = w.unwrap((TemplateModel) arg,
					types[argIndex]);
			argIndex++;
		}
		return unwrappedArgs;
	}
}
