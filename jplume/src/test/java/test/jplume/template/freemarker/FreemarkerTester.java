package test.jplume.template.freemarker;

import static org.junit.Assert.*;

import java.io.InputStream;

import jplume.core.Environ;
import jplume.http.Response;
import jplume.template.TemplateEngine;

import org.junit.Before;
import org.junit.Test;

import test.jplume.JPlumeTester;
import test.jplume.ServletContextAdapter;

public class FreemarkerTester extends JPlumeTester {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		Environ.setContext(new ServletContextAdapter());
	}

	@Test
	public void test() throws Exception{
		TemplateEngine engine = TemplateEngine.get();
		Response resp = engine.render(FreemarkerTester.class, "test.html");
		byte[] buf = new byte[1024];
		InputStream is = resp.getContent();
		while(is.read(buf) > 0) {
			System.out.write(buf);
		}
	}

}
