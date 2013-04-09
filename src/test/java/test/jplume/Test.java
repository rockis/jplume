package test.jplume;

import java.util.Map;

import jplume.conf.Settings;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		Settings.initalize("jplume-default.json");
		Map<Integer, String> o = Settings.getMap("ERROR_HANDLERS");
		
		System.out.println(o.get(404));
	}
}
