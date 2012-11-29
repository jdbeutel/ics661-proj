import spock.lang.Specification
import spock.lang.Unroll

import static Parser.prettyPrint

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

    def 'FOL grammar is not normalized'() {

        given:
        def g = FirstOrderLogic.GRAMMAR

        expect:
        g.toString() == FOL_DEF
    }

    def 'FOL "Restaurant(Maharani)" parses as expected'() {

        when:
        def p = FirstOrderLogic.parse('Restaurant(Maharani)')

        then:
        prettyPrint(p.completedParsesString) == """[S32 S
    [S31 Formula
        [S30 AtomicFormula
            [S17 Predicate Restaurant (0,1)]
         (
            [S27 TermList
                [S26 Term
                    [S25 Constant Maharani (2,3)]
                 (2,3)]
             (2,3)] ) (0,4)]
     (0,4)]
 (0,4)]"""
    }

    def 'FOL "Have(Speaker, FiveDollars) ∧ ¬Have(Speaker, LotOfTime)" parses as expected'() {

        when:
        def p = FirstOrderLogic.parse('Have(Speaker, FiveDollars) ∧ ¬Have(Speaker, LotOfTime)')

        then:
        prettyPrint(p.completedParsesString) == """[S112 S
    [S111 Formula
        [S109 LogicFormula
            [S43 Formula
                [S42 AtomicFormula
                    [S17 Predicate Have (0,1)]
                 (
                    [S40 TermList
                        [S26 Term
                            [S25 Constant Speaker (2,3)]
                         (2,3)] ,
                        [S38 TermList
                            [S37 Term
                                [S36 Constant FiveDollars (4,5)]
                             (4,5)]
                         (4,5)]
                     (2,5)] ) (0,6)]
             (0,6)]
            [S46 Connective ∧ (6,7)]
            [S108 Formula
                [S106 LogicFormula ¬
                    [S105 Formula
                        [S104 AtomicFormula
                            [S79 Predicate Have (8,9)]
                         (
                            [S102 TermList
                                [S88 Term
                                    [S87 Constant Speaker (10,11)]
                                 (10,11)] ,
                                [S100 TermList
                                    [S99 Term
                                        [S98 Constant LotOfTime (12,13)]
                                     (12,13)]
                                 (12,13)]
                             (10,13)] ) (8,14)]
                     (8,14)]
                 (7,14)]
             (7,14)]
         (0,14)]
     (0,14)]
 (0,14)]"""
    }

    def 'FOL "∀x(VegetarianRestaurant(x) ⇒ Serves(x, VegetarianFood))" parses as expected'() {

        when:
        def p = FirstOrderLogic.parse('∀x(VegetarianRestaurant(x) ⇒ Serves(x, VegetarianFood))')

        then:
        prettyPrint(p.completedParsesString) == """[S126 S
    [S125 Formula
        [S123 QuantifiedFormula
            [S17 Quantifier ∀ (0,1)]
            [S22 VariableList
                [S21 Variable x (1,2)]
             (1,2)]
            [S122 Formula (
                [S119 Formula
                    [S117 LogicFormula
                        [S70 Formula
                            [S69 AtomicFormula
                                [S56 Predicate VegetarianRestaurant (3,4)]
                             (
                                [S66 TermList
                                    [S65 Term
                                        [S64 Variable x (5,6)]
                                     (5,6)]
                                 (5,6)] ) (3,7)]
                         (3,7)]
                        [S73 Connective ⇒ (7,8)]
                        [S116 Formula
                            [S115 AtomicFormula
                                [S90 Predicate Serves (8,9)]
                             (
                                [S113 TermList
                                    [S99 Term
                                        [S98 Variable x (10,11)]
                                     (10,11)] ,
                                    [S111 TermList
                                        [S110 Term
                                            [S109 Constant VegetarianFood (12,13)]
                                         (12,13)]
                                     (12,13)]
                                 (10,13)] ) (8,14)]
                         (8,14)]
                     (3,14)]
                 (3,14)] ) (2,15)]
         (0,15)]
     (0,15)]
 (0,15)]"""
    }

    def 'FOL "λx.λy.Near(x,y)" parses as expected'() {

        when:
        def p = FirstOrderLogic.parse('λx.λy.Near(x,y)')

        then:
        prettyPrint(p.completedParsesString) == """[S94 S
    [S93 Formula
        [S91 LambdaFormula
            [S89 LambdaAbstraction λ
                [S19 Variable x (1,2)] .
                [S88 Formula
                    [S86 LambdaFormula
                        [S84 LambdaAbstraction λ
                            [S39 Variable y (4,5)] .
                            [S83 Formula
                                [S82 AtomicFormula
                                    [S57 Predicate Near (6,7)]
                                 (
                                    [S80 TermList
                                        [S66 Term
                                            [S65 Variable x (8,9)]
                                         (8,9)] ,
                                        [S78 TermList
                                            [S77 Term
                                                [S76 Variable y (10,11)]
                                             (10,11)]
                                         (10,11)]
                                     (8,11)] ) (6,12)]
                             (6,12)]
                         (3,12)]
                     (3,12)]
                 (3,12)]
             (0,12)]
         (0,12)]
     (0,12)]
 (0,12)]"""
    }

    def 'FOL "λx.(λy.Near(x,y))(Bacaro)" parses as expected'() {

        when:
        def p = FirstOrderLogic.parse('λx.(λy.Near(x,y))(Bacaro)')

        then:
        prettyPrint(p.completedParsesString) == """[S143 S
    [S142 Formula
        [S141 LambdaFormula
            [S140 LambdaApplication
                [S108 LambdaAbstraction λ
                    [S19 Variable x (1,2)] .
                    [S107 Formula (
                        [S104 Formula
                            [S102 LambdaFormula
                                [S100 LambdaAbstraction λ
                                    [S55 Variable y (5,6)] .
                                    [S99 Formula
                                        [S98 AtomicFormula
                                            [S73 Predicate Near (7,8)]
                                         (
                                            [S96 TermList
                                                [S82 Term
                                                    [S81 Variable x (9,10)]
                                                 (9,10)] ,
                                                [S94 TermList
                                                    [S93 Term
                                                        [S92 Variable y (11,12)]
                                                     (11,12)]
                                                 (11,12)]
                                             (9,12)] ) (7,13)]
                                     (7,13)]
                                 (4,13)]
                             (4,13)]
                         (4,13)] ) (3,14)]
                 (0,14)]
             (
                [S138 TermOrFormula
                    [S137 Term
                        [S126 Constant Bacaro (15,16)]
                     (15,16)]
                 (15,16)] ) (0,17)]
         (0,17)]
     (0,17)]
 (0,17)]"""
    }

    def 'FOL "λy.Near(Bacaro,y)" parses as expected'() {

        when:
        def p = FirstOrderLogic.parse('λy.Near(Bacaro,y)')

        then:
        prettyPrint(p.completedParsesString) == """[S69 S
    [S68 Formula
        [S66 LambdaFormula
            [S64 LambdaAbstraction λ
                [S19 Variable y (1,2)] .
                [S63 Formula
                    [S62 AtomicFormula
                        [S37 Predicate Near (3,4)]
                     (
                        [S60 TermList
                            [S46 Term
                                [S45 Constant Bacaro (5,6)]
                             (5,6)] ,
                            [S58 TermList
                                [S57 Term
                                    [S56 Variable y (7,8)]
                                 (7,8)]
                             (7,8)]
                         (5,8)] ) (3,9)]
                 (3,9)]
             (0,9)]
         (0,9)]
     (0,9)]
 (0,9)]"""
    }

    def 'FOL "Near(Bacaro,Centro)" parses as expected'() {

        when:
        def p = FirstOrderLogic.parse('Near(Bacaro,Centro)')

        then:
        prettyPrint(p.completedParsesString) == """[S44 S
    [S43 Formula
        [S42 AtomicFormula
            [S17 Predicate Near (0,1)]
         (
            [S40 TermList
                [S26 Term
                    [S25 Constant Bacaro (2,3)]
                 (2,3)] ,
                [S38 TermList
                    [S37 Term
                        [S36 Constant Centro (4,5)]
                     (4,5)]
                 (4,5)]
             (2,5)] ) (0,6)]
     (0,6)]
 (0,6)]"""
    }

    @Unroll
    def 'FOL "#input" is #expected'() {

        when:
        FirstOrderLogic.parse(input)

        then:
        IllegalArgumentException e = thrown()
        e.message.contains(expected as String)

        where:
        input                                                       || expected
        'foo'                                                       || 'unparsable'
        '∀x VegetarianRestaurant(x) ⇒ Serves(x, VegetarianFood)'    || 'ambiguous'
        'λx.λy.Near(x,y)(Bacaro)'                                   || 'ambiguous'
    }

    static final FOL_DEF = """S -> Formula
Formula -> LambdaFormula
Formula -> QuantifiedFormula
Formula -> LogicFormula
Formula -> AtomicFormula
Formula -> ( Formula )
LambdaFormula -> LambdaAbstraction
LambdaFormula -> LambdaApplication
LambdaAbstraction -> λ Variable . Formula
LambdaAbstraction -> λ AbstractionVariable . Formula
LambdaApplication -> LambdaAbstraction ( TermOrFormula )
LambdaApplication -> AbstractionVariable ( TermOrFormula )
QuantifiedFormula -> Quantifier VariableList Formula
LogicFormula -> Formula Connective Formula
LogicFormula -> ¬ Formula
AtomicFormula -> Predicate ( TermList )
TermOrFormula -> Term
TermOrFormula -> Formula
VariableList -> Variable
VariableList -> Variable , VariableList
TermList -> Term
TermList -> Term , TermList
Term -> Function ( TermList )
Term -> Constant
Term -> Variable
Connective -> ∧
Connective -> ∨
Connective -> ⇒
Quantifier -> ∀
Quantifier -> ∃
Constant -> VegetarianFood
Constant -> Maharani
Constant -> AyCaramba
Constant -> Bacaro
Constant -> Centro
Constant -> Leaf
Constant -> Speaker
Constant -> TurkeySandwich
Constant -> Desk
Constant -> Lunch
Constant -> FiveDollars
Constant -> LotOfTime
Constant -> Monday
Constant -> Tuesday
Constant -> Wednesday
Constant -> Thursday
Constant -> Friday
Constant -> Saturday
Constant -> Sunday
Constant -> Yesterday
Constant -> Today
Constant -> Tomorrow
Constant -> Now
Constant -> NewYork
Constant -> Boston
Constant -> SanFrancisco
Constant -> Matthew
Constant -> Franco
Constant -> Frasca
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
AbstractionVariable -> A
AbstractionVariable -> B
AbstractionVariable -> C
AbstractionVariable -> D
AbstractionVariable -> E
AbstractionVariable -> F
AbstractionVariable -> G
AbstractionVariable -> H
AbstractionVariable -> I
AbstractionVariable -> J
AbstractionVariable -> K
AbstractionVariable -> L
AbstractionVariable -> M
AbstractionVariable -> N
AbstractionVariable -> O
AbstractionVariable -> P
AbstractionVariable -> Q
AbstractionVariable -> R
AbstractionVariable -> T
AbstractionVariable -> U
AbstractionVariable -> V
AbstractionVariable -> W
AbstractionVariable -> X
AbstractionVariable -> Y
AbstractionVariable -> Z
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
