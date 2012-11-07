import spock.lang.Specification
import spock.lang.Unroll

/**
 * Test specification of Grammar.
 */
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

    // works around Spock (or JUnit?) problems with having both \n and . in test names
    private static String backslashToNewline(String s) {
        s.replaceAll('\\\\', '\n')
    }

    @Unroll
    def 'grammar "#description" normalizes to "#expected"'() {

        given:
        def g = new Grammar(backslashToNewline(description))

        when:
        g.normalize()

        then:
        g.toString() == backslashToNewline(expected)

        where:
        description                                 || expected

        // converting terminals to non-terminals
        'S -> a B [1]\\B -> C d [1]\\C -> e f [1]'  || 'S -> X1 B [1]\\X1 -> a [1]\\B -> C X2 [1]\\X2 -> d [1]\\C -> X3 X4 [1]\\X3 -> e [1]\\X4 -> f [1]'
        'S -> a b [1]'                              || 'S -> X1 X2 [1]\\X1 -> a [1]\\X2 -> b [1]'
        'S -> A a [1]\\A -> a [1]'                  || 'S -> A X1 [1]\\X1 -> a [1]\\A -> a [1]'   // A -> X1 undone by unit productions

        // converting unit productions
        'S -> VP [1]\\VP -> Verb [1]\\Verb -> book [.3]\\Verb -> include [.3]\\Verb -> prefer [.4]' || 'S -> book [.3]\\S -> include [.3]\\S -> prefer [.4]'
        'S -> VP Verb [1]\\VP -> Verb [1]\\Verb -> book [.7]\\Verb -> fly [.3]'                     || 'S -> VP Verb [1]\\VP -> book [.7]\\VP -> fly [.3]\\Verb -> book [.7]\\Verb -> fly [.3]'

        // making all rules binary
        'S -> A B C [1]\\A -> a [1]\\B -> b [1]\\C -> c [1]'                    || 'S -> X1 C [1]\\X1 -> A B [1]\\A -> a [1]\\B -> b [1]\\C -> c [1]'
        'S -> A B C D [1]\\A -> a [1]\\B -> b [1]\\C -> c [1]\\D -> d [1]'      || 'S -> X2 D [1]\\X2 -> X1 C [1]\\X1 -> A B [1]\\A -> a [1]\\B -> b [1]\\C -> c [1]\\D -> d [1]'
        'S -> A B C [1]\\A -> A B C [.2]\\A -> a [.8]\\B -> b [1]\\C -> c [1]'  || 'S -> X1 C [1]\\A -> X1 C [.2]\\X1 -> A B [1]\\A -> a [.8]\\B -> b [1]\\C -> c [1]'
        'S -> a b c [1]'                                                        || 'S -> X4 X3 [1]\\X4 -> X1 X2 [1]\\X1 -> a [1]\\X2 -> b [1]\\X3 -> c [1]'
    }

    def "assignment 3 requirement"() {

        given:
        def g = new Grammar(L1_DEF)

        when:
        g.normalize()

        then:
        g.toString() == L1_CNF
    }

    def "normalization is reflexive"() {

        given:
        def g = new Grammar(L1_CNF)

        when:
        g.normalize()

        then:
        g.toString() == L1_CNF
    }

    static final L1_DEF = """S -> NP VP     [.80]
S -> Aux NP VP              [.15]
S -> VP                     [.05]
NP -> Pronoun               [.35]
NP -> Proper-Noun           [.30]
NP -> Det Nominal           [.35]
Nominal -> Noun             [.75]
Nominal -> Nominal Noun     [.20]
Nominal -> Nominal PP       [.05]
VP -> Verb                  [.35]
VP -> Verb NP               [.20]
VP -> Verb NP PP            [.10]
VP -> Verb PP               [.15]
VP -> VP PP                 [.20]
PP -> Preposition NP        [1]
Det -> that                 [.10]
Det -> this                 [.20]
Det -> a                    [.70]
Noun -> book                [.10]
Noun -> flight              [.30]
Noun -> meal                [.15]
Noun -> money               [.05]
Verb -> book                [.30]
Verb -> include             [.30]
Verb -> prefer              [.40]
Pronoun -> I                [.40]
Pronoun -> she              [.05]
Pronoun -> me               [.15]
Proper-Noun -> Houston      [.60]
Proper-Noun -> NWA          [.40]
Aux -> does                 [.60]
Preposition -> from         [.30]
Preposition -> to           [.30]
Preposition -> on           [.20]
Preposition -> near         [.15]
Preposition -> through      [.05]"""

    static final L1_CNF = """S -> NP VP [.80]
S -> X1 VP [.15]
X1 -> Aux NP [1]
S -> book [.005250]
S -> include [.005250]
S -> prefer [.007000]
S -> Verb NP [.0100]
S -> X2 PP [.0050]
S -> Verb PP [.0075]
S -> VP PP [.0100]
NP -> I [.1400]
NP -> she [.0175]
NP -> me [.0525]
NP -> Houston [.1800]
NP -> NWA [.1200]
NP -> Det Nominal [.35]
Nominal -> book [.0750]
Nominal -> flight [.2250]
Nominal -> meal [.1125]
Nominal -> money [.0375]
Nominal -> Nominal Noun [.20]
Nominal -> Nominal PP [.05]
VP -> book [.1050]
VP -> include [.1050]
VP -> prefer [.1400]
VP -> Verb NP [.20]
VP -> X2 PP [.10]
X2 -> Verb NP [1]
VP -> Verb PP [.15]
VP -> VP PP [.20]
PP -> Preposition NP [1]
Det -> that [.10]
Det -> this [.20]
Det -> a [.70]
Noun -> book [.10]
Noun -> flight [.30]
Noun -> meal [.15]
Noun -> money [.05]
Verb -> book [.30]
Verb -> include [.30]
Verb -> prefer [.40]
Aux -> does [.60]
Preposition -> from [.30]
Preposition -> to [.30]
Preposition -> on [.20]
Preposition -> near [.15]
Preposition -> through [.05]"""
}
