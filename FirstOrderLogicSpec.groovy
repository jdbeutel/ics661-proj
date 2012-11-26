import spock.lang.Specification
import spock.lang.Unroll

import static CkyParser.prettyPrint

/**
 * Test specification of FirstOrderLogic.
 */
class FirstOrderLogicSpec extends Specification {

    @Unroll
    def 'symbolic char #c is not a letter, digit, or whitespace'() {

        expect:
        !c.isLetterOrDigit()
        !c.isWhitespace()

        where:
        c << FirstOrderLogic.SYMBOLIC_CHARS.toCharArray()
    }

    def 'λ is a letter'() {

        given:
        char c = 'λ'

        expect:
        c.isLetter()
    }

    def 'normalized FOL grammar'() {

        given:
        def g = FirstOrderLogic.GRAMMAR

        when:
        g.normalize()

        then:
        g.toString() == FOL_CNF
    }

    @Unroll
    def 'FOL "#input" parses as #expected'() {

        when:
        def p = FirstOrderLogic.parse(input)

        then:
        prettyPrint(p.completedParsesString) == prettyPrint(expected)

        where:
        input                               || expected
        'Restaurant(Maharani)'              || '[S [X8 [X7 [Predicate Restaurant] [X2 (]] [TermList Maharani]] [X3 )]]'
        'Have(Speaker, FiveDollars) ∧ ¬Have(Speaker, LotOfTime)'    || '[S [X9 [Formula [X8 [X7 [Predicate Have] [X2 (]] [TermList [X17 [Term Speaker] [X6 ,]] [TermList FiveDollars]]] [X3 )]] [Connective ∧]] [Formula [X1 ¬] [Formula [X8 [X7 [Predicate Have] [X2 (]] [TermList [X17 [Term Speaker] [X6 ,]] [TermList LotOfTime]]] [X3 )]]]]'
        '∀x(VegetarianRestaurant(x) ⇒ Serves(x, VegetarianFood))'   || '[S foo]'
        '∀x VegetarianRestaurant(x) ⇒ Serves(x, VegetarianFood)'    || '[S foo]'
    }

