package jplume.petstore.web;

import java.io.Serializable;

import jplume.http.Response;
import jplume.template.TemplateEngine;

abstract class AbstractAction implements Serializable {

	private static final long serialVersionUID = -1767714708233127983L;

	public Response render(String template) {
		return TemplateEngine.get().render(template);
	}

	public Response render(String template, Object data) {
		return TemplateEngine.get().render(template, data);
	}
	// protected transient ActionBeanContext context;
	//
	// protected void setMessage(String value) {
	// context.getMessages().add(new SimpleMessage(value));
	// }
	//
	// public ActionBeanContext getContext() {
	// return context;
	// }
	//
	// public void setContext(ActionBeanContext context) {
	// this.context = context;
	// }

}
