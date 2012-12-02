package fol.lambda

import spock.lang.Specification

/**
 * Test specification of Lambda Calculus for First Order Logic.
 */
class LambdaSpec extends Specification {

    def 'basic expression containing abstraction'() {

        given:
        def exp = new TermList([
                new Abstraction(
                        boundVar: new Variable('y'),
                        expr: new TermList(['Near', '(', 'Bacaro', ',', new Variable('y'), ')'])
                )
        ])

        expect:
        exp[0].boundVar == new Variable('y')
        exp[0].expr[4] == new Variable('y')

        and:
        exp.toString() == 'λy.(Near(Bacaro,y))'
    }

    def 'basic reduction'() {

        given:
        def app = new Application(
                abstraction: new Abstraction(
                        boundVar: new Variable('y'),
                        expr: new TermList(['Near', '(', 'Bacaro', ',', new Variable('y'), ')'])
                ),
                term: new Symbol('Centro')
        )

        expect:
        app.abstraction.boundVar == new Variable('y')
        app.abstraction.expr[4] == new Variable('y')
        app.term.symbol == 'Centro'

        and:
        app.toString() == 'λy.(Near(Bacaro,y))(Centro)'
        app.reduction().toString() == 'Near(Bacaro,Centro)'
        app.reduction() == new TermList(['Near', '(', 'Bacaro', ',', 'Centro', ')'])
    }

    def 'basic alpha-conversion'() {

        given:
        def app = new Application(
                abstraction: new Abstraction(
                        boundVar: new Variable('x'),
                        expr: new TermList([
                                new Abstraction(
                                        boundVar: new Variable('y'),
                                        expr: new TermList([new Variable('x')])
                                )
                        ])
                ),
                term: new Variable('y')
        )

        expect: 'substitution into the abstraction triggers alpha-conversion of the bound variable'
        app.toString() == 'λx.(λy.(x))(y)'
        app.reduction().toString() == 'λz.(y)'
        app.reduction() == new TermList([
                new Abstraction(
                        boundVar: new Variable('z'),
                        expr: new TermList([new Variable('y')])
                )
        ])
    }

    def 'variable application reduction'() {

        given:
        def app = new Application(
                abstraction: new Abstraction(
                        boundVar: new Variable('P'),
                        expr: new TermList([new Abstraction(
                                boundVar: new Variable('Q'),
                                expr: new TermList([
                                        '∀',
                                        new Variable('x'),
                                        new VariableApplication(new Variable('P'), new Variable('x')),
                                        '⇒',
                                        new VariableApplication(new Variable('Q'), new Variable('x')),
                                ])
                        )])
                ),
                term: new Abstraction(
                        boundVar: new Variable('x'),
                        expr: new TermList(['Restaurant', '(', new Variable('x'), ')'])
                )
        )

        expect:
        app.toString() == 'λP.(λQ.(∀x P(x)⇒Q(x)))(λx.(Restaurant(x)))'
        app.reduction().toString() == 'λQ.(∀xλx.(Restaurant(x))(x)⇒Q(x))'
        app.reduction() == new TermList([new Abstraction(
                boundVar: new Variable('Q'),
                expr: new TermList([
                        '∀',
                        new Variable('x'),
                        new Application(
                                abstraction: new Abstraction(
                                        boundVar: new Variable('x'),    // term's bound vars don't need alpha-conversion
                                        expr: new TermList(['Restaurant', '(', new Variable('x'), ')'])
                                ),
                                term: new Variable('x')
                        ),
                        '⇒',
                        new VariableApplication(new Variable('Q'), new Variable('x')),
                ])
        )])

        and: 'second level reduction'
        app.reduction().reduction().toString() == 'λQ.(∀x Restaurant(x)⇒Q(x))'
        app.reduction().reduction() == new TermList([new Abstraction(
                boundVar: new Variable('Q'),
                expr: new TermList([
                        '∀',
                        new Variable('x'),
                        'Restaurant',
                        '(',
                        new Variable('x'),
                        ')',
                        '⇒',
                        new VariableApplication(new Variable('Q'), new Variable('x')),
                ])
        )])
    }

    def "groovy sublist indexes"() {

        given:
        def x = ['a', 'b', 'c']

        expect:
        x[2..-1] + x[0..1] == ['c', 'a', 'b']
        x.subList(0, 0) == []


        when:
        x[3..-1]

        then:
        thrown(IndexOutOfBoundsException)
    }
}
