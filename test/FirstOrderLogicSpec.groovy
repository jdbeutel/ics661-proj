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

        given:
        def input = 'Restaurant(Maharani)'

        when:
        def p = FirstOrderLogic.parseTree(input)

        then:
        p.prettyPrint() == """
[S
    [Formula
        [AtomicFormula
            [Predicate Restaurant]
            (
            [TermList
                [Term
                    [Constant Maharani]
                ]
            ]
            )
        ]
    ]
]"""

        when:
        def l = FirstOrderLogic.parseLambda(input)

        then:
        l.toString() == input

        and: 'already in normal form'
        l.normalizationString == input
    }


    def 'FOL "Restaurant(Maharani)" parses as expected with details'() {

        given:
        def input = 'Restaurant(Maharani)'

        when:
        def p = FirstOrderLogic.parse(input)

        then:
        prettyPrint(p.completedParsesString) == """[S34 S
    [S33 Formula
        [S32 AtomicFormula
            [S19 Predicate Restaurant (0,1)]
         (
            [S29 TermList
                [S28 Term
                    [S27 Constant Maharani (2,3)]
                 (2,3)]
             (2,3)] ) (0,4)]
     (0,4)]
 (0,4)]"""

        when:
        def l = FirstOrderLogic.parseLambda(input)

        then:
        l.toString() == input
    }

    def 'FOL "Have(Speaker,FiveDollars)∧¬Have(Speaker,LotOfTime)" parses as expected'() {

        given:
        def input = 'Have(Speaker,FiveDollars)∧¬Have(Speaker,LotOfTime)'

        when:
        def p = FirstOrderLogic.parseTree(input)

        then:
        p.prettyPrint() == """
[S
    [Formula
        [LogicFormula
            [Formula
                [AtomicFormula
                    [Predicate Have]
                    (
                    [TermList
                        [Term
                            [Constant Speaker]
                        ]
                        ,
                        [TermList
                            [Term
                                [Constant FiveDollars]
                            ]
                        ]
                    ]
                    )
                ]
            ]
            [Connective ∧]
            [Formula
                [LogicFormula
                    ¬
                    [Formula
                        [AtomicFormula
                            [Predicate Have]
                            (
                            [TermList
                                [Term
                                    [Constant Speaker]
                                ]
                                ,
                                [TermList
                                    [Term
                                        [Constant LotOfTime]
                                    ]
                                ]
                            ]
                            )
                        ]
                    ]
                ]
            ]
        ]
    ]
]"""

        when:
        def l = FirstOrderLogic.parseLambda(input)

        then:
        l.toString() == input

        and: 'already in normal form'
        l.normalizationString == input
    }

    def 'FOL "∀x(VegetarianRestaurant(x)⇒Serves(x,VegetarianFood))" parses as expected'() {

        given:
        def input = '∀x(VegetarianRestaurant(x)⇒Serves(x,VegetarianFood))'

        when:
        def p = FirstOrderLogic.parseTree(input)

        then:
        p.prettyPrint() == """
[S
    [Formula
        [QuantifiedFormula
            [Quantifier ∀]
            [VariableList
                [Variable x]
            ]
            [Formula
                [ParentheticalFormula
                    (
                    [Formula
                        [LogicFormula
                            [Formula
                                [AtomicFormula
                                    [Predicate VegetarianRestaurant]
                                    (
                                    [TermList
                                        [Term
                                            [Variable x]
                                        ]
                                    ]
                                    )
                                ]
                            ]
                            [Connective ⇒]
                            [Formula
                                [AtomicFormula
                                    [Predicate Serves]
                                    (
                                    [TermList
                                        [Term
                                            [Variable x]
                                        ]
                                        ,
                                        [TermList
                                            [Term
                                                [Constant VegetarianFood]
                                            ]
                                        ]
                                    ]
                                    )
                                ]
                            ]
                        ]
                    ]
                    )
                ]
            ]
        ]
    ]
]"""

        when:
        def l = FirstOrderLogic.parseLambda(input)

        then:
        l.toString() == input

        and: 'already in normal form'
        l.normalizationString == input
    }

    def 'FOL "λx.λy.Near(x,y)" parses as expected'() {

        given:
        def input = 'λx.λy.Near(x,y)'
        def canonicalInput = 'λx.(λy.(Near(x,y)))'
        def expected = """
[S
    [Formula
        [LambdaFormula
            [LambdaAbstraction
                λ
                [Variable x]
                .
                [Formula
                    [LambdaFormula
                        [LambdaAbstraction
                            λ
                            [Variable y]
                            .
                            [Formula
                                [AtomicFormula
                                    [Predicate Near]
                                    (
                                    [TermList
                                        [Term
                                            [Variable x]
                                        ]
                                        ,
                                        [TermList
                                            [Term
                                                [Variable y]
                                            ]
                                        ]
                                    ]
                                    )
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    ]
]"""

        expect:
        FirstOrderLogic.parseTree(input).prettyPrint() == expected
        FirstOrderLogic.parseLambda(input).toString() == canonicalInput

        and: 'already in normal form'
        FirstOrderLogic.parseLambda(input).normalizationString == canonicalInput
    }

    def 'FOL "λx.(λy.Near(x,y))(Bacaro)" parses as expected'() {

        given:
        def input = 'λx.(λy.Near(x,y))(Bacaro)'
        def canonicalInput = 'λx.(λy.(Near(x,y)))(Bacaro)'

        when:
        def p = FirstOrderLogic.parseTree(input)

        then:
        p.prettyPrint() == """
[S
    [Formula
        [LambdaFormula
            [LambdaApplication
                [LambdaAbstraction
                    λ
                    [Variable x]
                    .
                    [Formula
                        [ParentheticalFormula
                            (
                            [Formula
                                [LambdaFormula
                                    [LambdaAbstraction
                                        λ
                                        [Variable y]
                                        .
                                        [Formula
                                            [AtomicFormula
                                                [Predicate Near]
                                                (
                                                [TermList
                                                    [Term
                                                        [Variable x]
                                                    ]
                                                    ,
                                                    [TermList
                                                        [Term
                                                            [Variable y]
                                                        ]
                                                    ]
                                                ]
                                                )
                                            ]
                                        ]
                                    ]
                                ]
                            ]
                            )
                        ]
                    ]
                ]
                (
                [TermOrFormula
                    [Term
                        [Constant Bacaro]
                    ]
                ]
                )
            ]
        ]
    ]
]"""

        when:
        def l = FirstOrderLogic.parseLambda(input)

        then:
        l.toString() == canonicalInput

        and: 'already in normal form'
        l.normalizationString == [
                'λx.(λy.(Near(x,y)))(Bacaro)',
                'λy.(Near(Bacaro,y))',
        ].join('\n')
    }

    def 'FOL "λy.Near(Bacaro,y)" parses as expected'() {

        given:
        def input = 'λy.Near(Bacaro,y)'
        def canonicalInput = 'λy.(Near(Bacaro,y))'

        when:
        def p = FirstOrderLogic.parseTree(input)

        then:
        p.prettyPrint() == """
[S
    [Formula
        [LambdaFormula
            [LambdaAbstraction
                λ
                [Variable y]
                .
                [Formula
                    [AtomicFormula
                        [Predicate Near]
                        (
                        [TermList
                            [Term
                                [Constant Bacaro]
                            ]
                            ,
                            [TermList
                                [Term
                                    [Variable y]
                                ]
                            ]
                        ]
                        )
                    ]
                ]
            ]
        ]
    ]
]"""

        when:
        def l = FirstOrderLogic.parseLambda(input)

        then:
        l.toString() == canonicalInput

        and: 'already in normal form'
        l.normalizationString == canonicalInput
    }

    def 'FOL "Near(Bacaro,Centro)" parses as expected'() {

        given:
        def input = 'Near(Bacaro,Centro)'

        when:
        def p = FirstOrderLogic.parseTree(input)

        then:
        p.prettyPrint() == """
[S
    [Formula
        [AtomicFormula
            [Predicate Near]
            (
            [TermList
                [Term
                    [Constant Bacaro]
                ]
                ,
                [TermList
                    [Term
                        [Constant Centro]
                    ]
                ]
            ]
            )
        ]
    ]
]"""

        when:
        def l = FirstOrderLogic.parseLambda(input)

        then:
        l.toString() == input

        and: 'already in normal form'
        l.normalizationString == input
    }

    def 'FOL "λP.(λQ.∀x(P(x)⇒Q(x)))(λx.Restaurant(x))" parses as expected'() {

        given:
        def input = 'λP.(λQ.∀x(P(x)⇒Q(x)))(λx.Restaurant(x))'
        def canonicalInput = 'λP.(λQ.(∀x(P(x)⇒Q(x))))(λx.(Restaurant(x)))'

        when:
        def p = FirstOrderLogic.parseTree(input)

        then:
        p.prettyPrint() == """
[S
    [Formula
        [LambdaFormula
            [LambdaApplication
                [LambdaAbstraction
                    λ
                    [AbstractionVariable P]
                    .
                    [Formula
                        [ParentheticalFormula
                            (
                            [Formula
                                [LambdaFormula
                                    [LambdaAbstraction
                                        λ
                                        [AbstractionVariable Q]
                                        .
                                        [Formula
                                            [QuantifiedFormula
                                                [Quantifier ∀]
                                                [VariableList
                                                    [Variable x]
                                                ]
                                                [Formula
                                                    [ParentheticalFormula
                                                        (
                                                        [Formula
                                                            [LogicFormula
                                                                [Formula
                                                                    [VariableApplication
                                                                        [AbstractionVariable P]
                                                                        (
                                                                        [TermOrFormula
                                                                            [Term
                                                                                [Variable x]
                                                                            ]
                                                                        ]
                                                                        )
                                                                    ]
                                                                ]
                                                                [Connective ⇒]
                                                                [Formula
                                                                    [VariableApplication
                                                                        [AbstractionVariable Q]
                                                                        (
                                                                        [TermOrFormula
                                                                            [Term
                                                                                [Variable x]
                                                                            ]
                                                                        ]
                                                                        )
                                                                    ]
                                                                ]
                                                            ]
                                                        ]
                                                        )
                                                    ]
                                                ]
                                            ]
                                        ]
                                    ]
                                ]
                            ]
                            )
                        ]
                    ]
                ]
                (
                [TermOrFormula
                    [Formula
                        [LambdaFormula
                            [LambdaAbstraction
                                λ
                                [Variable x]
                                .
                                [Formula
                                    [AtomicFormula
                                        [Predicate Restaurant]
                                        (
                                        [TermList
                                            [Term
                                                [Variable x]
                                            ]
                                        ]
                                        )
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
                )
            ]
        ]
    ]
]"""

        when:
        def l = FirstOrderLogic.parseLambda(input)

        then:
        l.toString() == canonicalInput

        and:
        l.normalizationString == [
                'λP.(λQ.(∀x(P(x)⇒Q(x))))(λx.(Restaurant(x)))',
                'λQ.(∀x(λx.(Restaurant(x))(x)⇒Q(x)))',
                'λQ.(∀x(Restaurant(x)⇒Q(x)))',
        ].join('\n')
    }

    def 'FOL "λQ.∀x(Restaurant(x)⇒Q(x))(λy.∃e(Closed(e)∧ClosedThing(e,y)))" parses as expected'() {

        given:
        def input = 'λQ.∀x(Restaurant(x)⇒Q(x))(λy.∃e(Closed(e)∧ClosedThing(e,y)))'
        def canonicalInput = 'λQ.(∀x(Restaurant(x)⇒Q(x)))(λy.(∃e(Closed(e)∧ClosedThing(e,y))))'

        when:
        def p = FirstOrderLogic.parseTree(input)

        then:
        p.prettyPrint() == """
[S
    [Formula
        [LambdaFormula
            [LambdaApplication
                [LambdaAbstraction
                    λ
                    [AbstractionVariable Q]
                    .
                    [Formula
                        [QuantifiedFormula
                            [Quantifier ∀]
                            [VariableList
                                [Variable x]
                            ]
                            [Formula
                                [ParentheticalFormula
                                    (
                                    [Formula
                                        [LogicFormula
                                            [Formula
                                                [AtomicFormula
                                                    [Predicate Restaurant]
                                                    (
                                                    [TermList
                                                        [Term
                                                            [Variable x]
                                                        ]
                                                    ]
                                                    )
                                                ]
                                            ]
                                            [Connective ⇒]
                                            [Formula
                                                [VariableApplication
                                                    [AbstractionVariable Q]
                                                    (
                                                    [TermOrFormula
                                                        [Term
                                                            [Variable x]
                                                        ]
                                                    ]
                                                    )
                                                ]
                                            ]
                                        ]
                                    ]
                                    )
                                ]
                            ]
                        ]
                    ]
                ]
                (
                [TermOrFormula
                    [Formula
                        [LambdaFormula
                            [LambdaAbstraction
                                λ
                                [Variable y]
                                .
                                [Formula
                                    [QuantifiedFormula
                                        [Quantifier ∃]
                                        [VariableList
                                            [Variable e]
                                        ]
                                        [Formula
                                            [ParentheticalFormula
                                                (
                                                [Formula
                                                    [LogicFormula
                                                        [Formula
                                                            [AtomicFormula
                                                                [Predicate Closed]
                                                                (
                                                                [TermList
                                                                    [Term
                                                                        [Variable e]
                                                                    ]
                                                                ]
                                                                )
                                                            ]
                                                        ]
                                                        [Connective ∧]
                                                        [Formula
                                                            [AtomicFormula
                                                                [Predicate ClosedThing]
                                                                (
                                                                [TermList
                                                                    [Term
                                                                        [Variable e]
                                                                    ]
                                                                    ,
                                                                    [TermList
                                                                        [Term
                                                                            [Variable y]
                                                                        ]
                                                                    ]
                                                                ]
                                                                )
                                                            ]
                                                        ]
                                                    ]
                                                ]
                                                )
                                            ]
                                        ]
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
                )
            ]
        ]
    ]
]"""

        when:
        def l = FirstOrderLogic.parseLambda(input)

        then:
        l.toString() == canonicalInput

        and:
        l.normalizationString == [
                'λQ.(∀x(Restaurant(x)⇒Q(x)))(λy.(∃e(Closed(e)∧ClosedThing(e,y))))',
                '∀x(Restaurant(x)⇒λy.(∃e(Closed(e)∧ClosedThing(e,y)))(x))',
                '∀x(Restaurant(x)⇒∃e(Closed(e)∧ClosedThing(e,x)))',
        ].join('\n')
    }

    def 'FOL "λQ.∀x(Restaurant(x)⇒Q(x))(λx.∃e(Closed(e)∧ClosedThing(e,x)))" also parses as expected'() {

        given:
        def input = 'λQ.∀x(Restaurant(x)⇒Q(x))(λx.∃e(Closed(e)∧ClosedThing(e,x)))'

        expect:
        FirstOrderLogic.parseLambda(input).normalizationString == [
                'λQ.(∀x(Restaurant(x)⇒Q(x)))(λx.(∃e(Closed(e)∧ClosedThing(e,x))))',
                '∀x(Restaurant(x)⇒λx.(∃e(Closed(e)∧ClosedThing(e,x)))(x))',
                '∀x(Restaurant(x)⇒∃e(Closed(e)∧ClosedThing(e,x)))',
        ].join('\n')
    }

    def 'FOL "λX.X(Maharani)(λx.∃e(Closed(e)∧ClosedThing(e,x)))" parses as expected'() {

        given:
        def input = 'λX.X(Maharani)(λx.∃e(Closed(e)∧ClosedThing(e,x)))'

        expect:
        FirstOrderLogic.parseLambda(input).normalizationString == [
                'λX.(X(Maharani))(λx.(∃e(Closed(e)∧ClosedThing(e,x))))',
                'λx.(∃e(Closed(e)∧ClosedThing(e,x)))(Maharani)',
                '∃e(Closed(e)∧ClosedThing(e,Maharani))',
        ].join('\n')
    }

    def 'FOL "λM.M(Matthew)(λW.(λz.W(λx.∃e(Opened(e)∧(Opener(e,z)∧Opened(e,x)))))(λP.(λQ.∃x(P(x)∧Q(x)))(λr.Restaurant(r))))" parses as expected'() {

        given:
        def properNoun = "λM.M(Matthew)"
        def verb = "λW.(λz.W(λx.∃e(Opened(e)∧(Opener(e,z)∧Opened(e,x)))))"
        def det = "λP.(λQ.∃x(P(x)∧Q(x)))"
        def noun = "λr.Restaurant(r)"
        def np = "$det($noun)"
        def vp = "$verb($np)"
        def input = "$properNoun($vp)"
        def input2 = 'λM.M(Matthew)(λW.(λz.W(λx.∃e(Opened(e)∧(Opener(e,z)∧Opened(e,x)))))(λP.(λQ.∃x(P(x)∧Q(x)))(λr.Restaurant(r))))'
        def canonicalInput = 'λM.(M(Matthew))(λW.(λz.(W(λx.(∃e(Opened(e)∧(Opener(e,z)∧Opened(e,x)))))))(λP.(λQ.(∃x(P(x)∧Q(x))))(λr.(Restaurant(r)))))'

        when:
        def l = FirstOrderLogic.parseLambda(input)

        then:
        input == input2
        l.toString() == canonicalInput
        l.normalizationString == [
                'λM.(M(Matthew))(λW.(λz.(W(λx.(∃e(Opened(e)∧(Opener(e,z)∧Opened(e,x)))))))(λP.(λQ.(∃x(P(x)∧Q(x))))(λr.(Restaurant(r)))))',
                'λW.(λz.(W(λx.(∃e(Opened(e)∧(Opener(e,z)∧Opened(e,x)))))))(λP.(λQ.(∃x(P(x)∧Q(x))))(λr.(Restaurant(r))))(Matthew)',
                'λz.(λP.(λQ.(∃x(P(x)∧Q(x))))(λr.(Restaurant(r)))(λy.(∃e(Opened(e)∧(Opener(e,z)∧Opened(e,y))))))(Matthew)',
                'λP.(λQ.(∃x(P(x)∧Q(x))))(λr.(Restaurant(r)))(λy.(∃e(Opened(e)∧(Opener(e,Matthew)∧Opened(e,y)))))',
                'λQ.(∃x(λr.(Restaurant(r))(x)∧Q(x)))(λy.(∃e(Opened(e)∧(Opener(e,Matthew)∧Opened(e,y)))))',
                '∃x(λr.(Restaurant(r))(x)∧λy.(∃e(Opened(e)∧(Opener(e,Matthew)∧Opened(e,y))))(x))',
                '∃x(Restaurant(x)∧∃e(Opened(e)∧(Opener(e,Matthew)∧Opened(e,x))))',
        ].join('\n')
    }

    @Unroll
    def 'FOL "#input" is #expected'() {

        when:
        FirstOrderLogic.parseTree(input)

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
        'λx.x(Maharani)(λx.∃e(Closed(e)∧ClosedThing(e,x)))'             || 'unparsable'
        'λM.M(Matthew)(λW.λz.W(λx.∃e(Opened(e)∧(Opener(e,z)∧Opened(e,x))))(λP.(λQ. ∃x(P(x)∧Q(x)))(λr.Restaurant(r))))'  || 'ambiguous input (2 parses)'
        'λM.M(Matthew)(λW.(λz.W(λx.∃e Opened(e)∧(Opener(e,z)∧Opened(e,x))))(λP.(λQ. ∃x(P(x)∧Q(x)))(λr.Restaurant(r))))' || 'ambiguous input (3 parses)'
        'λM.M(Matthew)(λW.(λz.W(λx.∃e(Opened(e)∧Opener(e,z)∧Opened(e,x))))(λP.(λQ. ∃x(P(x)∧Q(x)))(λr.Restaurant(r))))'  || 'ambiguous input (2 parses)'
        'λM.M(Matthew)(λW.λz.W(λx.∃e(Opened(e)∧Opener(e,z)∧Opened(e,x)))(λP.(λQ.(∃x P(x)∧Q(x)))(λr.Restaurant(r))))'    || 'ambiguous input (8 parses)'
    }

    static final FOL_DEF = """S -> Formula
Formula -> LambdaFormula
Formula -> QuantifiedFormula
Formula -> LogicFormula
Formula -> AtomicFormula
Formula -> VariableApplication
Formula -> ParentheticalFormula
ParentheticalFormula -> ( Formula )
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
