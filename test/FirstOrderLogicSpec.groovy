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

    def 'FOL "λP.(λQ.∀x(P(x)⇒Q(x)))(λx.Restaurant(x))" parses as expected'() {

        when:
        def p = FirstOrderLogic.parse('λP.(λQ.∀x(P(x)⇒Q(x)))(λx.Restaurant(x))')

        then:
        prettyPrint(p.completedParsesString) == """[S280 S
    [S279 Formula
        [S278 LambdaFormula
            [S277 LambdaApplication
                [S206 LambdaAbstraction λ
                    [S19 AbstractionVariable P (1,2)] .
                    [S205 Formula (
                        [S202 Formula
                            [S200 LambdaFormula
                                [S198 LambdaAbstraction λ
                                    [S55 AbstractionVariable Q (5,6)] .
                                    [S197 Formula
                                        [S195 QuantifiedFormula
                                            [S73 Quantifier ∀ (7,8)]
                                            [S78 VariableList
                                                [S77 Variable x (8,9)]
                                             (8,9)]
                                            [S194 Formula (
                                                [S191 Formula
                                                    [S189 LogicFormula
                                                        [S140 Formula
                                                            [S139 AtomicFormula
                                                                [S112 AbstractionVariable P (10,11)]
                                                             (
                                                                [S137 TermOrFormula
                                                                    [S136 Term
                                                                        [S125 Variable x (12,13)]
                                                                     (12,13)]
                                                                 (12,13)] ) (10,14)]
                                                         (10,14)]
                                                        [S143 Connective ⇒ (14,15)]
                                                        [S188 Formula
                                                            [S187 AtomicFormula
                                                                [S160 AbstractionVariable Q (15,16)]
                                                             (
                                                                [S185 TermOrFormula
                                                                    [S184 Term
                                                                        [S173 Variable x (17,18)]
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
                [S274 TermOrFormula
                    [S273 Formula
                        [S271 LambdaFormula
                            [S269 LambdaAbstraction λ
                                [S236 Variable x (23,24)] .
                                [S268 Formula
                                    [S267 AtomicFormula
                                        [S254 Predicate Restaurant (25,26)]
                                     (
                                        [S264 TermList
                                            [S263 Term
                                                [S262 Variable x (27,28)]
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
        prettyPrint(p.completedParsesString) == """[S316 S
    [S315 Formula
        [S314 LambdaFormula
            [S313 LambdaApplication
                [S148 LambdaAbstraction λ
                    [S19 AbstractionVariable Q (1,2)] .
                    [S147 Formula
                        [S145 QuantifiedFormula
                            [S37 Quantifier ∀ (3,4)]
                            [S42 VariableList
                                [S41 Variable x (4,5)]
                             (4,5)]
                            [S144 Formula (
                                [S141 Formula
                                    [S139 LogicFormula
                                        [S90 Formula
                                            [S89 AtomicFormula
                                                [S76 Predicate Restaurant (6,7)]
                                             (
                                                [S86 TermList
                                                    [S85 Term
                                                        [S84 Variable x (8,9)]
                                                     (8,9)]
                                                 (8,9)] ) (6,10)]
                                         (6,10)]
                                        [S93 Connective ⇒ (10,11)]
                                        [S138 Formula
                                            [S137 AtomicFormula
                                                [S110 AbstractionVariable Q (11,12)]
                                             (
                                                [S135 TermOrFormula
                                                    [S134 Term
                                                        [S123 Variable x (13,14)]
                                                     (13,14)]
                                                 (13,14)] ) (11,15)]
                                         (11,15)]
                                     (6,15)]
                                 (6,15)] ) (5,16)]
                         (3,16)]
                     (3,16)]
                 (0,16)]
             (
                [S310 TermOrFormula
                    [S309 Formula
                        [S307 LambdaFormula
                            [S305 LambdaAbstraction λ
                                [S178 Variable y (18,19)] .
                                [S304 Formula
                                    [S302 QuantifiedFormula
                                        [S196 Quantifier ∃ (20,21)]
                                        [S201 VariableList
                                            [S200 Variable e (21,22)]
                                         (21,22)]
                                        [S301 Formula (
                                            [S298 Formula
                                                [S296 LogicFormula
                                                    [S249 Formula
                                                        [S248 AtomicFormula
                                                            [S235 Predicate Closed (23,24)]
                                                         (
                                                            [S245 TermList
                                                                [S244 Term
                                                                    [S243 Variable e (25,26)]
                                                                 (25,26)]
                                                             (25,26)] ) (23,27)]
                                                     (23,27)]
                                                    [S252 Connective ∧ (27,28)]
                                                    [S295 Formula
                                                        [S294 AtomicFormula
                                                            [S269 Predicate ClosedThing (28,29)]
                                                         (
                                                            [S292 TermList
                                                                [S278 Term
                                                                    [S277 Variable e (30,31)]
                                                                 (30,31)] ,
                                                                [S290 TermList
                                                                    [S289 Term
                                                                        [S288 Variable y (32,33)]
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
AtomicFormula -> AbstractionVariable ( TermOrFormula )
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
