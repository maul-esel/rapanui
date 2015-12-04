package rapanui.dsl.conversion;

import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.nodemodel.INode;

public class RuleNameValueConverter implements IValueConverter<String> {
	@Override
	public String toValue(String string, INode node) throws ValueConverterException {
		return string.substring(1, string.length() - 1);
	}

	@Override
	public String toString(String value) throws ValueConverterException {
		return "\"" + value + "\"";
	}
}
