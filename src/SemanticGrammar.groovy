import grammar.Grammar
import parser.earley.EarleyParser
import fol.lambda.SingleTerm
import parser.earley.EarleyState
import fol.lambda.TermList

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

    static EarleyParser parse(String input) {
        new EarleyParser(input, GRAMMAR)
    }

    private static List<EarleyState> parses(String input) {
        def p = parse(input)
        def parses = (List<EarleyState>) p.completedParses
        if (!parses) {
            throw new IllegalArgumentException("unparsable input '$input': $p")
        }
        parses
    }

    static SingleTerm parseSemanticsDerivation(String input) {
        parses(input)[0].lambda
    }

    static List<SingleTerm> allParseSemanticsDerivation(String input) {
        parses(input)*.lambda
    }

    static TermList parseSemantics(String input) {
        parseSemanticsDerivation(input).normalization[-1]
    }

    static List<TermList> allParseSemantics(String input) {
        allParseSemanticsDerivation(input)*.normalization.collect {it[-1]}
    }
}
