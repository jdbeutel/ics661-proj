package fol.lambda

abstract class SingleTerm implements Term {

    abstract SingleTerm alphaConversion(Variable from, Variable to)

    abstract SingleTerm substitution(Variable v, SingleTerm e)

    abstract TermList reduction()

    abstract SingleTerm freshen(Collection<Variable> forbiddenVars)
}
