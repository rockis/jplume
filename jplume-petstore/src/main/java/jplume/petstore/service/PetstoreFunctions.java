package jplume.petstore.service;

import jplume.template.annotations.TemplateFunctionObject;

import org.springframework.stereotype.Service;

@Service
@TemplateFunctionObject(namespace = "petstore")
public class PetstoreFunctions {

	public String[] getCreditCardTypes() {
		return new String[]{ "Visa", "MasterCard", "American Express" };
	}
}
