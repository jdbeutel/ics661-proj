import java.util.regex.Pattern
import groovy.transform.EqualsAndHashCode

/**
 * First-Order Logic with Lambda notation.
 * This supports λ-reductions (a.k.a. β-reductions),
 * but does not go so far as to perform logical inference or productions.
 */
class FirstOrderLogic {

    static final SYMBOLIC_CHARS = '¬∧∨⇒∀∃().,'
    static final LAMBDA = 'λ'

    static final GRAMMAR = new Grammar("""S -> Formula
        Formula -> LambdaFormula | QuantifiedFormula | LogicFormula | AtomicFormula | ( Formula )
        LambdaFormula -> LambdaAbstraction | LambdaApplication
        LambdaAbstraction -> λ Variable . Formula | λ AbstractionVariable . Formula
        LambdaApplication -> LambdaAbstraction ( TermOrFormula )
        QuantifiedFormula -> Quantifier VariableList Formula
        LogicFormula -> Formula Connective Formula | ¬ Formula
        AtomicFormula -> Predicate ( TermList ) | AbstractionVariable ( TermOrFormula )
        TermOrFormula -> Term | Formula
        VariableList -> Variable | Variable , VariableList
        TermList -> Term | Term , TermList
        Term -> Function ( TermList ) | Constant | Variable
        Connective -> ∧ | ∨ | ⇒
        Quantifier -> ∀ | ∃
        Constant -> VegetarianFood | Maharani | AyCaramba | Bacaro | Centro | Leaf
        Constant -> Speaker | TurkeySandwich | Desk | Lunch | FiveDollars | LotOfTime
        Constant -> Monday | Tuesday | Wednesday | Thursday | Friday | Saturday | Sunday
        Constant -> Yesterday | Today | Tomorrow | Now
        Constant -> NewYork | Boston | SanFrancisco
        Constant -> Matthew | Franco | Frasca
        Variable -> a | b | c | d | e | f | g | h | i | j | k | l | m
        Variable -> n | o | p | q | r | s | t | u | v | w | x | y | z
        AbstractionVariable -> A | B | C | D | E | F | G | H | I | J | K | L | M
        AbstractionVariable -> N | O | P | Q | R |     T | U | V | W | X | Y | Z
        Predicate -> Serves | Near | Restaurant | Have | VegetarianRestaurant
        Predicate -> Eating | Time | Eater | Eaten | Meal | Location
        Predicate -> Arriving | Arriver | Destination | EndPoint | Precedes
        Predicate -> Closed | ClosedThing | Opened | Opener
        Predicate -> Menu | Having | Haver | Had
        Function -> LocationOf | CuisineOf | IntervalOf | MemberOf """)

    static List<String> getVariablesNames() {
        GRAMMAR.rulesFor('Variable').collect { it.symbols[0] }
    }

    static List<String> getAbstractionVariablesNames() {
        GRAMMAR.rulesFor('AbstractionVariable').collect { it.symbols[0] }
    }

    static EarleyParser parse(String input) {
        Pattern lexer = ~("[${SYMBOLIC_CHARS}${LAMBDA}]|" + /\w+/)
        def result = new EarleyParser(input, GRAMMAR, lexer)
        def count = result.completedParses.size()
        switch (count) {
            case 0:
                throw new IllegalArgumentException("unparsable input $input\n chart: $result")
            case 1:
                return result
            default:
                def prettyParses = Parser.prettyPrint(result.completedParsesString)
                def detail = "chart: $result \n has multiple parses: \n $prettyParses"
                throw new IllegalArgumentException("ambiguous input ($count parses) $input\n $detail")
        }
    }
}

// extension of Lambda Calculus to allow arbitrary lists of symbols of First Order Logic
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

abstract class Term {

    abstract Set<Variable> getFreeVariables()

    abstract Set<Variable> getBoundVariables()

//    boolean isClosed() {
//        !freeVariables
//    }
}

abstract class SingleTerm extends Term {

