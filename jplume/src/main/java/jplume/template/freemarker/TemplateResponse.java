package jplume.template.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import jplume.http.AbstractResponse;

public class TemplateResponse extends AbstractResponse {

	private Template ftlTemplate;
	
	private Object context;
	
	public TemplateResponse(Template template, Object context) {
		super(200);
		this.setCharset(template.getEncoding());
		this.ftlTemplate = template;
		if (context == null) {
			context = Collections.emptyMap();
		}
		this.context = context;
	}
	
	@Override
	public void apply(HttpServletResponse resp) {
		try {
			super.apply(resp);
			Writer w = resp.getWriter();
			ftlTemplate.process(context, w);
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e){
			e.printStackTrace();
		}
	}
}
