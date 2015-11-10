package rapanui.dsl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import rapanui.dsl.moai.RuleSystem;
import rapanui.dsl.moai.Formula;
import rapanui.dsl.moai.Term;

public class Parser {
	@Inject
	private XtextResourceSet resourceSet;

	public Parser() {
		new MoaiStandaloneSetup()
		.createInjectorAndDoEMFRegistration()
		.injectMembers(this);

		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
	}

	public RuleSystem parseRuleSystem(String input) {
		return (RuleSystem)parse(input);
	}

	public Formula parseFormula(String input) {
		return (Formula)parse(input);
	}

	public Term parseTerm(String input) {
		return (Term)parse(input);
	}

	private EObject parse(String input) {
		InputStream in;
		try {
			in = new ByteArrayInputStream(input.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}

		Resource resource = resourceSet.createResource(URI.createURI("dummy:/inmemory.moai"));
		try {
			resource.load(in, resourceSet.getLoadOptions());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return resource.getContents().get(0);
	}
}
