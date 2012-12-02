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
        // This reduction's substitutions will be safe if no term.freeVariables are bound within abstraction.expr.
        // So, alpha-convert each colliding bound variable so that it is fresh (i.e., in neither term.freeVariables
        // nor that abstraction's freeVariables).  This alphaConversion does not disturb the results,
        // because the variable bound in an abstraction is like a local variable; any alphaConversions of it
        // will not be visible after the abstraction is reduced in an application.
        def freshened = abstraction.expr.freshen(term.freeVariables)
        freshened.substitute(abstraction.boundVar, term)
    }

    Application freshen(Collection<Variable> forbiddenVars) {
        new Application(abstraction: abstraction.freshen(forbiddenVars), term: term.freshen(forbiddenVars))
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
