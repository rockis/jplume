package jplume.view.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ViewMethod {
	
	public String alias() default "";
	
	public String regex() default "";
	
	public String[] methods() default {};
}
