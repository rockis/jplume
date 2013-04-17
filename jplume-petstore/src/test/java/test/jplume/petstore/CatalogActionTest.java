package test.jplume.petstore;


import java.util.List;

import jplume.petstore.domain.Category;
import jplume.petstore.web.CatalogAction;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CatalogActionTest {

	private CatalogAction action;
	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		action  = ctx.getBean(CatalogAction.class);
	}

	@Test
	public void test() {
		List<Category> cs = action.getCategories();
		for (Category category : cs) {
			System.out.println(category.getDescription());
		}
	}
}
