package rapanui.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import rapanui.dsl.*;

public class RuleApplicationFinder implements JustificationFinder {
	private final JustificationFinder delegateFinder;

	public RuleApplicationFinder(JustificationFinder delegateFinder) {
		this.delegateFinder = delegateFinder;
	}

	@Override
	public Emitter<Justification> justifyAsync(ProofEnvironment environment, Formula formulaTemplate,
			int recursionDepth) {
		if (formulaTemplate.getLeft() == null && formulaTemplate.getRight() == null)
			return Emitter.empty();
		return Emitter.fromResultComputation(acceptor -> searchRuleApplications(acceptor, environment, formulaTemplate, recursionDepth));
	}

	private void searchRuleApplications(Consumer<Justification> acceptor, ProofEnvironment environment,
			Formula formulaTemplate, int recursionDepth) {
		for (RuleSystem ruleSystem : environment.getRuleSystems())
			for (Rule rule : ruleSystem.getRules())
				for (Formula conclusion : rule.getResolvedConclusions()) {
					BINARY_RELATION conclusionType = conclusion.getFormulaType();
					if (formulaTemplate.getFormulaType() == null || formulaTemplate.getFormulaType() == conclusionType
							|| (conclusionType == BINARY_RELATION.EQUATION && formulaTemplate.getFormulaType() == BINARY_RELATION.INCLUSION))
						searchApplications(rule, conclusion, formulaTemplate, acceptor, environment, recursionDepth);

					if (conclusionType == BINARY_RELATION.EQUATION) // (type is always compatible)
						searchApplications(rule, Builder.reverse(conclusion), formulaTemplate, acceptor, environment, recursionDepth);
				}
	}

	private void searchApplications(Rule rule, Formula conclusion, Formula formulaTemplate,
			Consumer<Justification> acceptor, ProofEnvironment environment, int recursionDepth) {
		TranslationFinder translationFinder = new TranslationFinder();

		// try matching specified terms to conclusion
		if ( !translationFinder.train(conclusion.getLeft(), formulaTemplate.getLeft())
			|| !translationFinder.train(conclusion.getRight(), formulaTemplate.getRight()) )
			return;

		if (rule.getResolvedPremises().size() == 0) // no premises to justify
			emitRuleApplication(acceptor, formulaTemplate, rule, conclusion, translationFinder, new Justification[0]);
		else {
			// search justifications for first premise
			Emitter<SearchResult> emitter = justifyPremise(rule.getResolvedPremises().get(0), translationFinder, environment, recursionDepth);
			for (Formula premise : rule.getResolvedPremises().subList(1, rule.getResolvedPremises().size())) {
				// Whenever a justification for the previous premise is found,
				// use the returned TranslationFinder to justify the current
				// premise. Then combine the results.
				emitter = emitter.flatMap(oldResult ->
					justifyPremise(premise, oldResult.finder, environment, recursionDepth)
					.map(newResult -> combineResults(oldResult, newResult))
				);
			}
			// Whenever a list of justifications for all premises is found, emit a rule application
			emitter.onEmit(result ->
				emitRuleApplication(acceptor, formulaTemplate, rule, conclusion, result.finder, result.justifications.stream().toArray(Justification[]::new))
			);
		}
	}

	private void emitRuleApplication(Consumer<Justification> acceptor, Formula formulaTemplate, Rule rule,
			Formula conclusion, TranslationFinder translationFinder, Justification[] premiseJustifications) {
		BINARY_RELATION type = formulaTemplate.getFormulaType() != null ? formulaTemplate.getFormulaType() : conclusion.getFormulaType();
		Term left = null, right = null;
		try {
			left = translationFinder.translate(conclusion.getLeft());
			right = translationFinder.translate(conclusion.getRight());

			Formula justifiedFormula = Builder.createFormula(left, type, right);
			acceptor.accept(new RuleApplication(justifiedFormula, rule, translationFinder.getDictionary(), premiseJustifications));
		} catch (Translator.IncompleteDictionaryException e) { /* do not emit */ }
	}

	private SearchResult combineResults(SearchResult oldResult, SearchResult newResult) {
		List<Justification> combined = Stream.of(oldResult.justifications, newResult.justifications)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		return new SearchResult(combined, newResult.finder);
	}

	private Emitter<SearchResult> justifyPremise(Formula premise, TranslationFinder translationFinder, ProofEnvironment environment, int recursionDepth) {
		if (recursionDepth <= 0)
			return Emitter.empty();

		Formula premiseTemplate = createTemplate(premise, translationFinder);
		return delegateFinder.justifyAsync(environment, premiseTemplate, recursionDepth - 1) // justify premise
			.map( premiseJustification -> new SearchResult(premiseJustification, translationFinder.clone()) ) // wrap it in a SearchResult
			.filter( result -> { // only emit it if it does not conflict with the current translation
				return result.finder.train(premise.getLeft(), result.getLast().getJustifiedFormula().getLeft())
					&& result.finder.train(premise.getRight(), result.getLast().getJustifiedFormula().getRight());
			} );
	}

	private Formula createTemplate(Formula formula, TranslationFinder translationFinder) {
		return Builder.createFormula(
				translationFinder.partialTranslate(formula.getLeft()),
				formula.getFormulaType(),
				translationFinder.partialTranslate(formula.getRight())
		);
	}

	private static class SearchResult {
		public final List<Justification> justifications;
		public final TranslationFinder finder;

		public SearchResult(List<Justification> justifications, TranslationFinder finder) {
			this.justifications = justifications;
			this.finder = finder;
		}

		public SearchResult(Justification justification, TranslationFinder finder) {
			this(Arrays.asList(justification), finder);
		}

		public Justification getLast() {
			return justifications.get(justifications.size()-1);
		}
	}
}
