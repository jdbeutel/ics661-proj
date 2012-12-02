package fol.lambda

import groovy.transform.EqualsAndHashCode

/**
 * LambdaAbstraction -> λ Variable . Formula | λ AbstractionVariable . Formula
 */
@EqualsAndHashCode
class Abstraction extends SingleTerm {
    Variable boundVar
    TermList expr

    Abstraction alphaConversion(Variable from, Variable to) {
        new Abstraction(boundVar: from == boundVar ? to : boundVar, expr: expr.alphaConversion(from, to))
    }

    Abstraction substitution(Variable v, SingleTerm e) {
        if (v == boundVar) {
            return this     // (λx.M)[x := N] ≡ λx.M    (stop recursion and preserve binding)
        } else {            // (λy.M)[x := N] ≡ λy.(M[x := N]), if x ≠ y, provided y ∉ FV(N)
            assert !(boundVar in e.freeVariables) : 'needs alphaConversion'
            return new Abstraction(boundVar: boundVar, expr: expr.substitute(v, e))
        }
    }

    Set<Variable> getFreeVariables() {
        expr.freeVariables - boundVar            // FV(λx.M) = FV(M) - {x}
    }

    Set<Variable> getBoundVariables() {
        expr.boundVariables + boundVar
    }

    String toString() {
        def exprStr = expr.toString()   // hack: to get GString to use List's overridden toString()
        "λ${boundVar}.($exprStr)"
    }
}
