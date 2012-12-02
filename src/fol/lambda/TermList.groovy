package fol.lambda

import groovy.transform.EqualsAndHashCode

/**
 * Extension of Lambda Calculus to allow arbitrary lists of symbols of First Order Logic.
 */
@EqualsAndHashCode
class TermList extends ArrayList<SingleTerm> {

    TermList(Collection terms) {
        super((List<SingleTerm>) terms.collect { it instanceof String ? new Symbol(it) : it })
    }

    TermList alphaConversion(Variable from, Variable to) {
        new TermList(this.collect { it.alphaConversion(from, to) })
    }

    TermList substitute(Variable v, SingleTerm e) {     // (M N)[x := P] ≡ (M[x := P]) (N[x := P])
        new TermList(this.collect { it.substitution(v, e) }.flatten())
    }

    Set<Variable> getFreeVariables() {
        this.collect { it.freeVariables}.flatten() as Set<Variable>     // FV(M N) = FV(M) ∪ FV(N)
    }

    Set<Variable> getBoundVariables() {
        // NB: cannot use this.boundVariables.flatten(); GPaths do not seem to work properly inside the List itself
        this.collect { it.boundVariables}.flatten() as Set<Variable>
    }

//    TermList reduction() {
//
//    }

    @Override
    public String toString() {
        this.join('')
    }
}
