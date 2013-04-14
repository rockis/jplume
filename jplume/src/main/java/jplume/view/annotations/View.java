package jplume.view.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface View {
	
	public String alias() default "";
	
	public String pattern() default "";
	
	public String[] methods() default {};
}
