package fol.lambda

import groovy.transform.EqualsAndHashCode
import fol.FirstOrderLogic

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
            assert !(boundVar in e.freeVariables) : 'needed alphaConversion'
            return new Abstraction(boundVar: boundVar, expr: expr.substitute(v, e))
        }
    }

    TermList reduction() {
        new TermList([new Abstraction(boundVar: boundVar, expr: expr.reduction())])
    }

    // This reduction's substitutions will be safe if no term.freeVariables are bound within abstraction.expr.
    // So, alpha-convert each colliding bound variable so that it is fresh (i.e., in neither term.freeVariables
    // nor that abstraction's freeVariables).  This alphaConversion does not disturb the results,
    // because the variable bound in an abstraction is like a local variable; any alphaConversions of it
    // will not be visible after the abstraction is reduced in an application.
    Abstraction freshen(Collection<Variable> forbiddenVars) {
        if (boundVar in forbiddenVars) {
            // alpha-convert this boundVar, and freshen expr
            def v = findFresh(forbiddenVars)
            return new Abstraction(boundVar: v, expr: expr.substitute(boundVar, v).freshen(forbiddenVars))
        } else {
            return new Abstraction(boundVar: boundVar, expr: expr.freshen(forbiddenVars))
        }
    }

    private Variable findFresh(Collection<Variable> forbiddenVars) {
        forbiddenVars += freeVariables
        for (n in candidateNames()) {
            Variable w = new Variable(n)
            if (!(w in forbiddenVars)) {
                return w
            }
        }
        throw new IllegalStateException("no fresh variable names available for $boundVar")
    }

    private List<String> candidateNames() {
        def name = boundVar.name
        List varNames = FirstOrderLogic.variablesNames
        def idx = varNames.indexOf(name)
        if (idx == -1) {
            varNames = FirstOrderLogic.abstractionVariablesNames
            assert name in varNames   // had to be one type or the other
            idx = varNames.indexOf(name)
            assert idx != -1
        }
        if (++idx < varNames.size()) {  // order by next name to try, and wrap
            varNames = varNames.subList(idx, varNames.size()) + varNames.subList(0, idx - 1)
        }
        varNames
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
