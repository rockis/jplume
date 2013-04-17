package jplume.conf;

public interface ObjectFactory {

	public <T> T createObject(Class<T> actionClass);
	
}
