package fol.lambda

import fol.FirstOrderLogic

/**
 * Extension of Lambda Calculus to allow arbitrary lists of symbols of First Order Logic.
 * This uses ArrayList's equals() and hashCode(), not the Groovy @EqualsAndHashCode,
 * because the latter seems to rely on the subclass' (non-existent) properties and ignore the superclass'.
 */
class TermList extends ArrayList<SingleTerm> {

    TermList(Collection terms) {
        super(terms.collect { (SingleTerm) it instanceof String ? new Symbol(it) : it })

        if (size() == 3 && this[0] == new Symbol('(') && this[2] == new Symbol(')')) {
            // hack to remove certain extra parenthesis added to disambiguate parse
            remove(2)
            remove(0)
        }
    }

    TermList substitute(Variable v, SingleTerm e) {     // (M N)[x := P] ≡ (M[x := P]) (N[x := P])
        new TermList(this.collect { it.substitution(v, e) }.flatten())
    }

    Set<Variable> getFreeVariables() {
        // NB: cannot use this.freeVariables.flatten(); GPaths do not seem to work properly inside the List itself
        this.collect { it.freeVariables }.flatten() as Set<Variable>     // FV(M N) = FV(M) ∪ FV(N)
    }

    TermList reduction() {
        new TermList(this.collect { it.reduction() }.flatten())
    }

    TermList freshen(Collection<Variable> forbiddenVars) {
        new TermList(this.collect { it.freshen(forbiddenVars) })
    }

    List<TermList> getNormalization() {
        def result = [this]
        while (true) {
            def last = result[-1]
            def next = last.reduction()
            if (next == last) {     // normalized
                return result
            }
            if (next in result) {
                throw new IllegalStateException('' + this + ' does not normalize; ' + result + ' loops to ' + next)
            }
            result << next
        }
    }

    String getNormalizationString() {
        // Cannot use just Groovy's join(), because for the component Lists,
        // join() would use its own InvokerHelper.formatList() instead of this class' toString().
        normalization*.toString().join('\n')
    }

    @Override
    public String toString() {
        def result = ''
        def prev = null
        for (term in this) {
            if (needsSpaceBetween(prev, term)) {
                result += ' '
            }
            result += term
            prev = term
        }
        result
    }

    private boolean needsSpaceBetween(Term t, Term u) {
        t && FirstOrderLogic.LEXER.matcher("${t.toString()[-1]}${u.toString()[0]}").size() != 2
    }
}
