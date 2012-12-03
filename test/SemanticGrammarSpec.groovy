import spock.lang.Specification


/**
 * Test specification of SemanticGrammar.
 */
class SemanticGrammarSpec extends Specification {

    def 'semantics of "Maharani closed"'() {

        given:
        def input = 'Maharani closed'

        expect:
        SemanticGrammar.parseSemantics(input).toString() == '∃e(Closed(e)∧ClosedThing(e,Maharani))'
        SemanticGrammar.parseSemanticsDerivation(input).normalizationString == [
                'λM.(M(Maharani))(λx.(∃e(Closed(e)∧ClosedThing(e,x))))',
                'λx.(∃e(Closed(e)∧ClosedThing(e,x)))(Maharani)',
                '∃e(Closed(e)∧ClosedThing(e,Maharani))',
        ].join('\n')
    }

    def 'semantics of "every restaurant closed"'() {

        given:
        def input = 'every restaurant closed'
        def det = 'λP.(λQ.(∀x(P(x)⇒Q(x))))'
        def noun = 'λr.(Restaurant(r))'
        def verb = 'λx.(∃e(Closed(e)∧ClosedThing(e,x)))'

        expect:
        SemanticGrammar.parseSemantics(input).toString() == '∀x(Restaurant(x)⇒∃e(Closed(e)∧ClosedThing(e,x)))'
        SemanticGrammar.parseSemanticsDerivation(input).normalizationString == [
                "$det($noun)($verb)",
                'λQ.(∀x(λr.(Restaurant(r))(x)⇒Q(x)))(λx.(∃e(Closed(e)∧ClosedThing(e,x))))',
                '∀x(λr.(Restaurant(r))(x)⇒λx.(∃e(Closed(e)∧ClosedThing(e,x)))(x))',
                '∀x(Restaurant(x)⇒∃e(Closed(e)∧ClosedThing(e,x)))',
        ].join('\n')
    }

    def 'semantics of "Matthew opened a restaurant"'() {

        given:
        def input = 'Matthew opened a restaurant'
        def properNoun = "λM.(M(Matthew))"
        def verb = "λW.(λz.(W(λx.(∃e(Opened(e)∧(Opener(e,z)∧Opened(e,x)))))))"
        def det = "λP.(λQ.(∃x(P(x)∧Q(x))))"
        def noun = "λr.(Restaurant(r))"
        def np = "$det($noun)"
        def vp = "$verb($np)"
        def expected = "$properNoun($vp)"
        def canonicalExpected = 'λM.(M(Matthew))(λW.(λz.(W(λx.(∃e(Opened(e)∧(Opener(e,z)∧Opened(e,x)))))))(λP.(λQ.(∃x(P(x)∧Q(x))))(λr.(Restaurant(r)))))'

        expect:
        canonicalExpected == expected
        SemanticGrammar.parseSemantics(input).toString() == '∃x(Restaurant(x)∧∃e(Opened(e)∧(Opener(e,Matthew)∧Opened(e,x))))'
        SemanticGrammar.parseSemanticsDerivation(input).normalizationString == [
                expected,
                'λW.(λz.(W(λx.(∃e(Opened(e)∧(Opener(e,z)∧Opened(e,x)))))))(λP.(λQ.(∃x(P(x)∧Q(x))))(λr.(Restaurant(r))))(Matthew)',
                'λz.(λP.(λQ.(∃x(P(x)∧Q(x))))(λr.(Restaurant(r)))(λy.(∃e(Opened(e)∧(Opener(e,z)∧Opened(e,y))))))(Matthew)',
                'λP.(λQ.(∃x(P(x)∧Q(x))))(λr.(Restaurant(r)))(λy.(∃e(Opened(e)∧(Opener(e,Matthew)∧Opened(e,y)))))',
                'λQ.(∃x(λr.(Restaurant(r))(x)∧Q(x)))(λy.(∃e(Opened(e)∧(Opener(e,Matthew)∧Opened(e,y)))))',
                '∃x(λr.(Restaurant(r))(x)∧λy.(∃e(Opened(e)∧(Opener(e,Matthew)∧Opened(e,y))))(x))',
                '∃x(Restaurant(x)∧∃e(Opened(e)∧(Opener(e,Matthew)∧Opened(e,x))))',
        ].join('\n')
    }
}
