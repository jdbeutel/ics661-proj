package fol.lambda

import groovy.transform.EqualsAndHashCode
import fol.FirstOrderLogic

/**
 * LambdaApplication -> LambdaAbstraction ( TermOrFormula )
 */
@EqualsAndHashCode
class Application extends SingleTerm {
    SingleTerm abstractionTerm
    SingleTerm term

    SingleTerm alphaConversion(Variable from, Variable to) {
        new Application(abstractionTerm: abstractionTerm.alphaConversion(from, to), term: term.alphaConversion(from, to))
    }

    Application substitution(Variable v, SingleTerm e) {    // (M N)[x := P] ≡ (M[x := P]) (N[x := P])
        new Application(abstractionTerm: abstractionTerm.substitution(v, e), term: term.substitution(v, e))
    }

    Set<Variable> getFreeVariables() {
        abstractionTerm.freeVariables + term.freeVariables         // FV(M N) = FV(M) ∪ FV(N)
    }

    Set<Variable> getBoundVariables() {
        abstractionTerm.boundVariables + term.boundVariables
    }

    // Lambda-reduction (a.k.a. beta-reduction)
    TermList reduction() {
        switch (abstractionTerm) {

            case Abstraction:   // do the real reduction
                // This reduction's substitutions will be safe if no term.freeVariables are bound within abstraction.expr.
                // So, alpha-convert each colliding bound variable so that it is fresh (i.e., in neither term.freeVariables
                // nor that abstraction's freeVariables).  This alphaConversion does not disturb the results,
                // because the variable bound in an abstraction is like a local variable; any alphaConversions of it
                // will not be visible after the abstraction is reduced in an application.
                def freshened = abstractionTerm.expr.freshen(term.freeVariables)
                return freshened.substitute(abstractionTerm.boundVar, term)

            case Application:   // try to reduce abstractionTerm to an Abstraction
                def x = reduceToSingleTerm('abstractionTerm')
                def y = reduceToSingleTerm('term')
                return new TermList([new Application(abstractionTerm: x, term: y)])

            default:
                throw new IllegalStateException("abstractionTerm is $abstractionTerm")
        }
    }

    private SingleTerm reduceToSingleTerm(String property) {
        def t = this[property]
        def reduced = t.reduction().flatten()
        if (reduced.size() > 1) {
            throw new IllegalStateException("$property $t reduced to multiple terms $reduced")
        }
        (SingleTerm) reduced[0]
    }

    Application freshen(Collection<Variable> forbiddenVars) {
        new Application(abstractionTerm: abstractionTerm.freshen(forbiddenVars), term: term.freshen(forbiddenVars))
    }

    String toString() {
        "$abstractionTerm($term)"
    }
}
