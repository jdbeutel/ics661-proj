import spock.lang.Specification
import spock.lang.Unroll

class GrammarSpec extends Specification {


    def 'L1 non-terminals'() {

        given:
        def g = new Grammar(L1_DEF)

        expect:
        g.nonTerminals == 'S NP Nominal VP PP Det Noun Verb Pronoun Proper-Noun Aux Preposition'.split() as Set
    }

    def 'L1 terminals'() {

        given:
        def g = new Grammar(L1_DEF)
        def terminals = 'that this a book flight meal money include prefer I she me ' +
                'Houston NWA does from to on near through'

        expect:
        g.terminals == terminals.split() as Set
    }

    @Unroll
    def 'grammar "#description" normalizes to "#expected"'() {

        given:
        def g = new Grammar((String) description)

        when:
        g.normalize()

        then:
        g.toString() == expected

        where:
        description                         || expected

        // converting terminals to non-terminals
        'S -> a B\nB -> C d\nC -> e f'      || 'S -> X1 B\nX1 -> a\nB -> C X2\nX2 -> d\nC -> X3 X4\nX3 -> e\nX4 -> f'
        'S -> a b'                          || 'S -> X1 X2\nX1 -> a\nX2 -> b'
        'S -> A a\nA -> a'                  || 'S -> A X1\nX1 -> a\nA -> a'   // A -> X1 undone by unit productions

        // converting unit productions
        'S -> VP\nVP -> Verb\nVerb -> book\nVerb -> include\nVerb -> prefer'    || 'S -> book\nS -> include\nS -> prefer'
        'S -> VP Verb\nVP -> Verb\nVerb -> book\nVerb -> fly'                   || 'S -> VP Verb\nVP -> book\nVP -> fly\nVerb -> book\nVerb -> fly'

        // making all rules binary
        'S -> A B C\nA -> a\nB -> b\nC -> c'                || 'S -> X1 C\nX1 -> A B\nA -> a\nB -> b\nC -> c'
        'S -> A B C D\nA -> a\nB -> b\nC -> c\nD -> d'      || 'S -> X2 D\nX2 -> X1 C\nX1 -> A B\nA -> a\nB -> b\nC -> c\nD -> d'
        'S -> A B C\nA -> A B C\nA -> a\nB -> b\nC -> c'    || 'S -> X1 C\nA -> X1 C\nX1 -> A B\nA -> a\nB -> b\nC -> c'
        'S -> a b c'                                        || 'S -> X4 X3\nX4 -> X1 X2\nX1 -> a\nX2 -> b\nX3 -> c'

        // the requirement
        L1_DEF                  || L1_CNF

        // reflexive
        L1_CNF                  || L1_CNF
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
Proper-Noun -> NWA
Aux -> does
Preposition -> from
Preposition -> to
Preposition -> on
Preposition -> near
Preposition -> through"""

    static final L1_CNF = """S -> NP VP
S -> X1 VP
X1 -> Aux NP
S -> book
S -> include
S -> prefer
S -> Verb NP
S -> X2 PP
S -> Verb PP
S -> VP PP
NP -> I
NP -> she
NP -> me
NP -> Houston
NP -> NWA
NP -> Det Nominal
Nominal -> book
Nominal -> flight
Nominal -> meal
Nominal -> money
Nominal -> Nominal Noun
Nominal -> Nominal PP
VP -> book
VP -> include
VP -> prefer
VP -> Verb NP
VP -> X2 PP
X2 -> Verb NP
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
Aux -> does
Preposition -> from
Preposition -> to
Preposition -> on
Preposition -> near
Preposition -> through"""
}
