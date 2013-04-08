package test.jplume;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jplume.core.RequestDispatcher;
import jplume.view.ViewMethod;

import org.junit.Test;

public class UrlResolverTest {

	@Test
	public void test() {

	}

	public static void main(String[] argv) throws Exception{
		RequestDispatcher urr = new RequestDispatcher("jplume/example/example");
//		ViewMethod vm = urr.resolve("");
//		System.out.println(vm);
//		long start = System.currentTimeMillis();
//		for(int i = 0; i < 1; i++) {
//			urr.resolve("");
//			urr.resolve("/help");
//			urr.resolve("/helloworld");
//			urr.resolve("/inc/include");
//		}
////		System.out.println(System.currentTimeMillis() - start);
//		Pattern p = Pattern.compile("^/param/(\\d+)/(?<P2>[\\w]+)/(\\d+)$");
//		Matcher matcher = p.matcher("/param/123/test/456");
//		matcher.matches();
////
////		System.out.println(m.matches());
////		System.out.println(m.group(1));
////		System.out.println(m.group(2));
////		System.out.println(m.group(3));
////		System.out.println(m.group("P2"));
//		
//		Class cz = TestAction3.class;
//		Method m = null;
//		Method[] ms = cz.getMethods();
//		for (int i = 0; i < ms.length; i++) {
//			if (ms[i].getName().equals("param")){
//				m = ms[i];
//				break;
//			}
//		}
//		ViewMethod mw = new ViewMethod(m);
//		System.out.println(mw.match(matcher));
	}
}
