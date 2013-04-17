package test.jplume.petstore;

import static org.junit.Assert.*;

import java.util.List;

import jplume.petstore.domain.Category;
import jplume.petstore.service.CatalogService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CatalogServiceTest {

	private CatalogService service;
	@Before
	public void setUp() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		service  = ctx.getBean(CatalogService.class);
	}

	@Test
	public void test() {
		List<Category> cs = service.getCategoryList();
		for (Category category : cs) {
			System.out.println(category.getDescription());
		}
	}

}
