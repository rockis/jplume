package test.jplume;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import jplume.conf.Settings;

import org.junit.Test;

public class SettingsTester extends JPlumeTester {

	@Test
	public void test() {
		assertTrue(Settings.getBoolean("USE_ETAGS", false));
		assertEquals(Settings.get("DEFAULT_CONTENT_TYPE"), "text/html");
		List<String> inters = Settings.getList("INTERCEPTORS");
		assertEquals(inters.size(), 1);
		assertEquals(Settings.get("DEFAULT_TEMPLATE_ENGINE"), "freemarker");
		Map<String, Object> m = Settings.getMap("TEMPLATE_ENGINES");
		@SuppressWarnings("unchecked")
		Map<String, String> fm = (Map)m.get("freemarker");
		assertEquals(fm.get("default_encoding"), "utf-8");
		assertEquals(fm.get("number_format"), "#");
	}

}