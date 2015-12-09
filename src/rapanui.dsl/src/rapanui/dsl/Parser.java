package rapanui.dsl;

import java.io.StringReader;

import javax.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;

import rapanui.dsl.services.DslGrammarAccess;

public class Parser {
	private static final Parser instance = new Parser();

	public static Parser getInstance() {
		return instance;
	}

	@Inject
	private IParser internalParser;

	@Inject
	private DslGrammarAccess grammar;

	private Parser() {
		new DslStandaloneSetup()
		.createInjectorAndDoEMFRegistration()
		.injectMembers(this);
	}

	public RuleSystem parseRuleSystem(String input) {
		return (RuleSystem)parse(input, grammar.getRuleSystemRule());
	}

	public Formula parseFormula(String input) {
		return (Formula)parse(input, grammar.getFormulaRule());
	}

	public Term parseTerm(String input) {
		return (Term)parse(input, grammar.getTermRule());
	}

	private EObject parse(String input, ParserRule rule) {
		IParseResult result = internalParser.parse(rule, new StringReader(input));
		if (result.hasSyntaxErrors()) {
			for (INode node : result.getSyntaxErrors())
				throw new IllegalArgumentException("Parsing error: "
					+ node.getSyntaxErrorMessage().getMessage());
		}
		return result.getRootASTElement();
	}

	public boolean canParseRuleSystem(String input) {
		return canParse(input, grammar.getRuleSystemRule());
	}

	public boolean canParseFormula(String input) {
		return canParse(input, grammar.getFormulaRule());
	}

	public boolean canParseTerm(String input) {
		return canParse(input, grammar.getTermRule());
	}

	private boolean canParse(String input, ParserRule rule) {
		IParseResult result = internalParser.parse(rule, new StringReader(input));
		return !result.hasSyntaxErrors();
	}
}
