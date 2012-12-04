package parser.earley

import java.util.regex.Pattern
import parser.Parser
import grammar.Grammar
import grammar.Rule

/**
 * Implementation of the Earley parser, from the textbook.
 */
class EarleyParser extends Parser {

    List<List<EarleyState>> chart = [].withDefault {[]}

    /**
     * Constructs an EarleyParser, parsing the given line with the given Grammar.
     *
     * @param line the line of words to parse (e.g., a sentence)
     * @param g the grammar to use for the parse
     * @param lexer (optional) a regex identifying each separate word (i.e., token) in the line
     */
    EarleyParser(String line, Grammar g, Pattern lexer = ~/\w+/) {
        super(line, g, lexer)
        parse()
    }

    /**
     * Parses this parser's line of words, according to its grammar.
     * This is the Earley algorithm from the textbook.
     */
    private void parse() {
        enqueue(dummyStartState, chart[0])
        for (i in 0..words.size()) {
            int j = 0
            while (j < chart[i].size()) {   // allowing chart[i] to grow as j goes through it
                def state = chart[i][j++]
                if (state.complete) {
                    completer(state)
                } else {
                    if (grammar.isLexicon(state.b)) {
                        scanner(state)
                    } else if (state.b in grammar.terminals) {
                        advancing(state)
                    } else {
                        predictor(state)
                    }
                }
            }
        }
    }

    private getDummyStartState() {
        Rule dummy = Rule.valuesOf("γ -> ${grammar.startSymbol}", grammar)[0]
        new EarleyState(dummy, 0, [0, 0], 'dummy start state')
    }

    private void predictor(EarleyState state) {
        def B = state.b
        def j = state.inputDotIdx
        for (rule in grammar.rulesFor(B)) {
            enqueue(new EarleyState(rule, 0, [j, j], 'predictor'), chart[j])
        }
    }

    private void scanner(EarleyState state) {
        def B = state.b
        def j = state.inputDotIdx
        def word = words[j]
        def rule = grammar.lexiconOf(word).find {it.nonTerminal == B}
        if (rule) {
            assert rule.nonTerminal == B && rule.symbols == [word]
            enqueue(new EarleyState(rule, 1, [j, j+1], 'scanner'), chart[j+1])
        }
    }

    private void advancing(EarleyState state) {
        def j = state.inputDotIdx
        if (words[j] == state.b) {
            enqueue(state.advancing(), chart[j+1])
        }
    }

    private void completer(EarleyState state) {
        def B = state.rule.nonTerminal
        def j = state.inputStartIdx
        def k = state.inputDotIdx
        def incompleteStates = chart[j].findAll {!it.complete && it != dummyStartState}
        for (EarleyState match in incompleteStates.findAll {it.b == B && it.inputDotIdx == j}) {
            enqueue(match.completer(state), chart[k])
        }
    }

    private enqueueSequence = 0

    private void enqueue(EarleyState state, List<EarleyState> chartEntry) {
        if (!(state in chartEntry)) {
            state.name = "S${enqueueSequence++}"
            chartEntry << state
        }
    }

    @Override
    List getCompletedParses() {
        def N = words.size()
        def fullParses = chart[N].findAll {it.complete && it.inputStartIdx == 0 && it.inputDotIdx == N} // [0,N]
        def sParses = fullParses.findAll {it.rule.nonTerminal == grammar.startSymbol}   // all S for [0,N]
        if (grammar.hasAttachments()) {
            sParses = sParses.sort {-it.probability}      // most-probable first
        }
        sParses
    }

    /**
     * Renders the whole parse chart, for debugging.
     *
     * @return a flat rendering of the parse chart (i.e., not nested or treed)
     */
    @Override
    String toString() {
        (0..<chart.size()).collect { i ->
            "Chart[$i]\t" + chart[i]*.toFlatString().join('\n\t')
        }.join('\n\n')
    }

    /**
     * For running from the command line (from the "src" dir),
     * this loads a grammar file and uses it to parse lines from stdin.
     *
     * @param args  command line arguments
     */
    static void main(String[] args) {

        if (args.size() != 1) {
            System.err.println "usage: groovy parser/earley/EarleyParser grammarFile < sentenceLines"
            System.exit 1
        }

        def g = new Grammar(new File(args[0]))
        System.in.eachLine {line ->
            println "\n$line: " + new EarleyParser(line, g).prettyCompletedParsesString
            null    // just avoiding a warning about not returning a value from eachLine
        }
    }
}
