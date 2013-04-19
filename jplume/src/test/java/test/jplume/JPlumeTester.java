package test.jplume;

import jplume.conf.Settings;
import jplume.conf.URLResolveProvider;
import jplume.conf.URLReverser;
import jplume.core.Environ;

import org.junit.Before;

public abstract class JPlumeTester {

	protected URLResolveProvider urp = null;

	@Before
	public void setUp() throws Exception {
		Settings.initalize("jplume-test.json");
		urp =  URLResolveProvider.create(Settings.get("ROOT_URLCONF"));
		Environ.setUrlReverser(new URLReverser(urp));
	}


}
