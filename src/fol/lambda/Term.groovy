package fol.lambda

interface Term {

    Set<Variable> getFreeVariables()

    Set<Variable> getBoundVariables()
}
