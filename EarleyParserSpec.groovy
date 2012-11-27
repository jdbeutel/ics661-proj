import spock.lang.Specification

import static Parser.prettyPrint

/**
 * Test specification of EarleyParser.
 */
class EarleyParserSpec extends Specification {

    def '"book that flight" parses as expected'() {

        given:
        def p = new EarleyParser('book that flight', new Grammar(L1_DEF))

        expect:
        p.toString() == EXPECTED_CHART
        prettyPrint(p.completedParsesString) == EXPECTED_PARSE
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
\tS2\tS -> ∙ Aux NP VP\t[0, 0]\tpredictor
\tS3\tS -> ∙ VP\t[0, 0]\tpredictor
\tS4\tNP -> ∙ Pronoun\t[0, 0]\tpredictor
\tS5\tNP -> ∙ Proper-Noun\t[0, 0]\tpredictor
\tS6\tNP -> ∙ Det Nominal\t[0, 0]\tpredictor
\tS7\tVP -> ∙ Verb\t[0, 0]\tpredictor
\tS8\tVP -> ∙ Verb NP\t[0, 0]\tpredictor
\tS9\tVP -> ∙ Verb NP PP\t[0, 0]\tpredictor
\tS10\tVP -> ∙ Verb PP\t[0, 0]\tpredictor
\tS11\tVP -> ∙ VP PP\t[0, 0]\tpredictor

Chart[1]\tS12\tVerb -> book ∙\t[0, 1]\tscanner
\tS13\tVP -> Verb ∙\t[0, 1]\tcompleter
\tS14\tVP -> Verb ∙ NP\t[0, 1]\tcompleter
\tS15\tVP -> Verb ∙ NP PP\t[0, 1]\tcompleter
\tS16\tVP -> Verb ∙ PP\t[0, 1]\tcompleter
\tS17\tS -> VP ∙\t[0, 1]\tcompleter
\tS18\tVP -> VP ∙ PP\t[0, 1]\tcompleter
\tS19\tNP -> ∙ Pronoun\t[1, 1]\tpredictor
\tS20\tNP -> ∙ Proper-Noun\t[1, 1]\tpredictor
\tS21\tNP -> ∙ Det Nominal\t[1, 1]\tpredictor
\tS22\tPP -> ∙ Preposition NP\t[1, 1]\tpredictor

Chart[2]\tS23\tDet -> that ∙\t[1, 2]\tscanner
\tS24\tNP -> Det ∙ Nominal\t[1, 2]\tcompleter
\tS25\tNominal -> ∙ Noun\t[2, 2]\tpredictor
\tS26\tNominal -> ∙ Nominal Noun\t[2, 2]\tpredictor
\tS27\tNominal -> ∙ Nominal PP\t[2, 2]\tpredictor

Chart[3]\tS28\tNoun -> flight ∙\t[2, 3]\tscanner
\tS29\tNominal -> Noun ∙\t[2, 3]\tcompleter
\tS30\tNP -> Det Nominal ∙\t[1, 3]\tcompleter
\tS31\tNominal -> Nominal ∙ Noun\t[2, 3]\tcompleter
\tS32\tNominal -> Nominal ∙ PP\t[2, 3]\tcompleter
\tS33\tVP -> Verb NP ∙\t[0, 3]\tcompleter
\tS34\tVP -> Verb NP ∙ PP\t[0, 3]\tcompleter
\tS35\tPP -> ∙ Preposition NP\t[3, 3]\tpredictor
\tS36\tS -> VP ∙\t[0, 3]\tcompleter
\tS37\tVP -> VP ∙ PP\t[0, 3]\tcompleter"""

    static final EXPECTED_PARSE = """[S36 S
    [S33 VP
        [S12 Verb book (0,1)]
        [S30 NP
            [S23 Det that (1,2)]
            [S29 Nominal
                [S28 Noun flight (2,3)]
             (2,3)]
         (1,3)]
     (0,3)]
 (0,3)]"""
}
