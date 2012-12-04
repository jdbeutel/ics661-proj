import grammar.Grammar
import parser.earley.EarleyParser
import fol.lambda.SingleTerm
import parser.earley.EarleyState
import fol.lambda.TermList

/**
 * A grammar with semantic attachments, based on the textbook's figure 18.4.
 * The semantics are in FirstOrderLogic with lambda expressions.
 */
class SemanticGrammar {

    static final GRAMMAR = new Grammar('''  S -> NP VP    [1, $0($1)]
        NP -> Det Nominal           [.4, $0($1)]
        NP -> ProperNoun            [.6, $0]
        Nominal -> Noun             [1, $0]
        VP -> Verb                  [.7, $0]
        VP -> Verb NP               [.3, $0($1)]
        Det -> every                [.2, λP.(λQ.∀x(P(x)⇒Q(x)))]
        Det -> a                    [.8, λP.(λQ.∃x(P(x)∧Q(x)))]
        Noun -> restaurant          [1, λr.Restaurant(r)]
        ProperNoun -> Matthew       [.3, λM.M(Matthew)]
        ProperNoun -> Maharani      [.2, λM.M(Maharani)]
        ProperNoun -> Franco        [.25, λF.F(Franco)]
        ProperNoun -> Frasca        [.25, λF.F(Frasca)]
        Verb -> closed              [.4, λx.∃e(Closed(e)∧ClosedThing(e,x))]
        Verb -> opened              [.6, λW.(λz.W(λx.∃e(Opened(e)∧(Opener(e,z)∧Opened(e,x)))))]''')

    /**
     * Constructs an EarleyParser, parsing the given line with the semantic Grammar.
     *
     * @param line the line of words to parse (e.g., a sentence)
     */
    static EarleyParser parse(String input) {
        new EarleyParser(input, GRAMMAR)
    }

    /**
     * Gets complete parses of the given input (sorted by probability, if any).
     *
     * @param input to parse
     * @return a list of complete parses
     * @throws IllegalArgumentException if no complete parse is found
     */
    private static List<EarleyState> parses(String input) {
        def p = parse(input)
        def parses = (List<EarleyState>) p.completedParses
        if (!parses) {
            throw new IllegalArgumentException("unparsable input '$input': $p")
        }
        parses
    }

    /**
     * Gets the immediate (un-normalized) semantic representation of the most probable parse of the given input.
     * The caller can normalize the result to get the derivation of the fully reduced form.
     *
     * @param input to parse
     * @return the Lambda representation of the first (i.e., most probable) complete parse of the input
     */
    static SingleTerm parseSemanticsDerivation(String input) {
        parses(input)[0].lambda
    }

    /**
     * Gets the normalized (i.e., fully reduced) semantic representation of the most probable parse of the given input.
     *
     * @param input to parse
     * @return the fully reduced Lambda representation
     */
    static TermList parseSemantics(String input) {
        parseSemanticsDerivation(input).normalization[-1]
    }

    /**
     * Renders all accepted, full parses into easy-to-read indentation.
     *
     * @return a rendering of all possible parses, or "not S" if none are accepted
     */
    static String prettyCompletedSemanticParses(String input) {
        def p = parse(input)
        def parses = p.completedParses
        if (parses) {
            return parses.collect {
                it.prettyPrint(0) + '\n-\n' + it.lambda.normalizationString
            }.join('\n;')
        } else {
            return "not ${GRAMMAR.startSymbol}"
        }
    }

    /**
     * For running from the command line (from the "src" dir), this parses lines from stdin.
     *
     * @param args  command line arguments (unused)
     */
    static void main(String[] args) {
        System.in.eachLine {line ->
            println "\n$line: " + prettyCompletedSemanticParses(line)
            null    // just avoiding a warning about not returning a value from eachLine
        }
    }
}
