package rapanui.dsl.conversion;

import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.impl.AbstractDeclarativeValueConverterService;

public class DslValueConverterService extends AbstractDeclarativeValueConverterService {
	@ValueConverter(rule = "RULE_NAME")
	public IValueConverter<String> getRULE_NAMEConverter() {
		return new RuleNameValueConverter();
	}
}
