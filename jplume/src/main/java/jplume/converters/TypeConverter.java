package jplume.converters;

import java.lang.reflect.Type;

public interface TypeConverter<T> {

	public Type getType();
	
	public T convert(String value);
	
	public boolean isValid(String value);
	
}
