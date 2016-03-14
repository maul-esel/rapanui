package rapanui.core.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Objects;

import cucumber.api.java.en.*;
import rapanui.core.*;
import rapanui.dsl.*;

public class ProofSteps {
	private static final long SUGGESTION_TIMEOUT = 100;

	private ProofEnvironment currentEnvironment;
	private ConclusionProcess currentConclusion;
	private Transformation currentSuggestion;

	@Given("I have a proof environment with the rule system \"([^\"]+)\"")
	public void createProofEnvironent(String ruleSystem) {
		RuleSystemCollection ruleSystems = new RuleSystemCollection();
		ruleSystems.load("test/libraries/" + ruleSystem);
		currentEnvironment = new ProofEnvironment(ruleSystems);
	}

	@Given("I add the premise that (.*) is (.*)")
	public void addPremise(String termSyntax, String definitionName) {
		Term term = Parser.getInstance().parseTerm(termSyntax);
		Definition definition = currentEnvironment.getRuleSystems().resolveDefinition(definitionName);
		DefinitionReference reference = Builder.createDefinitionReference(term, definition);
		currentEnvironment.addPremise(reference);
	}

	@Given("I add the formula (.*) as premise")
	public void addPremise(String formulaSyntax) {
		Formula premise = Parser.getInstance().parseFormula(formulaSyntax);
		currentEnvironment.addPremise(premise);
	}

	@When("I start a new conclusion with start term (.*)")
	public void startConclusion(String termSyntax) {
		Term term = Parser.getInstance().parseTerm(termSyntax);
		currentConclusion = currentEnvironment.addConclusion(term);
	}

	@Then("the (greater|equal)? term (.*) is suggested")
	public void findSuggestion(String greaterOrEqual, String termSyntax) {
		final Term term = Parser.getInstance().parseTerm(termSyntax);

		BINARY_RELATION suggestionType = BINARY_RELATION.UNSPECIFIED;
		if (Objects.equal(greaterOrEqual, "equal"))
			suggestionType = BINARY_RELATION.EQUATION;
		else if (Objects.equal(greaterOrEqual, "greater"))
			suggestionType = BINARY_RELATION.INCLUSION;

		List<Transformation> suggestions = Collections.synchronizedList(new LinkedList<Transformation>());
		SuggestionFinder.getDefaultInstance().makeSuggestionsAsync(currentConclusion, suggestionType, false)
			.onEmit(suggestions::add);

		try {
			Thread.sleep(SUGGESTION_TIMEOUT);
		} catch (InterruptedException e) {}

		Optional<Transformation> requiredSuggestion = suggestions.stream()
			.filter(suggestion -> suggestion.getOutput().structurallyEquals(term))
			.findAny();
		assert requiredSuggestion.isPresent() : "The term " + termSyntax + " was not suggested.";

		currentSuggestion = requiredSuggestion.get();
	}

	@When("I apply this suggestion")
	public void applySuggestion() {
		currentConclusion.appendTransformation(currentSuggestion);
	}
}
