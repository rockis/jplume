package jplume.conf;

public class URLResolverGroup extends URLResolveProvider{
	
	private URLResolveProvider[] urlpatterns = new URLResolveProvider[0];
	
	public URLResolverGroup(Class<?> actionClass, URLResolveProvider... patterns) {
		urlpatterns = patterns;
		for (URLResolveProvider pattern : patterns) {
			if (pattern instanceof URLResolver) {
				((URLResolver)pattern).setActionClass(actionClass);
			}
		}
	}

	public URLResolverGroup(URLResolveProvider... patterns) {
		urlpatterns = patterns;
	}

	public <T> T visit(String path, URLVisitor<T> visitor) throws URLResolveException {
		for (URLResolveProvider pattern : urlpatterns) {
			T resp = pattern.visit(path, visitor);
			if (resp != null) return resp;
		}
		return null;
	}

	public void setRegexPrefix(String regexPrefix) {
		for (URLResolveProvider pattern : urlpatterns) {
			pattern.setRegexPrefix(regexPrefix);
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (URLResolveProvider pattern : urlpatterns) {
			sb.append(pattern.toString() + "\n");
		}
		return sb.toString();
	}
}