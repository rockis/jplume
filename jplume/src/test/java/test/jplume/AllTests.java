package test.jplume;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.jplume.template.freemarker.FreemarkerTester;

@RunWith(Suite.class)
@SuiteClasses({ SettingsTester.class, UrlResolverTester.class,  FreemarkerTester.class })
public class AllTests {

}
