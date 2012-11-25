import java.util.regex.Pattern

/**
 * First-Order Logic with Lambda notation.
 * This supports λ-reductions (a.k.a. β-reductions),
 * but does not go so far as to perform logical inference or production.
 */
class FirstOrderLogic {

    static final SYMBOLIC_CHARS = '¬∧∨⇒∀∃().,'
    static final LAMBDA = 'λ'

    static final GRAMMAR = new Grammar("""S -> Formula
        Formula -> AtomicFormula | Formula Connective Formula | Quantifier VariableList Formula | ¬ Formula | ( Formula )
        Formula -> λ Variable . Formula
        VariableList -> Variable | Variable , VariableList
        TermList -> Term | Term , TermList
        AtomicFormula -> Predicate ( TermList )
        Term -> Function ( TermList ) | Constant | Variable
        Connective -> ∧ | ∨ | ⇒
        Quantifier -> ∀ | ∃
        Constant -> VegetarianFood | Maharani | AyCaramba | Bacaro | Centro | Leaf
        Constant -> Speaker | TurkeySandwich | Desk | Lunch | FiveDollars | LotOfTime
        Constant -> Monday | Tuesday | Wednesday | Thursday | Friday | Saturday | Sunday
        Constant -> Yesterday | Today | Tomorrow | Now
        Constant -> NewYork | Boston | SanFrancisco
        Constant -> Matthew | Franco | Frasca
        Variable -> a | b | c | d | e | f | g | h | i | j | k | l | m | n | o | p | q | r | s | t | u | v | w | x | y | z
        Variable -> A | B | C | D | E | F | G | H | I | J | K | L | M | N | O | P | Q | R |     T | U | V | W | X | Y | Z
        Predicate -> Serves | Near | Restaurant | Have | VegetarianRestaurant
        Predicate -> Eating | Time | Eater | Eaten | Meal | Location
        Predicate -> Arriving | Arriver | Destination | EndPoint | Precedes
        Predicate -> Closed | ClosedThing | Opened | Opener
        Predicate -> Menu | Having | Haver | Had
        Function -> LocationOf | CuisineOf | IntervalOf | MemberOf """)

    static Parser parse(String input) {
        Pattern lexer = ~("[${SYMBOLIC_CHARS}${LAMBDA}]|" + /\w+/)
        new Parser(input, GRAMMAR, lexer)
    }
}
