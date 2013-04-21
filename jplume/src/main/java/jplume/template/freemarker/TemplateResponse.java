package jplume.template.freemarker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import jplume.conf.Settings;
import jplume.http.AbstractResponse;

public class TemplateResponse extends AbstractResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5896753958056165409L;

	private Template ftlTemplate;
	
	private Object data;
	
	public TemplateResponse(Template template, String contentType, Object data) {
		super(HttpServletResponse.SC_OK);
		this.encoding = template.getEncoding();
		this.contentType = contentType;
		this.ftlTemplate = template;
		if (data == null) {
			data = Collections.emptyMap();
		}
		this.data = data;
	}

	public TemplateResponse(Template template, Object context) {
		this(template, Settings.getDefaultContentType(), context);
	}
	
	@Override
	public InputStream getContent() {
		try {
			StringWriter w = new StringWriter();
			ftlTemplate.process(data, w);
			w.close();
			return new ByteArrayInputStream(w.toString().getBytes());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (TemplateException e){
			throw new IllegalStateException(e);
		}
	}
}
