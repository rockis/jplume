package jplume.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jplume.converters.Converter;
import jplume.http.Request;

public class ArgumentBuilder {
	
	private List<PathIndexedArgument> pathIndexedArgs = new ArrayList<>();
	private List<PathNamedArgument> pathNamedArgs = new ArrayList<>();
	private List<QueryArgument> querydArgs = new ArrayList<>();

	private boolean hasFormArg = false;
	
	Object[] build(Request request, String[] indexedVars, Map<String, String> namedVars) {
		List<Object> args = new LinkedList<>();
		if (pathNamedArgs.size() > 0) {
			for (PathNamedArgument arg : pathNamedArgs) {
				args.add(arg.get(namedVars));
			}
		}else{
			for (PathIndexedArgument arg : pathIndexedArgs) {
				args.add(arg.get(indexedVars));
			}
		}
		for (QueryArgument arg : querydArgs) {
			args.add(arg.get(request));
		}
		return args.toArray(new Object[0]);
	}
	
	void addArgument(Argument arg){
		if (arg instanceof PathIndexedArgument){
			this.pathIndexedArgs.add((PathIndexedArgument)arg);
		}else if (arg instanceof PathNamedArgument) {
			this.pathNamedArgs.add((PathNamedArgument)arg);
		}else if (arg instanceof QueryArgument) {
			this.querydArgs.add((QueryArgument)arg);
		}else{
			throw new IllegalStateException("Unsupported argument type");
		}
	}
	
	public boolean hasFormArg() {
		return hasFormArg;
	}
	
	public void setHasFormArg(boolean hasFormArg) {
		this.hasFormArg = hasFormArg;
	}
	
	public Class<?>[] getArgumentTypes() {
		List<Class<?>> types = new LinkedList<>();
		List<Object> args = new LinkedList<>();
		if (pathNamedArgs.size() > 0) {
			for (PathNamedArgument arg : pathNamedArgs) {
				args.add(arg.type);
			}
		}else{
			for (PathIndexedArgument arg : pathIndexedArgs) {
				args.add(arg.type);
			}
		}
		for (QueryArgument arg : querydArgs) {
			types.add(arg.type);
		}
		return args.toArray(new Class<?>[0]);
	}
	
	public boolean validate(String[] indexedVars, Map<String, String> namedVars) {
		for(Argument arg : pathIndexedArgs) {
			if (!((PathArgument)arg).validate(indexedVars, namedVars)){
				return false;
			}
		}
		for(Argument arg : pathNamedArgs) {
			if (!((PathNamedArgument)arg).validate(indexedVars, namedVars)){
				return false;
			}
		}
		return true;
	}
	

	public List<PathIndexedArgument> getPathIndexedArgs() {
		return Collections.unmodifiableList(pathIndexedArgs);
	}

	public List<PathNamedArgument> getPathNamedArgs() {
		return Collections.unmodifiableList(pathNamedArgs);
	}

	public List<QueryArgument> getQuerydArgs() {
		return Collections.unmodifiableList(querydArgs);
	}
	
	public static abstract class Argument {
		
		protected Class<?> type;
		Argument(Class<?> type) {
			this.type = type;
		}
		
		public Class<?> getType() {
			return type;
		}
		
		protected Object convert(String val) {
			return Converter.convert(this.type, val);
		}
	}

	public static abstract class PathArgument extends Argument {
		PathArgument(Class<?> type) {
			super(type);
		}
		
		protected boolean validate(String val) {
			return Converter.validate(type, val);
		}
		
		protected abstract boolean validate(String[] indexedVars, Map<String, String> namedVars);
	}
	
	public static class PathIndexedArgument extends PathArgument{
		private int pathIndex;

		PathIndexedArgument(Class<?> type, int pathIndex) {
			super(type);
			this.pathIndex = pathIndex;
		}
		
		public int getArgIndex() {
			return this.pathIndex;
		}

		protected Object get(String[] indexedVars) {
			return convert(indexedVars[pathIndex]);
		}
		
		protected boolean validate(String[] indexedVars, Map<String, String> namedVars) {
			return validate(indexedVars[pathIndex]);
		}
	}
	
	public static class PathNamedArgument extends PathArgument{
		private String argName = null;

		public PathNamedArgument(Class<?> type, String argName) {
			super(type);
			this.argName = argName;
		}

		public String getArgName() {
			return argName;
		}
		
		Object get(Map<String, String> namedVars) {
			return convert(namedVars.get(this.argName));
		}
		
		protected boolean validate(String[] indexedVars, Map<String, String> namedVars) {
			String val = namedVars.get(argName);
			if (val == null) {
				return false;
			}
			return validate(namedVars.get(argName));
		}
	}
	
	
	public static class QueryArgument extends Argument {
		private String name;
		private String defval;

		QueryArgument(Class<?> type, String name, String defval) {
			super(type);
			this.name   = name;
			this.defval = defval;
		}

		Object get(Request request) {
			String val = request.getParam(name);
			if (val == null || !Converter.validate(this.type, val)) {
				return convert(defval);
			}
			return convert(val);
		}
	}
	
}