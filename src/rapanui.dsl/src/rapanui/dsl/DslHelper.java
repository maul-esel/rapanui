package rapanui.dsl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import rapanui.dsl.moai.Formula;
import rapanui.dsl.moai.Term;

public class DslHelper {
	public static boolean equal(Term left, Term right) {
		return EcoreUtil.equals(left, right);
	}
	public static boolean equal(Formula left, Formula right) {
		return EcoreUtil.equals(left, right);
	}
}
