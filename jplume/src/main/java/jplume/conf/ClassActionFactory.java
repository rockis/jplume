package jplume.conf;

public class ClassActionFactory implements ObjectFactory {

	@Override
	public <T> T createObject(Class<T> actionClass) {
		try {
			return actionClass.newInstance();
		} catch (InstantiationException |  IllegalAccessException e) {
			throw new IllegalStateException("Couldn't create instance of " + actionClass);
		}
	}

}