package jplume.converters;

import java.lang.reflect.Type;

public class DoubleConverter implements TypeConverter<Double> {

	public Type getType() {
		return Double.class;
	}

	public Double convert(String value) {
		return Double.parseDouble(value);
	}

	public boolean isValid(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
