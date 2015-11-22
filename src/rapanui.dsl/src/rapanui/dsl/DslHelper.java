package rapanui.dsl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import rapanui.dsl.moai.Term;

public class DslHelper {
	public static boolean equal(Term left, Term right) {
		return EcoreUtil.equals(left, right);
	}
}
