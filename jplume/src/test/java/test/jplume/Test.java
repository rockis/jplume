package test.jplume;

import java.util.regex.Pattern;

import jplume.conf.Settings;
import jplume.template.TemplateEngine;
import jplume.view.ErrorHandler;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		Pattern p = Pattern.compile("/export/(pdf|doc)/download/(\\w+)");
		
		long s = System.currentTimeMillis();
		int n = 1000000;
		int a = 20;
		for(int i = 0; i < n; i++) {
//			for(int j = 0; j < a; j++) {
				p.matcher("/export/pdf/download/abc");
//			}
		}
		
		System.out.println((System.currentTimeMillis() - s));
	}
}
