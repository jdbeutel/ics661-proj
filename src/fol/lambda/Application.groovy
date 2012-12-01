package fol.lambda

import groovy.transform.EqualsAndHashCode
import fol.FirstOrderLogic

/**
 * LambdaApplication -> LambdaAbstraction ( TermOrFormula )
 */
@EqualsAndHashCode
class Application extends SingleTerm {
    Abstraction abstraction
    SingleTerm term

    SingleTerm alphaConversion(Variable from, Variable to) {
        new Application(abstraction: abstraction.alphaConversion(from, to), term: term.alphaConversion(from, to))
    }

    Application substitution(Variable v, SingleTerm e) {    // (M N)[x := P] ≡ (M[x := P]) (N[x := P])
        new Application(abstraction: abstraction.substitution(v, e), term: term.substitution(v, e))
    }

    Set<Variable> getFreeVariables() {
        abstraction.freeVariables + term.freeVariables         // FV(M N) = FV(M) ∪ FV(N)
    }

    Set<Variable> getBoundVariables() {
        abstraction.boundVariables + term.boundVariables
    }

    // Lambda-reduction (a.k.a. beta-reduction)
    TermList reduction() {
        def e = term
        // The Lambda Calculus rules would be satisfied if none of e.freeVariables are bound within the abstraction.
        // However, the abstraction.freeVariables may be significant to FOL, so we'll alpha-convert those too.
        def allVars = abstraction.boundVariables + abstraction.freeVariables
        def collisions = e.freeVariables.intersect allVars
        for (w in collisions) {
            def x = findAvailable(w, e, allVars)
            e = e.alphaConversion(w, x)
        }
        abstraction.expr.substitute(abstraction.boundVar, e)
    }

    private Variable findAvailable(Variable w, SingleTerm e, Collection<Variable> allVars) {
        assert w in allVars && w in e.freeVariables
        allVars += e.freeVariables + e.boundVariables   // gains previous conversion on each call
        for (n in candidateNames(w)) {
            Variable v = new Variable(n)
            if (!(v in allVars)) {
                return v
            }
        }
        throw new IllegalStateException("no variable names available for $w")
    }

    private List<String> candidateNames(Variable w) {
        List varNames = FirstOrderLogic.variablesNames
        def idx = varNames.indexOf(w.name)
        if (idx == -1) {
            varNames = FirstOrderLogic.abstractionVariablesNames
            assert w.name in varNames
            idx = varNames.indexOf(w.name)
            assert idx != -1
        }
        if (++idx < varNames.size()) {  // order by next name to try, wrapping
            varNames = varNames.subList(idx, varNames.size()) + varNames.subList(0, idx - 1)
        }
        varNames
    }

    String toString() {
        "$abstraction($term)"
    }
}
