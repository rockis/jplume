package jplume.converters;

import java.lang.reflect.Type;

public class FloatConverter implements TypeConverter<Float> {

	public Type getType() {
		return Float.class;
	}

	public Float convert(String value) {
		return Float.parseFloat(value);
	}

	public boolean isValid(String value) {
		try {
			Float.parseFloat(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
