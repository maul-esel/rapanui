package rapanui.dsl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

public class RuleSystemCollection implements Iterable<RuleSystem> {
	private final List<RuleSystem> ruleSystems = new LinkedList<RuleSystem>();

	@Inject XtextResourceSet resourceSet;

	public RuleSystemCollection() {
		new DslStandaloneSetup()
		.createInjectorAndDoEMFRegistration()
		.injectMembers(this);

		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		resourceSet.addLoadOption(XtextResource.OPTION_ENCODING, "utf-8");
	}

	public void load(String path) {
		Resource resource = resourceSet.getResource(URI.createFileURI(path), true);
		if (!resource.getErrors().isEmpty())
			throw new IllegalArgumentException("Can't load resource: "+resource.getErrors().get(0).getMessage());
		ruleSystems.add((RuleSystem)resource.getContents().get(0));
	}

	public void load(InputStream data) throws IOException {
		Resource resource = resourceSet.createResource(
				URI.createURI("memory://inputstream/" + data.hashCode() + ".raps"));
		resource.load(data, resourceSet.getLoadOptions());
		if (!resource.getErrors().isEmpty())
			throw new IllegalArgumentException("Can't load resource: "+resource.getErrors().get(0).getMessage());
		ruleSystems.add((RuleSystem)resource.getContents().get(0));
	}

	public String[] getDefinitionNames() {
		return ruleSystems.stream()
			.flatMap(system -> Arrays.stream(system.getDefinitionNames()))
			.toArray(String[]::new);
	}

	public Definition resolveDefinition(String name) {
		// TODO: consider using scoping API / Xtext built-in reference resolving code
		Optional<Definition> definition = ruleSystems.stream()
			.flatMap(ruleSystem -> ruleSystem.getDefinitions().stream())
			.filter((def) -> def.getName().equals(name))
			.findFirst();
		return definition.get();
	}

	public int size() {
		return ruleSystems.size();
	}

	public RuleSystem get(int index) {
		return ruleSystems.get(index);
	}

	@Override
	public Iterator<RuleSystem> iterator() {
		return ruleSystems.iterator();
	}
}
