package jplume.template.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jplume.utils.StringUtil;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class StaticClassModel  implements TemplateHashModel {

    private Class<?> clazz;
    private BeansWrapper wrapper;

    public StaticClassModel(Class<?> clazz, BeansWrapper wrapper) {
        this.clazz = clazz;
        this.wrapper = wrapper;
    }

    public TemplateModel get(String key) throws TemplateModelException {
        if (key == null) {
            return null;
        }
        if (isField(key)) {
            return getAttribute(key);
        } else {
            return invokeMethod(key);
        }
    }

    private boolean isField(String key) {
        try {
            clazz.getDeclaredField(key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private TemplateModel getAttribute(String key)
            throws TemplateModelException {
        try {
            Field field = clazz.getDeclaredField(key);
            return wrapper.getOuterIdentity().wrap(field.get(null));
        } catch (Exception e) {
            throw new TemplateModelException(e);
        }
    }

    private TemplateModel invokeMethod(String key)
            throws TemplateModelException {
        Method method = null;
        try {
            String methodName = "get" + StringUtil.capitalize(key);
            method = clazz.getDeclaredMethod(methodName, new Class[0]);
        } catch (Exception e) {
        }
        if (method == null) {
            try {
                String methodName = "is" + StringUtil.capitalize(key);
                method = clazz.getDeclaredMethod(methodName, new Class[0]);
            } catch (Exception e) {
            }
        }
        if (method == null) {
            throw new TemplateModelException("No such key:" + key);
        }
        Object retval;
        try {
            retval = method.invoke(null, new Object[0]);
        } catch (Exception e) {
            throw new TemplateModelException(e);
        }
        return method.getReturnType() == Void.TYPE ? TemplateModel.NOTHING
                : wrapper.getOuterIdentity().wrap(retval);
    }

    public boolean isEmpty() throws TemplateModelException {
        return clazz == null;
    }
}
