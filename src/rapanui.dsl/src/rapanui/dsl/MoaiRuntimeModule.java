package rapanui.dsl;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class MoaiRuntimeModule extends rapanui.dsl.AbstractMoaiRuntimeModule {
    @Override
    public Class<? extends org.eclipse.xtext.conversion.IValueConverterService> bindIValueConverterService() {
        return rapanui.dsl.conversion.MoaiValueConverterService.class;
    }
}
