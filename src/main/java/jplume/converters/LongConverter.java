package jplume.converters;

import java.lang.reflect.Type;

public class LongConverter implements TypeConverter<Long> {

	public Type getType() {
		return Long.class;
	}

	public Long convert(String value) {
		return Long.parseLong(value);
	}

	public boolean isValid(String value) {
		try {
			Long.parseLong(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
