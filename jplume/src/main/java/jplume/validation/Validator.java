package jplume.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jplume.http.Request;

public class Validator {

	protected List<String> errors = new LinkedList<>();
	
	protected Map<String, List<String>> fieldErrors = new HashMap<>();
	
	protected final Request request;
	
	public Validator(Request request) {
		this.request = request;
	}

	public boolean hasError() {
		return !errors.isEmpty() || !fieldErrors.isEmpty();
	}
	
	public void addError(String message) {
		errors.add(message);
	}
	
	public void addFieldError(String name, String message) {
		if (!fieldErrors.containsKey(name)) {
			fieldErrors.put(name, new ArrayList<String>());
		}
		fieldErrors.get(name).add(message);
	}
	
	public void clearErrors() {
		this.errors.clear();
		this.fieldErrors.clear();
	}
	
	public void extend(Validator otherValidator) {
		this.errors.addAll(otherValidator.errors);
		this.fieldErrors.putAll(otherValidator.fieldErrors);
	}

	protected String fieldValue(String name) {
		return request.getParam(name);
	}

	public List<String> getErrors() {
		return Collections.unmodifiableList(errors);
	}

	public Map<String, List<String>> getFieldErrors() {
		return Collections.unmodifiableMap(fieldErrors);
	}
	
	public boolean require(String name) {
		return fieldValue(name) != null && !fieldValue(name).isEmpty();
	}
	
	public boolean require(String name, String errormsg) {
		if (!require(name)) {
			addFieldError(name, errormsg);
			return false;
		}
		return true;
	}
	
	public boolean equals(String name, String value) {
		return fieldValue(name).equals(value);
	}
	
	public boolean equals(String name, String value, String errormsg) {
		if (!equals(name, value)) {
			addFieldError(name, errormsg);
			return false;
		}
		return true;
	}
	
	public boolean format(String name, String regex) {
		String val = fieldValue(name);
		return Pattern.matches(regex, val);
	}
	
	public boolean format(String name, String regex, String errormsg) {
		if (!format(name, regex)) {
			addFieldError(name, errormsg);
			return false;
		}
		return true;
	}
	
	public boolean numeric(String name) {
		return format(name, "^[\\d]+$");
	}
	
	public boolean numeric(String name, String errormsg) {
		if (!numeric(name)) {
			addFieldError(name, errormsg);
			return false;
		}
		return true;
	}
	
	public boolean include(String name, String[] include) {
		Set<String> inc = new HashSet<>(Arrays.asList(include));
		return inc.contains(fieldValue(name));
	}
	
	public boolean include(String name, String[] include, String errormsg) {
		if (!include(name, include)) {
			addFieldError(name, errormsg);
			return false;
		}
		return true;
	}
	
	public boolean exclude(String name, String[] exclude) {
		Set<String> inc = new HashSet<>(Arrays.asList(exclude));
		return !inc.contains(fieldValue(name));
	}
	
	public boolean exclude(String name, String[] exclude, String errormsg) {
		if (!exclude(name, exclude)) {
			addFieldError(name, errormsg);
			return false;
		}
		return true;
	}
	
	public boolean range(String name, int min, int max) {
		if (!numeric(name)) return false;
		int val = Integer.parseInt(fieldValue(name));
		return val >= min && val <= max;
	}
	
	public boolean range(String name, int min, int max, String errormsg) {
		if (!range(name, min, max)) {
			addFieldError(name, errormsg);
			return false;
		}
		return true;
	}
	
	public boolean range(String name, long min, long max) {
		if (!numeric(name)) return false;
		long val = Long.parseLong(fieldValue(name));
		return val >= min && val <= max;
	}
	
	public boolean range(String name, long min, long max, String errormsg) {
		if (!range(name, min, max)) {
			addFieldError(name, errormsg);
			return false;
		}
		return true;
	}
	
	public boolean max(String name, long max) {
		if (!numeric(name)) return false;
		long val = Long.parseLong(fieldValue(name));
		return val <= max;
	}
	
	public boolean max(String name, long max, String errormsg) {
		if (!max(name, max)) {
			addFieldError(name, errormsg);
			return false;
		}
		return true;
	}
	
	public boolean min(String name, long min) {
		if (!numeric(name)) return false;
		long val = Long.parseLong(fieldValue(name));
		return val >= min;
	}
	
	public boolean min(String name, long min, String errormsg) {
		if (!min(name, min)) {
			addFieldError(name, errormsg);
			return false;
		}
		return true;
	}
	
	public boolean strlen(String name, int min, int max) {
		String val = fieldValue(name);
		if (min < 0){
			return val.length() <= max;
		}
		if (max < 0) {
			return val.length() >= min;
		}
		return val.length() >= min && val.length() <= max;
	}
	
	public boolean strlen(String name, int min, int max, String errormsg) {
		if (!strlen(name, min, max)) {
			addFieldError(name, errormsg);
			return false;
		}
		return true;
	}
	
	public boolean email(String name) {
		return format(name, "^[^@]+@([-\\w]+\\.)+[A-Za-z]{2,4}$");
	}
	
	public boolean email(String name, String errormsg) {
		if (!email(name)) {
			addFieldError(name, errormsg);
			return false;
		}
		return true;
	}
	
	
}
