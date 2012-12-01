package fol.lambda

import groovy.transform.EqualsAndHashCode

/**
 * Extension of Lambda Calculus to allow arbitrary lists of symbols of First Order Logic.
 */
@EqualsAndHashCode
class TermList extends Term {
    SingleTerm head
    TermList tail

    TermList() {}

    TermList(List terms) {
        def h = terms[0]
        head = (SingleTerm) h instanceof String ? new Symbol(h) : h
        if (terms.tail()) {
            tail = new TermList(terms.tail())
        }
    }

    TermList alphaConversion(Variable from, Variable to) {
        new TermList(head: head.alphaConversion(from, to), tail: tail?.alphaConversion(from, to))
    }

    TermList substitute(Variable v, SingleTerm e) {     // (M N)[x := P] ≡ (M[x := P]) (N[x := P])
        new TermList(head: head.substitution(v, e), tail: tail?.substitute(v, e))
    }

    Set<Variable> getFreeVariables() {
        head.freeVariables + (tail?.freeVariables ?: [])         // FV(M N) = FV(M) ∪ FV(N)
    }

    Set<Variable> getBoundVariables() {
        head.boundVariables + (tail?.boundVariables ?: [])
    }

    SingleTerm getAt(int i) {
        if (i == 0) {
            head
        } else {
            if (!tail) {
                throw new IndexOutOfBoundsException("ran out of tail")
            }
            tail[i-1]
        }
    }

    String toString() {
        "$head${ tail ?: ''}"
    }
}
