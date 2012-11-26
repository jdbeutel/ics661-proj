import spock.lang.Specification

/**
 * Test specification of EarleyParser.
 */
class EarleyParserSpec extends Specification {

    def '"book that flight" parse chart as expected'() {

        given:
        def p = new EarleyParser('book that flight', new Grammar(L1_DEF))

        expect:
        p.toString() == EXPECTED_CHART
    }

    static final L1_DEF = """S -> NP VP | Aux NP VP | VP
NP -> Pronoun | Proper-Noun | Det Nominal
Nominal -> Noun | Nominal Noun  | Nominal PP
VP -> Verb | Verb NP | Verb NP PP | Verb PP | VP PP
PP -> Preposition NP
Det -> that | this | a
Noun -> book | flight | meal | money
Verb -> book | include | prefer
Pronoun -> I | she | me
Proper-Noun -> Houston | NWA
Aux -> does
Preposition -> from | to | on | near | through"""

    static final EXPECTED_CHART = """Chart[0]\tS0\tγ -> ∙ S\t[0, 0]\tdummy start state
\tS1\tS -> ∙ NP VP\t[0, 0]\tpredictor
todo: fill this in
\tS37\tVP -> VP ∙ PP\t[0, 3]\tcompleter"""
}
