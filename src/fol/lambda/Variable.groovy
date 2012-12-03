package fol.lambda

import groovy.transform.EqualsAndHashCode

/**
 * FOL or Lambda variable
 */
@EqualsAndHashCode  // using name for identity
class Variable extends SingleTerm {
    String name

    Variable(String s) {
        name = s
    }

    SingleTerm substitution(Variable v, SingleTerm e) {
        if (this == v) {        // x[x := N] ≡ N
            return e            // substituted!
        } else {
            assert name != v.name
            return this        // y[x := N] ≡ y, if x ≠ y
        }
    }

    TermList reduction() {
        new TermList([this])    // cannot reduce
    }

    Variable freshen(Collection<Variable> forbiddenVars) {
        this    // cannot freshen
    }

    Set<Variable> getFreeVariables() {
        [this]                  // FV(x) = {x}, where x is a variable
    }

    String toString() {
        name
    }
}
