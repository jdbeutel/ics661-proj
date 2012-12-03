package fol.lambda

import groovy.transform.EqualsAndHashCode

/**
 * First Order Logic payload in the Lambda framework
 */
@EqualsAndHashCode
class Symbol extends SingleTerm {
    String symbol                       // disregarding logic etc

    Symbol(String s) {
        symbol = s
    }

    Symbol alphaConversion(Variable from, Variable to) {
        this    // unchanged
    }

    Symbol substitution(Variable v, SingleTerm e) {
        this    // unchanged
    }

    TermList reduction() {
        new TermList([this])    // cannot reduce
    }

    Symbol freshen(Collection<Variable> forbiddenVars) {
        this    // cannot freshen
    }

    Set<Variable> getFreeVariables() {
        []      // none
    }

    String toString() {
        symbol
    }
}
