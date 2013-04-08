package jplume.conf;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Settings {

	public static final String JPLUME_USER_CONFIG_FILE = "USER_CONFIG_FILE";
	
	private static ScriptEngine jsEngine = null;
	
	private static final String defaultConfigFile = "jplume-default.json";
	
	
	public static void initalize(String userConfigFile) throws InvalidConfigException {
		ScriptEngineManager sem = new ScriptEngineManager();
		jsEngine = sem.getEngineByName("js");
		readConfig(defaultConfigFile);
		if (userConfigFile != null && !userConfigFile.isEmpty()){
			readConfig(userConfigFile);
		}
	}
	
	private static void readConfig(String configFile) throws InvalidConfigException {
		try {
			URL config = Settings.class.getClassLoader().getResource(configFile);
			if (config == null) {
				throw new InvalidConfigException("Could not read config file '"
					+ configFile + "'");
			}
			jsEngine.eval(new InputStreamReader(config.openStream()));
		} catch (ScriptException e) {
			throw new InvalidConfigException("The config file '" + configFile + "' has error ", e);
		} catch (IOException e) {
			throw new InvalidConfigException("Could not read config file '"
					+ configFile + "'", e);
		}
	}
	
	public static String get(String key, String defaultVal){
		Object val =  jsEngine.get(key);
		return val == null ? defaultVal : val.toString();
	}
	
	public static String get(String key){
		return get(key, null);
	}
	
	public static String defaultCharset() {
		return get("DEFAULT_CHARSET", "utf-8");
	}
	
	public static String defaultContentType() {
		return get("DEFAULT_CONTENT_TYPE", "text/html");
	}
}