package parser

import java.util.regex.Pattern
import grammar.Grammar

/**
 * Code common between the parser implementations.
 */
abstract class Parser {

    List<String> words
    Grammar grammar

    /**
     * Constructs a Parser, parsing the given line with the given Grammar.
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
    abstract List<Parse> getCompletedParses()

    /**
     * Renders all accepted, full parses as required for assignment 4.
     *
     * @return a rendering of all possible parses, or "not S" if none are accepted
     */
    String getCompletedParsesString() {
        completedParses?.join(';') ?: "not ${grammar.startSymbol}"
    }

    /**
     * Renders all accepted, full parses into easy-to-read indentation.
     *
     * @return a rendering of all possible parses, or "not S" if none are accepted
     */
    String getPrettyCompletedParsesString() {
        // using Parser.prettyPrint(), not the static prettyPrint() below.
        completedParses*.prettyPrint(0).join('\n;') ?: "not ${grammar.startSymbol}"
    }

    /**
     * Renders a completedParsesString in an easily readable and comparable format, for testing.
     * The completedParses implementation's toString() needs to provide a compatible input format.
     *
     * @param s a completedParsesString
     * @return the given parse formatted on multiple lines with indents
     */
    static String prettyPrint(String s) {
        def result = new StringBuilder()
        def level = -1
        while (s) {
            char c = s[0]
            s = s.substring(1)
            switch (c) {
                case '[':
                    level++
                    if (level) {
                        indent(result, level)
                    }
                    result << '['
                    break
                case ']':
                    level--
                    result << ']'
                    if (s.startsWith(' {') || s.startsWith(' (')) {
                        indent(result, level)
                    }
                    break
                case ';':
                    if (level == -1) {
                        result << '\n;\n'
                    } else {
                        result << ';'
                    }
                    break
                default:
                    result << c
                    break
            }
        }
        result
    }

    private static void indent(StringBuilder sb, level) {
        int lastCharIdx = sb.length() - 1
        if (sb.charAt(lastCharIdx) == ' ') {
            // delete trailing space, because IntelliJ automatically does the same thing to the spec data
            sb.deleteCharAt(lastCharIdx)
        }
        sb << '\n' + ' ' * (level*4)
    }
}

