package test.jplume;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Test2 {

	public static void main(String[] args) {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine jsEngine = sem.getEngineByName("js");
		try {
			jsEngine.eval("a=1; \n a = b;");
		} catch (ScriptException e) {
			System.out.println(e.getLineNumber());
			e.printStackTrace();
		}
	}
}
