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

    Variable alphaConversion(Variable from, Variable to) {
        (Variable) substitution(from, to)
    }

    SingleTerm substitution(Variable v, SingleTerm e) {
        if (this == v) {        // x[x := N] ≡ N
            return e            // substituted!
        } else {
            assert name != v.name
            return this        // y[x := N] ≡ y, if x ≠ y
        }
    }

    Set<Variable> getFreeVariables() {
        [this]                  // FV(x) = {x}, where x is a variable
    }

    Set<Variable> getBoundVariables() {
        []      // none
    }

    String toString() {
        name
    }
}
