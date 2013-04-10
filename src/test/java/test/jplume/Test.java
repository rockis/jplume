package test.jplume;

import jplume.conf.Settings;
import jplume.template.TemplateEngine;
import jplume.view.ErrorHandler;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		Settings.initalize("jplume-default.json");
		TemplateEngine et = TemplateEngine.get();
		et.render(ErrorHandler.class, "404.html");
		
	}
}