    static final FOL_CNF = """S -> X8 X3
S -> X9 Formula
S -> X10 Formula
S -> X1 Formula
S -> X11 X3
S -> X13 Formula
Formula -> X8 X3
Formula -> X9 Formula
X9 -> Formula Connective
Formula -> X10 Formula
X10 -> Quantifier VariableList
Formula -> X1 Formula
X1 -> ¬
Formula -> X11 X3
X11 -> X2 Formula
X2 -> (
X3 -> )
Formula -> X13 Formula
X13 -> X12 X5
X12 -> X4 Variable
X4 -> λ
X5 -> .
VariableList -> a
VariableList -> b
VariableList -> c
VariableList -> d
VariableList -> e
VariableList -> f
VariableList -> g
VariableList -> h
VariableList -> i
VariableList -> j
VariableList -> k
VariableList -> l
VariableList -> m
VariableList -> n
VariableList -> o
VariableList -> p
VariableList -> q
VariableList -> r
VariableList -> s
VariableList -> t
VariableList -> u
VariableList -> v
VariableList -> w
VariableList -> x
VariableList -> y
VariableList -> z
VariableList -> A
VariableList -> B
VariableList -> C
VariableList -> D
VariableList -> E
VariableList -> F
VariableList -> G
VariableList -> H
VariableList -> I
VariableList -> J
VariableList -> K
VariableList -> L
VariableList -> M
VariableList -> N
VariableList -> O
VariableList -> P
VariableList -> Q
VariableList -> R
VariableList -> T
VariableList -> U
VariableList -> V
VariableList -> W
VariableList -> X
VariableList -> Y
VariableList -> Z
VariableList -> X14 VariableList
X14 -> Variable X6
X6 -> ,
TermList -> X16 X3
TermList -> VegetarianFood
TermList -> Maharani
TermList -> AyCaramba
TermList -> Bacaro
TermList -> Centro
TermList -> Leaf
TermList -> Speaker
TermList -> TurkeySandwich
TermList -> Desk
TermList -> Lunch
TermList -> FiveDollars
TermList -> LotOfTime
TermList -> Monday
TermList -> Tuesday
TermList -> Wednesday
TermList -> Thursday
TermList -> Friday
TermList -> Saturday
TermList -> Sunday
TermList -> Yesterday
TermList -> Today
TermList -> Tomorrow
TermList -> Now
TermList -> NewYork
TermList -> Boston
TermList -> SanFrancisco
TermList -> Matthew
TermList -> Franco
TermList -> Frasca
TermList -> a
TermList -> b
TermList -> c
TermList -> d
TermList -> e
TermList -> f
TermList -> g
TermList -> h
TermList -> i
TermList -> j
TermList -> k
TermList -> l
TermList -> m
TermList -> n
TermList -> o
TermList -> p
TermList -> q
TermList -> r
TermList -> s
TermList -> t
TermList -> u
TermList -> v
TermList -> w
TermList -> x
TermList -> y
TermList -> z
TermList -> A
TermList -> B
TermList -> C
TermList -> D
TermList -> E
TermList -> F
TermList -> G
TermList -> H
TermList -> I
TermList -> J
TermList -> K
TermList -> L
TermList -> M
TermList -> N
TermList -> O
TermList -> P
TermList -> Q
TermList -> R
TermList -> T
TermList -> U
TermList -> V
TermList -> W
TermList -> X
TermList -> Y
TermList -> Z
TermList -> X17 TermList
X17 -> Term X6
X8 -> X7 TermList
X7 -> Predicate X2
Term -> X16 X3
X16 -> X15 TermList
X15 -> Function X2
Term -> VegetarianFood
Term -> Maharani
Term -> AyCaramba
Term -> Bacaro
Term -> Centro
Term -> Leaf
Term -> Speaker
Term -> TurkeySandwich
Term -> Desk
Term -> Lunch
Term -> FiveDollars
Term -> LotOfTime
Term -> Monday
Term -> Tuesday
Term -> Wednesday
Term -> Thursday
Term -> Friday
Term -> Saturday
Term -> Sunday
Term -> Yesterday
Term -> Today
Term -> Tomorrow
Term -> Now
Term -> NewYork
Term -> Boston
Term -> SanFrancisco
Term -> Matthew
Term -> Franco
Term -> Frasca
Term -> a
Term -> b
Term -> c
Term -> d
Term -> e
Term -> f
Term -> g
Term -> h
Term -> i
Term -> j
Term -> k
Term -> l
Term -> m
Term -> n
Term -> o
Term -> p
Term -> q
Term -> r
Term -> s
Term -> t
Term -> u
Term -> v
Term -> w
Term -> x
Term -> y
Term -> z
Term -> A
Term -> B
Term -> C
Term -> D
Term -> E
Term -> F
Term -> G
Term -> H
Term -> I
Term -> J
Term -> K
Term -> L
Term -> M
Term -> N
Term -> O
Term -> P
Term -> Q
Term -> R
Term -> T
Term -> U
Term -> V
Term -> W
Term -> X
Term -> Y
Term -> Z
Connective -> ∧
Connective -> ∨
Connective -> ⇒
Quantifier -> ∀
Quantifier -> ∃
Variable -> a
Variable -> b
Variable -> c
Variable -> d
Variable -> e
Variable -> f
Variable -> g
Variable -> h
Variable -> i
Variable -> j
Variable -> k
Variable -> l
Variable -> m
Variable -> n
Variable -> o
Variable -> p
Variable -> q
Variable -> r
Variable -> s
Variable -> t
Variable -> u
Variable -> v
Variable -> w
Variable -> x
Variable -> y
Variable -> z
Variable -> A
Variable -> B
Variable -> C
Variable -> D
Variable -> E
Variable -> F
Variable -> G
Variable -> H
Variable -> I
Variable -> J
Variable -> K
Variable -> L
Variable -> M
Variable -> N
Variable -> O
Variable -> P
Variable -> Q
Variable -> R
Variable -> T
Variable -> U
Variable -> V
Variable -> W
Variable -> X
Variable -> Y
Variable -> Z
Predicate -> Serves
Predicate -> Near
Predicate -> Restaurant
Predicate -> Have
Predicate -> VegetarianRestaurant
Predicate -> Eating
Predicate -> Time
Predicate -> Eater
Predicate -> Eaten
Predicate -> Meal
Predicate -> Location
Predicate -> Arriving
Predicate -> Arriver
Predicate -> Destination
Predicate -> EndPoint
Predicate -> Precedes
Predicate -> Closed
Predicate -> ClosedThing
Predicate -> Opened
Predicate -> Opener
Predicate -> Menu
Predicate -> Having
Predicate -> Haver
Predicate -> Had
Function -> LocationOf
Function -> CuisineOf
Function -> IntervalOf
Function -> MemberOf"""
}
