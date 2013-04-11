package jplume.converters;

import java.lang.reflect.Type;

public class StringConverter implements TypeConverter<String> {

	public Type getType() {
		return String.class;
	}

	public String convert(String value) {
		return value;
	}

	public boolean isValid(String value) {
		return true;
	}

	
}
