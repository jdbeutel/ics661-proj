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

    Set<Variable> getFreeVariables() {
        []      // none
    }

    Set<Variable> getBoundVariables() {
        []      // none
    }

    String toString() {
        symbol
    }
}
