package fol.lambda

abstract class SingleTerm extends Term {

    abstract SingleTerm alphaConversion(Variable from, Variable to)

    abstract SingleTerm substitution(Variable v, SingleTerm e)
}
