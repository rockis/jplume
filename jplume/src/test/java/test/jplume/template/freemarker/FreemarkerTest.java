package test.jplume.template.freemarker;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import jplume.conf.Settings;
import jplume.core.ActionContext;
import jplume.http.Response;
import jplume.template.TemplateEngine;

import org.junit.Before;
import org.junit.Test;

import test.jplume.HttpServletResponseAdapter;
import test.jplume.ServletContextAdapter;

public class FreemarkerTest {

	@Before
	public void setUp() throws Exception {
		Settings.initalize("jplume-test.json");
		ActionContext.setContext(new ServletContextAdapter());
	}

	@Test
	public void test() {
		TemplateEngine engine = TemplateEngine.get();
		Response resp = engine.render(FreemarkerTest.class, "test.html");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final PrintWriter pw = new PrintWriter(bos);
		resp.apply(new HttpServletResponseAdapter(){
			@Override
			public PrintWriter getWriter() throws IOException {
				return pw;
			}
		});
		pw.flush();
		System.out.println(bos.toString());
	}

}
