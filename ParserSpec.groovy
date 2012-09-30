import spock.lang.Specification
import spock.lang.Unroll

/**
 * Test specification of Parser.
 */
class ParserSpec extends Specification {

    @Unroll
    def 'input "#input" parses as #expected'() {

        given:
        def g = new Grammar(L1_DEF)

        when:
        def p = new Parser(input, g)

        then:
        p.completedParsesString == expected

        where:
        input                               || expected
        'book'                              || '[S book]'
        'book that flight'                  || '[S [Verb book] [NP [Det that] [Nominal flight]]]'
        'book that flight through Houston'  || '[S [Verb book] [NP [Det that] [Nominal [Nominal flight] [PP [Preposition through] [NP Houston]]]]];[S [VP [Verb book] [NP [Det that] [Nominal flight]]] [PP [Preposition through] [NP Houston]]];[S [X2 [Verb book] [NP [Det that] [Nominal flight]]] [PP [Preposition through] [NP Houston]]]'
        'does this flight include a meal'   || '[S [X1 [Aux does] [NP [Det this] [Nominal flight]]] [VP [Verb include] [NP [Det a] [Nominal meal]]]]'
        'I prefer TWA'                      || '[S [NP I] [VP [Verb prefer] [NP TWA]]]'
        'I prefer a flight to Houston'      || '[S [NP I] [VP [Verb prefer] [NP [Det a] [Nominal [Nominal flight] [PP [Preposition to] [NP Houston]]]]]];[S [NP I] [VP [VP [Verb prefer] [NP [Det a] [Nominal flight]]] [PP [Preposition to] [NP Houston]]]];[S [NP I] [VP [X2 [Verb prefer] [NP [Det a] [Nominal flight]]] [PP [Preposition to] [NP Houston]]]]'
        'book the flight'                   || 'not S'  // 'the' is missing from the lexicon (not a Det)
        'does this flight include a dinner' || 'not S'  // 'dinner' is missing from the lexicon (not a Nominal)
        'book flight'                       || 'not S'  // missing rule
        'book flight that'                  || 'not S'  // missing rule
        'book book'                         || 'not S'  // missing rule
    }

    static final L1_DEF = """S -> NP VP
S -> Aux NP VP
S -> VP
NP -> Pronoun
NP -> Proper-Noun
NP -> Det Nominal
Nominal -> Noun
Nominal -> Nominal Noun
Nominal -> Nominal PP
VP -> Verb
VP -> Verb NP
VP -> Verb NP PP
VP -> Verb PP
VP -> VP PP
PP -> Preposition NP
Det -> that
Det -> this
Det -> a
Noun -> book
Noun -> flight
Noun -> meal
Noun -> money
Verb -> book
Verb -> include
Verb -> prefer
Pronoun -> I
Pronoun -> she
Pronoun -> me
Proper-Noun -> Houston
Proper-Noun -> TWA
Aux -> does
Preposition -> from
Preposition -> to
Preposition -> on
Preposition -> near
Preposition -> through"""
}
