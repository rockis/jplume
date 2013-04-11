package jplume.template;

import java.util.Date;

import jplume.template.annotations.TemplateFunction;

public class Functions {

	@TemplateFunction
	public Date date() {
		return new Date();
	}
}
