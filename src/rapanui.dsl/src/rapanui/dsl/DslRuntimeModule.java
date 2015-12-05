package rapanui.dsl;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class DslRuntimeModule extends rapanui.dsl.AbstractDslRuntimeModule {
	@Override
	public Class<? extends org.eclipse.xtext.conversion.IValueConverterService> bindIValueConverterService() {
		return rapanui.dsl.conversion.DslValueConverterService.class;
	}
}
