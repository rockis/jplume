package jplume.converters;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Converter {

	private static Map<Type, TypeConverter<?>> converters = new HashMap<Type, TypeConverter<?>>();
	
	static {
		converters.put(Integer.class, new IntegerConverter());
		converters.put(int.class, new IntegerConverter());
		converters.put(Long.class, new LongConverter());
		converters.put(long.class, new LongConverter());
		converters.put(Double.class, new DoubleConverter());
		converters.put(double.class, new DoubleConverter());
		converters.put(Float.class, new FloatConverter());
		converters.put(float.class, new FloatConverter());
		converters.put(Boolean.class, new BooleanConverter());
		converters.put(double.class, new BooleanConverter());
		converters.put(String.class, new StringConverter());
	}
	
	@SuppressWarnings("unchecked")
	public static <T>T convert(T type, String value) {
		if (converters.containsKey(type)) {
			return (T) converters.get(type).convert(value);
		}
		return null;
	}
	
	public static boolean  isValid(Class<?> type, String value) {
		if (converters.containsKey(type)) {
			return converters.get(type).isValid(value);
		}
		return false;
	}
}
