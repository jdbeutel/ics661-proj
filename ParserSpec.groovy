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
        'book'                              || '[S book][.005250]'
        'book that flight'                  || '[S [Verb book][.30] [NP [Det that][.10] [Nominal flight][.1875]][.00375000]][.00001125000000]'
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

    static final L1_DEF = """S -> NP VP     [.80]
S -> Aux NP VP              [.15]
S -> VP                     [.05]
NP -> Pronoun               [.35]
NP -> Proper-Noun           [.30]
NP -> Det Nominal           [.20]
NP -> Nominal               [.15]
Nominal -> Noun             [.75]
Nominal -> Nominal Noun     [.20]
Nominal -> Nominal PP       [.05]
VP -> Verb                  [.35]
VP -> Verb NP               [.20]
VP -> Verb NP PP            [.10]
VP -> Verb PP               [.15]
VP -> Verb NP NP            [.05]
VP -> VP PP                 [.15]
PP -> Preposition NP        [1]
Det -> that                 [.10]
Det -> this                 [.05]
Det -> a                    [.25]
Det -> the                  [.60]
Noun -> book                [.10]
Noun -> flight              [.25]
Noun -> meal                [.15]
Noun -> money               [.05]
Noun -> flights             [.35]
Noun -> dinner              [.10]
Verb -> book                [.30]
Verb -> include             [.30]
Verb -> prefer              [.40]
Pronoun -> I                [.40]
Pronoun -> she              [.05]
Pronoun -> me               [.15]
Pronoun -> you              [.40]
Proper-Noun -> Houston      [.60]
Proper-Noun -> NWA          [.40]
Aux -> does                 [.60]
Aux -> can                  [.40]
Preposition -> from         [.30]
Preposition -> to           [.30]
Preposition -> on           [.20]
Preposition -> near         [.15]
Preposition -> through      [.05]"""
}
