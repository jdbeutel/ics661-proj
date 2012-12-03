import spock.lang.Specification
import spock.lang.Unroll

import static parser.Parser.prettyPrint
import fol.FirstOrderLogic

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
        prettyPrint(p.completedParsesString) == """[S33 S
    [S32 Formula
        [S31 AtomicFormula
            [S18 Predicate Restaurant (0,1)]
         (
            [S28 TermList
                [S27 Term
                    [S26 Constant Maharani (2,3)]
                 (2,3)]
             (2,3)] ) (0,4)]
     (0,4)]
 (0,4)]"""
    }

    def 'FOL "Have(Speaker, FiveDollars) ∧ ¬Have(Speaker, LotOfTime)" parses as expected'() {

        when:
        def p = FirstOrderLogic.parse('Have(Speaker, FiveDollars) ∧ ¬Have(Speaker, LotOfTime)')

        then:
        prettyPrint(p.completedParsesString) == """[S115 S
    [S114 Formula
        [S112 LogicFormula
            [S44 Formula
                [S43 AtomicFormula
                    [S18 Predicate Have (0,1)]
                 (
                    [S41 TermList
                        [S27 Term
                            [S26 Constant Speaker (2,3)]
                         (2,3)] ,
                        [S39 TermList
                            [S38 Term
                                [S37 Constant FiveDollars (4,5)]
                             (4,5)]
                         (4,5)]
                     (2,5)] ) (0,6)]
             (0,6)]
            [S47 Connective ∧ (6,7)]
            [S111 Formula
                [S109 LogicFormula ¬
                    [S108 Formula
                        [S107 AtomicFormula
                            [S82 Predicate Have (8,9)]
                         (
                            [S105 TermList
                                [S91 Term
                                    [S90 Constant Speaker (10,11)]
                                 (10,11)] ,
                                [S103 TermList
                                    [S102 Term
                                        [S101 Constant LotOfTime (12,13)]
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
        prettyPrint(p.completedParsesString) == """[S130 S
    [S129 Formula
        [S127 QuantifiedFormula
            [S18 Quantifier ∀ (0,1)]
            [S23 VariableList
                [S22 Variable x (1,2)]
             (1,2)]
            [S126 Formula (
                [S123 Formula
                    [S121 LogicFormula
                        [S73 Formula
                            [S72 AtomicFormula
                                [S59 Predicate VegetarianRestaurant (3,4)]
                             (
                                [S69 TermList
                                    [S68 Term
                                        [S67 Variable x (5,6)]
                                     (5,6)]
                                 (5,6)] ) (3,7)]
                         (3,7)]
                        [S76 Connective ⇒ (7,8)]
                        [S120 Formula
                            [S119 AtomicFormula
                                [S94 Predicate Serves (8,9)]
                             (
                                [S117 TermList
                                    [S103 Term
                                        [S102 Variable x (10,11)]
                                     (10,11)] ,
                                    [S115 TermList
                                        [S114 Term
                                            [S113 Constant VegetarianFood (12,13)]
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
        prettyPrint(p.completedParsesString) == """[S97 S
    [S96 Formula
        [S94 LambdaFormula
            [S92 LambdaAbstraction λ
                [S20 Variable x (1,2)] .
                [S91 Formula
                    [S89 LambdaFormula
                        [S87 LambdaAbstraction λ
                            [S41 Variable y (4,5)] .
                            [S86 Formula
                                [S85 AtomicFormula
                                    [S60 Predicate Near (6,7)]
                                 (
                                    [S83 TermList
                                        [S69 Term
                                            [S68 Variable x (8,9)]
                                         (8,9)] ,
                                        [S81 TermList
                                            [S80 Term
                                                [S79 Variable y (10,11)]
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
        prettyPrint(p.completedParsesString) == """[S148 S
    [S147 Formula
        [S146 LambdaFormula
            [S145 LambdaApplication
                [S112 LambdaAbstraction λ
                    [S20 Variable x (1,2)] .
                    [S111 Formula (
                        [S108 Formula
                            [S106 LambdaFormula
                                [S104 LambdaAbstraction λ
                                    [S58 Variable y (5,6)] .
                                    [S103 Formula
                                        [S102 AtomicFormula
                                            [S77 Predicate Near (7,8)]
                                         (
                                            [S100 TermList
                                                [S86 Term
                                                    [S85 Variable x (9,10)]
                                                 (9,10)] ,
                                                [S98 TermList
                                                    [S97 Term
                                                        [S96 Variable y (11,12)]
                                                     (11,12)]
                                                 (11,12)]
                                             (9,12)] ) (7,13)]
                                     (7,13)]
                                 (4,13)]
                             (4,13)]
                         (4,13)] ) (3,14)]
                 (0,14)]
             (
                [S143 TermOrFormula
                    [S142 Term
                        [S131 Constant Bacaro (15,16)]
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
        prettyPrint(p.completedParsesString) == """[S71 S
    [S70 Formula
        [S68 LambdaFormula
            [S66 LambdaAbstraction λ
                [S20 Variable y (1,2)] .
                [S65 Formula
                    [S64 AtomicFormula
                        [S39 Predicate Near (3,4)]
                     (
                        [S62 TermList
                            [S48 Term
                                [S47 Constant Bacaro (5,6)]
                             (5,6)] ,
                            [S60 TermList
                                [S59 Term
                                    [S58 Variable y (7,8)]
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
        prettyPrint(p.completedParsesString) == """[S45 S
    [S44 Formula
        [S43 AtomicFormula
            [S18 Predicate Near (0,1)]
         (
            [S41 TermList
                [S27 Term
                    [S26 Constant Bacaro (2,3)]
                 (2,3)] ,
                [S39 TermList
                    [S38 Term
                        [S37 Constant Centro (4,5)]
                     (4,5)]
                 (4,5)]
             (2,5)] ) (0,6)]
     (0,6)]
 (0,6)]"""
    }

    def 'FOL "λP.(λQ.∀x(P(x)⇒Q(x)))(λx.Restaurant(x))" parses as expected'() {

        when:
        def p = FirstOrderLogic.parse('λP.(λQ.∀x(P(x)⇒Q(x)))(λx.Restaurant(x))')

        then:
        prettyPrint(p.completedParsesString) == """[S291 S
    [S290 Formula
        [S289 LambdaFormula
            [S288 LambdaApplication
                [S215 LambdaAbstraction λ
                    [S20 AbstractionVariable P (1,2)] .
                    [S214 Formula (
                        [S211 Formula
                            [S209 LambdaFormula
                                [S207 LambdaAbstraction λ
                                    [S58 AbstractionVariable Q (5,6)] .
                                    [S206 Formula
                                        [S204 QuantifiedFormula
                                            [S77 Quantifier ∀ (7,8)]
                                            [S82 VariableList
                                                [S81 Variable x (8,9)]
                                             (8,9)]
                                            [S203 Formula (
                                                [S200 Formula
                                                    [S198 LogicFormula
                                                        [S147 Formula
                                                            [S146 VariableApplication
                                                                [S118 AbstractionVariable P (10,11)]
                                                             (
                                                                [S144 TermOrFormula
                                                                    [S143 Term
                                                                        [S132 Variable x (12,13)]
                                                                     (12,13)]
                                                                 (12,13)] ) (10,14)]
                                                         (10,14)]
                                                        [S150 Connective ⇒ (14,15)]
                                                        [S197 Formula
                                                            [S196 VariableApplication
                                                                [S168 AbstractionVariable Q (15,16)]
                                                             (
                                                                [S194 TermOrFormula
                                                                    [S193 Term
                                                                        [S182 Variable x (17,18)]
                                                                     (17,18)]
                                                                 (17,18)] ) (15,19)]
                                                         (15,19)]
                                                     (10,19)]
                                                 (10,19)] ) (9,20)]
                                         (7,20)]
                                     (7,20)]
                                 (4,20)]
                             (4,20)]
                         (4,20)] ) (3,21)]
                 (0,21)]
             (
                [S285 TermOrFormula
                    [S284 Formula
                        [S282 LambdaFormula
                            [S280 LambdaAbstraction λ
                                [S246 Variable x (23,24)] .
                                [S279 Formula
                                    [S278 AtomicFormula
                                        [S265 Predicate Restaurant (25,26)]
                                     (
                                        [S275 TermList
                                            [S274 Term
                                                [S273 Variable x (27,28)]
                                             (27,28)]
                                         (27,28)] ) (25,29)]
                                 (25,29)]
                             (22,29)]
                         (22,29)]
                     (22,29)]
                 (22,29)] ) (0,30)]
         (0,30)]
     (0,30)]
 (0,30)]"""
    }

    def 'FOL "λQ.∀x(Restaurant(x)⇒Q(x))(λy.∃e(Closed(e)∧ClosedThing(e,y)))" parses as expected'() {

        when:
        def p = FirstOrderLogic.parse('λQ.∀x(Restaurant(x)⇒Q(x))(λy.∃e(Closed(e)∧ClosedThing(e,y)))')

        then:
        prettyPrint(p.completedParsesString) == """[S327 S
    [S326 Formula
        [S325 LambdaFormula
            [S324 LambdaApplication
                [S154 LambdaAbstraction λ
                    [S20 AbstractionVariable Q (1,2)] .
                    [S153 Formula
                        [S151 QuantifiedFormula
                            [S39 Quantifier ∀ (3,4)]
                            [S44 VariableList
                                [S43 Variable x (4,5)]
                             (4,5)]
                            [S150 Formula (
                                [S147 Formula
                                    [S145 LogicFormula
                                        [S94 Formula
                                            [S93 AtomicFormula
                                                [S80 Predicate Restaurant (6,7)]
                                             (
                                                [S90 TermList
                                                    [S89 Term
                                                        [S88 Variable x (8,9)]
                                                     (8,9)]
                                                 (8,9)] ) (6,10)]
                                         (6,10)]
                                        [S97 Connective ⇒ (10,11)]
                                        [S144 Formula
                                            [S143 VariableApplication
                                                [S115 AbstractionVariable Q (11,12)]
                                             (
                                                [S141 TermOrFormula
                                                    [S140 Term
                                                        [S129 Variable x (13,14)]
                                                     (13,14)]
                                                 (13,14)] ) (11,15)]
                                         (11,15)]
                                     (6,15)]
                                 (6,15)] ) (5,16)]
                         (3,16)]
                     (3,16)]
                 (0,16)]
             (
                [S321 TermOrFormula
                    [S320 Formula
                        [S318 LambdaFormula
                            [S316 LambdaAbstraction λ
                                [S185 Variable y (18,19)] .
                                [S315 Formula
                                    [S313 QuantifiedFormula
                                        [S204 Quantifier ∃ (20,21)]
                                        [S209 VariableList
                                            [S208 Variable e (21,22)]
                                         (21,22)]
                                        [S312 Formula (
                                            [S309 Formula
                                                [S307 LogicFormula
                                                    [S259 Formula
                                                        [S258 AtomicFormula
                                                            [S245 Predicate Closed (23,24)]
                                                         (
                                                            [S255 TermList
                                                                [S254 Term
                                                                    [S253 Variable e (25,26)]
                                                                 (25,26)]
                                                             (25,26)] ) (23,27)]
                                                     (23,27)]
                                                    [S262 Connective ∧ (27,28)]
                                                    [S306 Formula
                                                        [S305 AtomicFormula
                                                            [S280 Predicate ClosedThing (28,29)]
                                                         (
                                                            [S303 TermList
                                                                [S289 Term
                                                                    [S288 Variable e (30,31)]
                                                                 (30,31)] ,
                                                                [S301 TermList
                                                                    [S300 Term
                                                                        [S299 Variable y (32,33)]
                                                                     (32,33)]
                                                                 (32,33)]
                                                             (30,33)] ) (28,34)]
                                                     (28,34)]
                                                 (23,34)]
                                             (23,34)] ) (22,35)]
                                     (20,35)]
                                 (20,35)]
                             (17,35)]
                         (17,35)]
                     (17,35)]
                 (17,35)] ) (0,36)]
         (0,36)]
     (0,36)]
 (0,36)]"""
    }

    @Unroll
    def 'FOL "#input" is #expected'() {

        when:
        FirstOrderLogic.parse(input)

        then:
        IllegalArgumentException e = thrown()
        e.message.contains(expected as String)

        where:
        input                                                           || expected
        'foo'                                                           || 'unparsable'
        '∀x VegetarianRestaurant(x) ⇒ Serves(x, VegetarianFood)'        || 'ambiguous input (2 parses)'
        'λx.λy.Near(x,y)(Bacaro)'                                       || 'ambiguous input (2 parses)'
        'λP.λQ.∀x P(x)⇒Q(x)(λx.Restaurant(x))'                          || 'ambiguous input (5 parses)'
        'λP.λQ.∀x(P(x)⇒Q(x))(λx.Restaurant(x))'                         || 'ambiguous input (2 parses)'
        'λQ.∀x Restaurant(x)⇒Q(x)(λy.∃e Closed(e)∧ClosedThing(e,y))'    || 'ambiguous input (6 parses)'
        'λQ.∀x(Restaurant(x)⇒Q(x))(λy.∃e Closed(e)∧ClosedThing(e,y))'   || 'ambiguous input (3 parses)'
    }

    static final FOL_DEF = """S -> Formula
Formula -> LambdaFormula
Formula -> QuantifiedFormula
Formula -> LogicFormula
Formula -> AtomicFormula
Formula -> VariableApplication
Formula -> ( Formula )
LambdaFormula -> LambdaAbstraction
LambdaFormula -> LambdaApplication
LambdaAbstraction -> λ Variable . Formula
LambdaAbstraction -> λ AbstractionVariable . Formula
LambdaApplication -> LambdaAbstraction ( TermOrFormula )
QuantifiedFormula -> Quantifier VariableList Formula
LogicFormula -> Formula Connective Formula
LogicFormula -> ¬ Formula
AtomicFormula -> Predicate ( TermList )
VariableApplication -> AbstractionVariable ( TermOrFormula )
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
