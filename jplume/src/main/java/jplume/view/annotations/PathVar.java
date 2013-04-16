/* Copyright 2012-2013 Ray Cheung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jplume.view.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation used for injecting URL path variable to View method's parameters</p>
 *
 * <p>eg. URL path pattern <i>'^/media/([\w]+)$'</i> routing to view method:<p>
 * <p>public void foo(@PathVar String var) { ... }</p>
 * <p>The value of parameter <b>var</b> will be '([\w]+)'
 * <p>If the URL path pattern use named group, eg <i>'^/media/(?&lt;varname&gt;[\w])$'</i></p>
 * <p>The PathVar must be given a specific name, for example</p>
 * <p>public void foo(@PathVar(name="varname") String var) { ... }</p>
 * @author Ray Cheung
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PathVar {
	
	public String name() default "";
	
}
