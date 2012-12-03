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
            if (e instanceof Variable) {    // alpha-conversion of this boundAbstractionVar
                return new VariableApplication(e, term.substitution(v, e))
            }
            assert e.class in [Abstraction, Application, VariableApplication] : "substituted unsuitable for $v: $e"
            return new Application(abstractionTerm: e, term: term.substitution(v, e))
        } else {
            return new VariableApplication(boundAbstractionVar, term.substitution(v, e))
        }
    }

    TermList reduction() {
        new TermList([this])        // cannot reduce; don't know yet what alphaConversions will be needed
    }

    VariableApplication freshen(Collection<Variable> forbiddenVars) {
        new VariableApplication(boundAbstractionVar.freshen(forbiddenVars), term.freshen(forbiddenVars))
    }

    Set<Variable> getFreeVariables() {
        boundAbstractionVar.freeVariables + term.freeVariables         // FV(M N) = FV(M) ∪ FV(N)
    }

    Set<Variable> getBoundVariables() {
        boundAbstractionVar.boundVariables + term.boundVariables
    }

    String toString() {
        "$boundAbstractionVar($term)"
    }
}
