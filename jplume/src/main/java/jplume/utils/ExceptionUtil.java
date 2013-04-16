package jplume.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

import org.slf4j.Logger;

public class ExceptionUtil {

	public static Throwable last(Throwable e) {
		while(e.getCause() != null) {
			e = e.getCause();
		}
		return e;
	}
	
	public static void logScriptException(Logger logger, ScriptException e, URL scriptfile) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(scriptfile.openStream()));
		List<String> contents = new ArrayList<>();
		String line = br.readLine();
		while(line != null) {
			contents.add(line);
			line = br.readLine();
		}

		int lineNumber = e.getLineNumber();
		int scriptLine = lineNumber;
		if (scriptLine < 5) {
			scriptLine = 1;
		}else{
			scriptLine -= 5;
		}
		logger.error(e.getMessage());
		while(scriptLine < lineNumber + 5 && scriptLine <= contents.size()) {
			logger.error(String.format("%s %s", (scriptLine == lineNumber ? ">" : " "), contents.get(scriptLine - 1)));
			scriptLine++;
		}
	}

}
