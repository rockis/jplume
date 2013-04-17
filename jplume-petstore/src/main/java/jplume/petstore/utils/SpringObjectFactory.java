package jplume.petstore.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jplume.conf.ObjectFactory;
import jplume.core.Environ;

public class SpringObjectFactory implements ObjectFactory {

	private ApplicationContext ctxt;
	
	public SpringObjectFactory() {
		ctxt = WebApplicationContextUtils.getWebApplicationContext(Environ.getContext());
	}
	
	@Override
	public <T> T createObject(Class<T> actionClass) {
		if (actionClass.getAnnotation(Service.class) != null) {
			return ctxt.getBean(actionClass);
		}
		try {
			return actionClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("No unique bean of type [" + actionClass + "] is defined");
		}
	}

}
