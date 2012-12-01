package fol.lambda

abstract class Term {

    abstract Set<Variable> getFreeVariables()

    abstract Set<Variable> getBoundVariables()

//    boolean isClosed() {
//        !freeVariables
//    }
}
