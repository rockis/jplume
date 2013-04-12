package test.jplume;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import jplume.conf.Settings;

import org.junit.Before;
import org.junit.Test;

public class SettingsTest {

	@Before
	public void setUp() throws Exception {
		Settings.initalize("jplume-default.json");
	}

	@Test
	public void test() {
		assertTrue(Settings.getBoolean("USE_ETAGS", false));
		assertEquals(Settings.get("DEFAULT_CONTENT_TYPE"), "text/html");
		List<String> inters = Settings.getList("INTERCEPTORS");
		assertEquals(inters.size(), 1);
		Map<String, Object> m = Settings.getMap("TEMPLATE_ENGINE");
		assertEquals(m.get("default"), "freemarker");
		@SuppressWarnings("unchecked")
		Map<String, String> fm = (Map)m.get("freemarker");
		assertEquals(fm.get("default_encoding"), "utf-8");
		assertEquals(fm.get("number_format"), "#");
	}

}