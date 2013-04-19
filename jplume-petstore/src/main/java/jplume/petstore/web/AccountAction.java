package jplume.petstore.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jplume.http.HttpResponse;
import jplume.http.Response;
import jplume.petstore.domain.Account;
import jplume.petstore.domain.Product;
import jplume.petstore.service.AccountService;
import jplume.petstore.service.CatalogService;
import jplume.view.annotations.Prefix;
import jplume.view.annotations.ViewMethod;

@Service
@Prefix(regex="^/account")
public class AccountAction extends AbstractAction {

	private static final long serialVersionUID = 5499663666155758178L;

	private static final String NEW_ACCOUNT = "/WEB-INF/jsp/account/NewAccountForm.jsp";
	private static final String EDIT_ACCOUNT = "/WEB-INF/jsp/account/EditAccountForm.jsp";
	private static final String SIGNON = "/WEB-INF/jsp/account/SignonForm.jsp";
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
		return render("/account/login.html");
	}
	
	@ViewMethod(regex="^/login/do$")
	public Response check() {
		return render("/account/login.html");
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
		return HttpResponse.ok("ok");
	}
	//
	// public Resolution signoff() {
	// context.getRequest().getSession().invalidate();
	// clear();
	// return new RedirectResolution(CatalogActionBean.class);
	// }
	//
	// public boolean isAuthenticated() {
	// return authenticated && account != null && account.getUsername() != null;
	// }
	//
	// public void clear() {
	// account = new Account();
	// myList = null;
	// authenticated = false;
	// }

}
