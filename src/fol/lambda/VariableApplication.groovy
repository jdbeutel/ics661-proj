package fol.lambda

import groovy.transform.EqualsAndHashCode

/**
 * AtomicFormula -> AbstractionVariable ( TermOrFormula )
 */
@EqualsAndHashCode
class VariableApplication extends SingleTerm {
    Variable boundAbstractionVar
    SingleTerm term

    VariableApplication(Variable v, SingleTerm t) {
        boundAbstractionVar = v
        term = t
    }

    VariableApplication alphaConversion(Variable from, Variable to) {
        def v = boundAbstractionVar == from ? to : boundAbstractionVar
        new VariableApplication(v, term.alphaConversion(from, to))
    }

    SingleTerm substitution(Variable v, SingleTerm e) {
        if (v == boundAbstractionVar) {
            assert e instanceof Abstraction : "substituted non-Abstraction for $v: $e"
            return new Application(abstraction: e, term: term)
        } else {
            return new VariableApplication(boundAbstractionVar, term.substitution(v, e))
        }
    }

    Set<Variable> getFreeVariables() {
        boundAbstractionVar.freeVariables + term.freeVariables         // FV(M N) = FV(M) ∪ FV(N)
    }

    Set<Variable> getBoundVariables() {
        boundAbstractionVar.boundVariables + term.boundVariables
    }

    String toString() {
        " $boundAbstractionVar($term)"
    }
}
