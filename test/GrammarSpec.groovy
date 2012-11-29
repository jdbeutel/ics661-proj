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
        def terminals = 'that this a the book flight meal money flights dinner include prefer I she me you ' +
                'Houston NWA does can from to on near through'

        expect:
        g.terminals == terminals.split() as Set
    }

    // works around Spock (or JUnit?) problems with having both \n and . in test names
    static String backslashToNewline(String s) {
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

    @Unroll
    def 'grammar without attachments "#description" normalizes to "#expected"'() {

        given:
        def g = new Grammar(backslashToNewline(description))

        when:
        g.normalize()

        then:
        g.toString() == backslashToNewline(expected)

        where:
        description                                 || expected

        // converting terminals to non-terminals
        'S -> a B\\B -> C d\\C -> e f'  || 'S -> X1 B\\X1 -> a\\B -> C X2\\X2 -> d\\C -> X3 X4\\X3 -> e\\X4 -> f'
        'S -> a b'                      || 'S -> X1 X2\\X1 -> a\\X2 -> b'
        'S -> A a\\A -> a'              || 'S -> A X1\\X1 -> a\\A -> a'   // A -> X1 undone by unit productions

        // converting unit productions
        'S -> VP\\VP -> Verb\\Verb -> book\\Verb -> include\\Verb -> prefer'    || 'S -> book\\S -> include\\S -> prefer'
        'S -> VP Verb\\VP -> Verb\\Verb -> book\\Verb -> fly'                   || 'S -> VP Verb\\VP -> book\\VP -> fly\\Verb -> book\\Verb -> fly'

        // making all rules binary
        'S -> A B C\\A -> a\\B -> b\\C -> c'                || 'S -> X1 C\\X1 -> A B\\A -> a\\B -> b\\C -> c'
        'S -> A B C D\\A -> a\\B -> b\\C -> c\\D -> d'      || 'S -> X2 D\\X2 -> X1 C\\X1 -> A B\\A -> a\\B -> b\\C -> c\\D -> d'
        'S -> A B C\\A -> A B C\\A -> a\\B -> b\\C -> c'    || 'S -> X1 C\\A -> X1 C\\X1 -> A B\\A -> a\\B -> b\\C -> c'
        'S -> a b c'                                        || 'S -> X4 X3\\X4 -> X1 X2\\X1 -> a\\X2 -> b\\X3 -> c'
    }

    def "assignment 3 requirement"() {

        given:
        def g = new Grammar(L1_DEF)

        when:
        g.normalize()

        then:
        g.toString() == L1_CNF
    }

    def "normalization is stable"() {

        given:
        def g = new Grammar(input)

        when:
        g.normalize()
        def firstNormal = g.toString()

        and: 'again'
        g.normalize()
        def secondNormal = g.toString()

        then:
        firstNormal == secondNormal

        where:
        input << [L1_DEF, L1_CNF]
    }

    def "normalization is reflexive"() {

        given:
        def g = new Grammar(L1_CNF)

        when:
        g.normalize()

        then:
        g.toString() == L1_CNF
    }

    def 'split does not work the way Grammar would need it to'() {

        expect:
        'abc'.split(/\b/) == ['', 'abc']
        'a c'.split(/\b/) == ['', 'a', ' ', 'c']
        'a.c'.split(/\b/) == ['', 'a', '.', 'c']
        'λx.x'.split(/\b/) == ['', 'λx', '.', 'x']
        'a,b'.split(/\b/) == ['', 'a', ',', 'b']
        'a, b'.split(/\b/) == ['', 'a', ', ', 'b']
        'a) ∧ ¬b'.split(/\b/) == ['', 'a', ') ∧ ¬', 'b']
    }

    @Unroll
    def '#pattern matches "#input" as #expected'() {

        expect:
        pattern.matcher(input).collect {it} == expected

        where:
        input       | pattern       | expected
        'abc'       | ~/\w+/        | ['abc']
        'a c'       | ~/\w+/        | ['a', 'c']
        'λx.x'      | ~/\w+/        | ['x', 'x']     // not sure why λ is not in \w, but don't want it there anyway
        'λx.x'      | ~/[λ.]|\w+/   | ['λ', 'x', '.', 'x']
        'a,b'       | ~/\w+/        | ['a', 'b']
        'a,b'       | ~/[,]|\w+/    | ['a', ',', 'b']
        'a, b'      | ~/\w+/        | ['a', 'b']
        'a, b'      | ~/[,]|\w+/    | ['a', ',', 'b']
        'a) ∧ ¬b'   | ~/\w+/        | ['a', 'b']
        'a) ∧ ¬b'   | ~/[)∧¬]|\w+/  | ['a', ')', '∧', '¬', 'b']
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

    static final L1_CNF = """S -> NP VP [.80]
S -> X1 VP [.15]
X1 -> Aux NP [1]
S -> book [.005250]
S -> include [.005250]
S -> prefer [.007000]
S -> Verb NP [.0100]
S -> X2 PP [.0050]
S -> Verb PP [.0075]
S -> X2 NP [.0025]
S -> VP PP [.0075]
NP -> I [.1400]
NP -> she [.0175]
NP -> me [.0525]
NP -> you [.1400]
NP -> Houston [.1800]
NP -> NWA [.1200]
NP -> Det Nominal [.20]
NP -> book [.011250]
NP -> flight [.028125]
NP -> meal [.016875]
NP -> money [.005625]
NP -> flights [.039375]
NP -> dinner [.011250]
NP -> Nominal Noun [.0300]
NP -> Nominal PP [.0075]
Nominal -> book [.0750]
Nominal -> flight [.1875]
Nominal -> meal [.1125]
Nominal -> money [.0375]
Nominal -> flights [.2625]
Nominal -> dinner [.0750]
Nominal -> Nominal Noun [.20]
Nominal -> Nominal PP [.05]
VP -> book [.1050]
VP -> include [.1050]
VP -> prefer [.1400]
VP -> Verb NP [.20]
VP -> X2 PP [.10]
VP -> Verb PP [.15]
VP -> X2 NP [.05]
X2 -> Verb NP [1]
VP -> VP PP [.15]
PP -> Preposition NP [1]
Det -> that [.10]
Det -> this [.05]
Det -> a [.25]
Det -> the [.60]
Noun -> book [.10]
Noun -> flight [.25]
Noun -> meal [.15]
Noun -> money [.05]
Noun -> flights [.35]
Noun -> dinner [.10]
Verb -> book [.30]
Verb -> include [.30]
Verb -> prefer [.40]
Aux -> does [.60]
Aux -> can [.40]
Preposition -> from [.30]
Preposition -> to [.30]
Preposition -> on [.20]
Preposition -> near [.15]
Preposition -> through [.05]"""
}
