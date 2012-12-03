package fol

import java.util.regex.Pattern

import parser.Parser
import grammar.Grammar
import parser.earley.*
import fol.lambda.*

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
        Formula -> LambdaFormula | QuantifiedFormula | LogicFormula | AtomicFormula | VariableApplication | ( Formula )
        LambdaFormula -> LambdaAbstraction | LambdaApplication
        LambdaAbstraction -> λ Variable . Formula | λ AbstractionVariable . Formula
        LambdaApplication -> LambdaAbstraction ( TermOrFormula )
        QuantifiedFormula -> Quantifier VariableList Formula
        LogicFormula -> Formula Connective Formula | ¬ Formula
        AtomicFormula -> Predicate ( TermList )
        VariableApplication -> AbstractionVariable ( TermOrFormula )
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

    static TermList parseLambda(String input) {
        def ep = parse(input)
        (TermList) buildLambda((EarleyState) ep.completedParses[0])
    }

    private static buildLambda(EarleyState folParse) {
        assert folParse.complete
        def symbols = folParse.rule.symbols
        def defaultTranslation = {
            def results = []
            for (i in 0..<symbols.size()) {
                def c = folParse.components[i]
                results << (c ? buildLambda(c) : new Symbol(symbols[i]))
            }
            new TermList(results.flatten())
        }
        def translations = [:].withDefault {defaultTranslation}
        translations << [   // preserving default
                'LambdaAbstraction':    {new Abstraction(boundVar: (Variable) buildLambda(folParse.components[1]), expr: (TermList) buildLambda(folParse.components[3]))},
                'Variable':    {new Variable(symbols[0])},
                'VariableApplication':  {def terms = buildLambda(folParse.components[2]); assert terms.size() == 1; new VariableApplication((Variable) buildLambda(folParse.components[0]), (SingleTerm) terms[0])},
        ]
        def handler = (Closure) translations[folParse.rule.nonTerminal]
        handler()
    }
}
