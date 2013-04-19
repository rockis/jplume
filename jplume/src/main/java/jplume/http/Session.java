package jplume.http;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

public class Session implements Map<String, Object> {

	private final HttpSession httpSession;
	
	private Map<String, Object> values = new HashMap<String, Object>();
	
	public Session(HttpSession httpSession) {
		super();
		this.httpSession = httpSession;
		for(Enumeration<String> e = this.httpSession.getAttributeNames(); e.hasMoreElements();) {
			String key = e.nextElement();
			this.values.put(key, httpSession.getAttribute(key));
		}
	}

	@Override
	public int size() {
		return this.values.size();
	}

	@Override
	public boolean isEmpty() {
		return this.values.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return values.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values.containsValue(value);
	}

	public Object get(Object key) {
		return values.get(key);
	}

	public Object put(String key, Object value) {
		httpSession.setAttribute(key, value);
		return values.put(key, value);
	}

	public Object remove(Object key) {
		httpSession.removeAttribute(key.toString());
		return values.remove(key);
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		for(Map.Entry<? extends String, ? extends Object> e : m.entrySet()) {
			httpSession.setAttribute(e.getKey(), e.getValue());
		}
		values.putAll(m);
	}

	public void clear() {
		for(String key : values.keySet()) {
			httpSession.removeAttribute(key);
		}
		values.clear();
	}

	public Set<String> keySet() {
		return values.keySet();
	}

	public Collection<Object> values() {
		return values.values();
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return values.entrySet();
	}

	public boolean equals(Object o) {
		return values.equals(o);
	}

	public int hashCode() {
		return values.hashCode();
	}

}
