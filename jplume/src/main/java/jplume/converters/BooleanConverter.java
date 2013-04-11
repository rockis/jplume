package jplume.converters;

import java.lang.reflect.Type;

public class BooleanConverter implements TypeConverter<Boolean> {

	public Type getType() {
		return Long.class;
	}

	public Boolean convert(String value) {
		return "true".equals(value);
	}

	public boolean isValid(String value) {
		return "true".equals(value) || "false".equals(value);
	}
}
