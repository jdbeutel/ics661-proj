package fol.lambda

import groovy.transform.EqualsAndHashCode
import fol.FirstOrderLogic

/**
 * Extension of Lambda Calculus to allow arbitrary lists of symbols of First Order Logic.
 */
@EqualsAndHashCode
class TermList extends ArrayList<SingleTerm> {

    TermList(Collection terms) {
        super(terms.collect { (SingleTerm) it instanceof String ? new Symbol(it) : it })
    }

    TermList alphaConversion(Variable from, Variable to) {
        new TermList(this.collect { it.alphaConversion(from, to) })
    }

    TermList substitute(Variable v, SingleTerm e) {     // (M N)[x := P] ≡ (M[x := P]) (N[x := P])
        new TermList(this.collect { it.substitution(v, e) }.flatten())
    }

    Set<Variable> getFreeVariables() {
        // NB: cannot use this.freeVariables.flatten(); GPaths do not seem to work properly inside the List itself
        this.collect { it.freeVariables }.flatten() as Set<Variable>     // FV(M N) = FV(M) ∪ FV(N)
    }

    Set<Variable> getBoundVariables() {
        // NB: cannot use this.boundVariables.flatten(); GPaths do not seem to work properly inside the List itself
        this.collect { it.boundVariables }.flatten() as Set<Variable>
    }

    TermList reduction() {
        new TermList(this.collect { it.reduction() }.flatten())
    }

    TermList freshen(Collection<Variable> forbiddenVars) {
        new TermList(this.collect { it.freshen(forbiddenVars) })
    }

    @Override
    public String toString() {
        def result = ''
        def prev = null
        for (term in this) {
            if (needsSpace(prev, term)) {
                result += ' '
            }
            result += term
            prev = term
        }
        result
    }

    private boolean needsSpace(Term t, Term u) {
        t && FirstOrderLogic.LEXER.matcher("${t.toString()[-1]}${u.toString()[0]}").size() != 2
    }
}
