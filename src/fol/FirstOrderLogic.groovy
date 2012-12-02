package fol

import java.util.regex.Pattern

import parser.Parser
import grammar.Grammar
import parser.earley.EarleyParser

/**
 * First-Order Logic with Lambda notation.
 * This supports λ-reductions (a.k.a. β-reductions),
 * but does not go so far as to perform logical inference or productions.
 */
class FirstOrderLogic {

    static final SYMBOLIC_CHARS = '¬∧∨⇒∀∃().,'
    static final LAMBDA = 'λ'
    static final Pattern LEXER = ~("[${SYMBOLIC_CHARS}${LAMBDA}]|" + /\w+/)

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
        def result = new EarleyParser(input, GRAMMAR, LEXER)
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