    abstract SingleTerm alphaConversion(Variable from, Variable to)

    abstract SingleTerm substitution(Variable v, SingleTerm e)
}

// First Order Logic payload in the Lambda framework
@EqualsAndHashCode
class Symbol extends SingleTerm {
    String symbol                       // disregarding logic etc

    Symbol(String s) {
        symbol = s
    }

    Symbol alphaConversion(Variable from, Variable to) {
        this    // unchanged
    }

    Symbol substitution(Variable v, SingleTerm e) {
        this    // unchanged
    }

    Set<Variable> getFreeVariables() {
        []      // none
    }

    Set<Variable> getBoundVariables() {
        []      // none
    }

    String toString() {
        symbol
    }
}

// FOL or Lambda variable
@EqualsAndHashCode  // using name for identity
class Variable extends SingleTerm {
    String name

    Variable(String s) {
        name = s
    }

    Variable alphaConversion(Variable from, Variable to) {
        (Variable) substitution(from, to)
    }

    SingleTerm substitution(Variable v, SingleTerm e) {
        if (this == v) {        // x[x := N] ≡ N
            return e            // substituted!
        } else {
            assert name != v.name
            return this        // y[x := N] ≡ y, if x ≠ y
        }
    }

    Set<Variable> getFreeVariables() {
        [this]                  // FV(x) = {x}, where x is a variable
    }

    Set<Variable> getBoundVariables() {
        []      // none
    }

    String toString() {
        name
    }
}

// LambdaAbstraction -> λ Variable . Formula | λ AbstractionVariable . Formula
@EqualsAndHashCode
class Abstraction extends SingleTerm {
    Variable boundVar
    TermList expr

    Abstraction alphaConversion(Variable from, Variable to) {
        new Abstraction(boundVar: from == boundVar ? to : boundVar, expr: expr.alphaConversion(from, to))
    }

    Abstraction substitution(Variable v, SingleTerm e) {
        if (v == boundVar) {
            return this     // (λx.M)[x := N] ≡ λx.M    (stop recursion and preserve binding)
        } else {            // (λy.M)[x := N] ≡ λy.(M[x := N]), if x ≠ y, provided y ∉ FV(N)
            assert !(boundVar in e.freeVariables) : 'needs alphaConversion'
            return new Abstraction(boundVar: boundVar, expr: expr.substitute(v, e))
        }
    }

    Set<Variable> getFreeVariables() {
        expr.freeVariables - boundVar            // FV(λx.M) = FV(M) - {x}
    }

    Set<Variable> getBoundVariables() {
        expr.boundVariables + boundVar
    }

    String toString() {
        "λ${boundVar}.($expr)"
    }
}

// LambdaApplication -> LambdaAbstraction ( TermOrFormula )
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

// AtomicFormula -> AbstractionVariable ( TermOrFormula )
@EqualsAndHashCode
class VariableApplication extends SingleTerm {
    Variable boundAbstractionVar
    SingleTerm term

    VariableApplication(Variable v, SingleTerm t) {
        boundAbstractionVar = v
        term = t
    }

    VariableApplication alphaConversion(Variable from, Variable to) {
        def v = boundAbstractionVar == from ? to : boundAbstractionVar
        new VariableApplication(v, term.alphaConversion(from, to))
    }

    SingleTerm substitution(Variable v, SingleTerm e) {
        if (v == boundAbstractionVar) {
            assert e instanceof Abstraction : "substituted non-Abstraction for $v: $e"
            return new Application(abstraction: e, term: term)
        } else {
            return new VariableApplication(boundAbstractionVar, term.substitution(v, e))
        }
    }

    Set<Variable> getFreeVariables() {
        boundAbstractionVar.freeVariables + term.freeVariables         // FV(M N) = FV(M) ∪ FV(N)
    }

    Set<Variable> getBoundVariables() {
        boundAbstractionVar.boundVariables + term.boundVariables
    }

    String toString() {
        " $boundAbstractionVar($term)"
    }
}
