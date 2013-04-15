package test.jplume;

public class Test2 {

	public static void main(String[] args) {
		Integer[] aa = new Integer[] { 99 };
		Class c = aa.getClass();
		System.out.println(c.getComponentType());
		Class[] cs = c.getClasses();
		for (Class class1 : cs) {
			System.out.println(class1);
		}
		
	}
}
