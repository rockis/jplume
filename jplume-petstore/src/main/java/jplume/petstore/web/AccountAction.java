package jplume.petstore.web;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jplume.core.Environ;
import jplume.http.HttpJsonResponse;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.petstore.domain.Account;
import jplume.petstore.domain.Cart;
import jplume.petstore.service.AccountService;
import jplume.validation.Validator;
import jplume.view.annotations.Prefix;
import jplume.view.annotations.ViewMethod;

@Service
@Prefix(regex="^/account")
public class AccountAction extends AbstractAction {

	private static final long serialVersionUID = 5499663666155758178L;

	//
	// private static final List<String> LANGUAGE_LIST;
	// private static final List<String> CATEGORY_LIST;
	//
	@Autowired
	private transient AccountService accountService;

	@ViewMethod(regex="^/signin$")
	public Response signIn() {
		Request request  = Environ.getRequest();
		System.out.println(request.getSession().get("user"));
		return render("/account/signin.html");
	}
	
	@ViewMethod(regex="^/signin/do$")
	public Response doSignIn() {
		Request request  = Environ.getRequest();
		Account acc = accountService.getAccount(request.getParam("username"), request.getParam("password"));
		request.getSession().put("user", acc);
		request.getSession().put("cart", new Cart());
		return HttpJsonResponse.ok();
	}
	
	public void validateDoSignIn(Request request, Validator validator) {
		if (validator.require("username", "Missing username. Please correct and try again.")
				&& validator.require("password", "Missing password. Please correct and try again.")) {
			Account acc = accountService.getAccount(request.getParam("username"), request.getParam("password"));
			if (acc == null) {
				validator.addError("Incorrect username or password");
			}
		}
	}
	
	@ViewMethod(regex="^/register$")
	public Response register() {
		return render("/account/register.html");
	}
	
	@ViewMethod(regex="^/register/do$")
	public Response doRegister() {
		Request req = Environ.getRequest();
		Account acc = new Account();
		try {
			System.out.println(req.getParam("languagePrefrence"));
			BeanUtils.populate(acc, req.getParams());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(acc.getLanguagePreference());
		accountService.insertAccount(acc);
		return HttpJsonResponse.ok();
	}
	
	public void validateDoRegister(Request request, Validator validator) {
		
	}
	
	@ViewMethod(regex="^/profile$")
	public Response profile() {
		Request request = Environ.getRequest();
		Account account = (Account)request.getSession().get("user");
		Map<String, Object> data = new HashMap<>();
		data.put("account", account);
		return render("/account/profile.html", data);
	}
		
	@ViewMethod(regex="^/profile/update$")
	public Response updateProfile() {
		return HttpResponse.ok("ok");
	}
	
	@ViewMethod(regex="^/signout$")
	public Response signOut() {
		Environ.getRequest().getSession().clear();
		return HttpResponse.redirect(CatalogAction.class, "index");
	}
}
