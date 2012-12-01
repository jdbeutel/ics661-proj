import spock.lang.Specification
import spock.lang.Unroll

import static parser.Parser.prettyPrint
import parser.cky.CkyParser
import grammar.Grammar

/**
 * Test specification of CkyParser.
 */
class CkyParserSpec extends Specification {

    @Unroll
    def 'input "#input" parses as #expected'() {

        given:
        def g = new Grammar(L1_DEF)

        when:
        def p = new CkyParser(input, g)

        then:
        prettyPrint(p.completedParsesString) == prettyPrint(expected)

        where:
        input                               || expected
        'book'                              || '[S book {.005250}]'
        'book that flight'                  || '[S [Verb book {.30}] [NP [Det that {.10}] [Nominal flight {.1875}] {.00375000}] {.00001125000000}]'
        'book that flight through Houston'  || '[S [X2 [Verb book {.30}] [NP [Det that {.10}] [Nominal flight {.1875}] {.00375000}] {.00112500000}] [PP [Preposition through {.05}] [NP Houston {.1800}] {.009000}] {5.0625000000000E-8}];[S [VP [Verb book {.30}] [NP [Det that {.10}] [Nominal flight {.1875}] {.00375000}] {.000225000000}] [PP [Preposition through {.05}] [NP Houston {.1800}] {.009000}] {1.51875000000000E-8}];[S [Verb book {.30}] [NP [Det that {.10}] [Nominal [Nominal flight {.1875}] [PP [Preposition through {.05}] [NP Houston {.1800}] {.009000}] {.000084375000}] {.0000016875000000}] {5.0625000000000E-9}]'
        'does this flight include a meal'   || '[S [X1 [Aux does {.60}] [NP [Det this {.05}] [Nominal flight {.1875}] {.00187500}] {.00112500000}] [VP [Verb include {.30}] [NP [Det a {.25}] [Nominal meal {.1125}] {.00562500}] {.000337500000}] {5.69531250000000000E-8}]'
        'I prefer NWA'                      || '[S [NP I {.1400}] [VP [Verb prefer {.40}] [NP NWA {.1200}] {.00960000}] {.00107520000000}]'
        'I prefer a flight to Houston'      || '[S [NP I {.1400}] [VP [X2 [Verb prefer {.40}] [NP [Det a {.25}] [Nominal flight {.1875}] {.00937500}] {.00375000000}] [PP [Preposition to {.30}] [NP Houston {.1800}] {.054000}] {.0000202500000000000}] {.0000022680000000000000000}];[S [NP I {.1400}] [VP [VP [Verb prefer {.40}] [NP [Det a {.25}] [Nominal flight {.1875}] {.00937500}] {.000750000000}] [PP [Preposition to {.30}] [NP Houston {.1800}] {.054000}] {.00000607500000000000}] {6.8040000000000000000E-7}];[S [NP I {.1400}] [VP [Verb prefer {.40}] [NP [Det a {.25}] [Nominal [Nominal flight {.1875}] [PP [Preposition to {.30}] [NP Houston {.1800}] {.054000}] {.000506250000}] {.0000253125000000}] {.00000202500000000000}] {2.2680000000000000000E-7}]'
        'book the flight'                   || '[S [Verb book {.30}] [NP [Det the {.60}] [Nominal flight {.1875}] {.02250000}] {.00006750000000}]'
        'book flight'                       || '[S [Verb book {.30}] [NP flight {.028125}] {.000084375000}]'
        'book flight that'                  || 'not S'  // missing rule
        'book the prefer'                   || 'not S'  // missing rule
        'flight flight'                     || 'not S'  // missing rule
        'book those flights'                || 'not S'  // 'those' is missing from the lexicon (not a Det)
        'does this flight include a lunch'  || 'not S'  // 'lunch' is missing from the lexicon (not a Nominal)
    }

    @Unroll
    def 'input "#input" prettyPrints to #expected'() {

        expect:
        prettyPrint(input) == GrammarSpec.backslashToNewline(expected)

        where:
        input                                       || expected
        '[S book {.123}]'                           || '[S book {.123}]'
        '[S [NP I {.123}] [VP [Verb prefer {.123}] [NP NWA {.123}] {.123}] {.123}]'     || '[S\\    [NP I {.123}]\\    [VP\\        [Verb prefer {.123}]\\        [NP NWA {.123}]\\     {.123}]\\ {.123}]'
        '[S book {.123}];[S chair {.123}]'          || '[S book {.123}]\\;\\[S chair {.123}]'
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
