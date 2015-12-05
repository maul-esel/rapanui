package rapanui.dsl;

import rapanui.dsl.moai.Formula;
import rapanui.dsl.moai.Term;

public class DslHelper {
	public static String serialize(Formula formula) {
		return Serializer.getInstance().serialize(formula);
	}

	public static String serialize(Term term) {
		return Serializer.getInstance().serialize(term);
	}
}
