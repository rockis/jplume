package test.jplume;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.jplume.template.freemarker.FreemarkerTest;

@RunWith(Suite.class)
@SuiteClasses({ SettingsTest.class, UrlResolverTest.class,  FreemarkerTest.class })
public class AllTests {

}
