package rapanui.dsl;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.ISerializer;

import rapanui.dsl.moai.Formula;
import rapanui.dsl.moai.Term;

public class DslHelper {
	public static boolean equal(Term left, Term right) {
		return EcoreUtil.equals(left, right);
	}

	public static boolean equal(Formula left, Formula right) {
		return EcoreUtil.equals(left, right);
	}

	public static String serialize(Formula formula) {
		return new MoaiStandaloneSetup()
		.createInjectorAndDoEMFRegistration()
		.getInstance(ISerializer.class)
		.serialize(formula)
		.replaceAll("\\s+", " "); // remove extraneous whitespace // TODO: do this properly in a formatter
	}

	public static String serialize(Term term) {
		return new MoaiStandaloneSetup()
		.createInjectorAndDoEMFRegistration()
		.getInstance(ISerializer.class)
		.serialize(term)
		.replaceAll("\\s+", " "); // remove extraneous whitespace // TODO: do this properly in a formatter
	}
}
