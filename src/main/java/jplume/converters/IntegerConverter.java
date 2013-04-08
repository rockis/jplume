package jplume.converters;

import java.lang.reflect.Type;

public class IntegerConverter implements TypeConverter<Integer> {

	public Type getType() {
		return Integer.class;
	}

	public Integer convert(String value) {
		return Integer.parseInt(value);
	}

	public boolean isValid(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
}
