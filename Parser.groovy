import java.util.regex.Pattern

/**
 * Code common between the parser implementations.
 */
abstract class Parser {

    List<String> words
    Grammar grammar

    /**
     * Constructs a CkyParser, parsing the given line with the given Grammar.
     *
     * @param line the line of words to parse (i.e., a sentence)
     * @param g the grammar to use for the parse
     * @param lexer (optional) a regex identifying each separate word (i.e., token) in the line
     */
    Parser(String line, Grammar g, Pattern lexer = ~/\w+/) {
        grammar = g
        words = lexer.matcher(line).collect {it}
    }

    /**
     * @return a list of roots of all accepted, full parse trees, or the empty list if none are accepted
     */
    abstract List getCompletedParses()

    /**
     * Renders all accepted, full parses as required for assignment 4.
     *
     * @return a rendering of all possible parses, or "not S" if none are accepted
     */
    String getCompletedParsesString() {
        completedParses?.join(';') ?: "not ${grammar.startSymbol}"
    }

    /**
     * Renders a completedParsesString in an easily readable and comparable format, for testing.
     * The completedParses implementation's toString() needs to provide a compatible input format.
     *
     * @param s a completedParsesString
     * @return the given parse formatted on multiple lines with indents
     */
    static String prettyPrint(String s) {
        def result = ''
        def level = -1
        while (s) {
            char c = s[0]
            s = s.substring(1)
            switch (c) {
                case '[':
                    level++
                    if (level) {
                        result += '\n' + ' ' * (level*4)
                    }
                    result += '['
                    break
                case ']':
                    level--
                    result += ']'
                    if (s.startsWith(' {')) {
                        result += '\n' + ' ' * (level*4)
                    }
                    break
                case ';':
                    if (level == -1) {
                        result += '\n;\n'
                    } else {
                        result += ';'
                    }
                    break
                default:
                    result += c
                    break
            }
        }
        result
    }
}
