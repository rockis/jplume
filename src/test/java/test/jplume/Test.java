package test.jplume;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import jplume.http.HttpResponse;
import jplume.http.Response;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		MimetypesFileTypeMap m = new MimetypesFileTypeMap();
		System.out.println(m.getContentType("login.jpg"));
	}

}
