package jplume.petstore.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jplume.core.Environ;
import jplume.http.HttpJsonResponse;
import jplume.http.HttpResponse;
import jplume.http.Request;
import jplume.http.Response;
import jplume.petstore.domain.Account;
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

	// @SpringBean
	// private transient CatalogService catalogService;
	//
	// private Account account = new Account();
	// private List<Product> myList;
	// private boolean authenticated;
	//
	// static {
	// List<String> langList = new ArrayList<String>();
	// langList.add("english");
	// langList.add("japanese");
	// LANGUAGE_LIST = Collections.unmodifiableList(langList);
	//
	// List<String> catList = new ArrayList<String>();
	// catList.add("FISH");
	// catList.add("DOGS");
	// catList.add("REPTILES");
	// catList.add("CATS");
	// catList.add("BIRDS");
	// CATEGORY_LIST = Collections.unmodifiableList(catList);
	// }
	//
	// public Account getAccount() {
	// return this.account;
	// }
	//
	// public String getUsername() {
	// return account.getUsername();
	// }
	//
	// @Validate(required=true, on={"signon", "newAccount", "editAccount"})
	// public void setUsername(String username) {
	// account.setUsername(username);
	// }
	//
	// public String getPassword() {
	// return account.getPassword();
	// }
	//
	// @Validate(required=true, on={"signon", "newAccount", "editAccount"})
	// public void setPassword(String password) {
	// account.setPassword(password);
	// }
	//
	// public List<Product> getMyList() {
	// return myList;
	// }
	//
	// public void setMyList(List<Product> myList) {
	// this.myList = myList;
	// }
	//
	// public List<String> getLanguages() {
	// return LANGUAGE_LIST;
	// }
	//
	// public List<String> getCategories() {
	// return CATEGORY_LIST;
	// }
	//
	// public Resolution newAccountForm() {
	// return new ForwardResolution(NEW_ACCOUNT);
	// }
	//
	// public Resolution newAccount() {
	// accountService.insertAccount(account);
	// account = accountService.getAccount(account.getUsername());
	// myList =
	// catalogService.getProductListByCategory(account.getFavouriteCategoryId());
	// authenticated = true;
	// return new RedirectResolution(CatalogActionBean.class);
	// }
	//
	// public Resolution editAccountForm() {
	// return new ForwardResolution(EDIT_ACCOUNT);
	// }
	//
	// public Resolution editAccount() {
	// accountService.updateAccount(account);
	// account = accountService.getAccount(account.getUsername());
	// myList =
	// catalogService.getProductListByCategory(account.getFavouriteCategoryId());
	// return new RedirectResolution(CatalogActionBean.class);
	// }
	//
	// @DefaultHandler
	// public Resolution signonForm() {
	// return new ForwardResolution(SIGNON);
	// }
	//
	@ViewMethod(regex="^/login$")
	public Response login() {
		Request request  = Environ.getRequest();
		System.out.println(request.getSession().get("user"));
		return render("/account/login.html");
	}
	
	@ViewMethod(regex="^/login/do$")
	public Response check() {
		Request request  = Environ.getRequest();
		Account acc = accountService.getAccount(request.getParam("username"), request.getParam("password"));
		request.getSession().put("user", acc);
		return HttpJsonResponse.ok("");
	}
	
	public void validateCheck(Request request, Validator validator) {
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
	public Response registerAccount() {
		return render("/account/register.html");
	}
	
	@ViewMethod(regex="^/profile$")
	public Response profile() {
		return HttpResponse.ok("ok");
	}
	
	@ViewMethod(regex="^/logout$")
	public Response logout() {
		Environ.getRequest().getSession().clear();
		return HttpResponse.redirect(CatalogAction.class, "index");
	}
}
