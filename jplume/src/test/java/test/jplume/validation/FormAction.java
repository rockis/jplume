package test.jplume.validation;

import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.validation.Validator;
import jplume.view.annotations.Form;
import jplume.view.annotations.PathVar;
import jplume.view.annotations.ViewMethod;

public class FormAction {

	@ViewMethod(regex="^/([\\w]+)$")
	public Response submit(@PathVar String category, @Form FormBean form ) {
		System.out.println("run " + category);
		return HttpResponse.ok("ok");
	}

	public Response validateSubmit(String category, Request request, Validator validator) {
		validator.require("id", "Please input id");
		validator.require("name", "Please input your name");
		if (!validator.require("age", "Please input your age")
			|| !validator.numeric("age", "Invalid number")
			|| !validator.range("age", 18, 40, "Age must between 18 to 40")){
			return null;
		}
		validator.include("gender", new String[]{"male", "female"}, "Invalid gender");
		return null;
	}
}
