import java.util.regex.Pattern
import groovy.transform.EqualsAndHashCode

/**
 * Implementation of the Earley parser, from the textbook.
 */
class EarleyParser extends Parser {

    List<List<State>> chart = [].withDefault {[]}

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
                def state = chart[i][j]
                if (state.complete) {
                    completer(state)
                } else {
                    if (grammar.isLexicon(state.b)) {
                        scanner(state)
                    } else {
                        predictor(state)
                    }
                }
                j++
            }
        }
    }

    private getDummyStartState() {
        Rule dummy = Rule.valuesOf("γ -> ${grammar.startSymbol}", grammar)[0]
        new State(dummy, 0, [0, 0], 'dummy start state')
    }

    private void predictor(State state) {
        def B = state.b
        def j = state.inputDotIdx
        for (rule in grammar.rulesFor(B)) {
            enqueue(new State(rule, 0, [j, j], 'predictor'), chart[j])
        }
    }

    private void scanner(State state) {
        def B = state.b
        def j = state.inputDotIdx
        def word = words[j]
        def rule = grammar.lexiconOf(word).find {it.nonTerminal == B}
        if (rule) {
            assert rule.nonTerminal == B && rule.symbols == [word]
            enqueue(new State(rule, 1, [j, j+1], 'scanner'), chart[j+1])
        }
    }

    private void completer(State state) {
        def B = state.rule.nonTerminal
        def j = state.inputStartIdx
        def k = state.inputDotIdx
        for (State match in chart[j].findAll {it.b == B && it.inputDotIdx == j}) {
            enqueue(match.completer(state), chart[k])
        }
    }

    private static enqueueSequence = 0

    private void enqueue(State state, List<State> chartEntry) {
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

    @Override
    String toString() {
        (0..<chart.size()).collect { i ->
            "Chart[$i]\t" + chart[i].join('\n\t')
        }.join('\n\n')
    }

    /**
     * Chart entries in an Earley parse.
     * These are also nodes (i.e., sub-trees) in the parse tree.
     * As such, completed components are part of the identity (differing from the textbook algorithm).
     */
    @EqualsAndHashCode(excludes = ['function', 'name'])
    static class State {

        Rule rule
        int dotIdx
        int inputStartIdx, inputDotIdx
        List<State> components = []
        String function     // not part of the identity
        String name         // not part of the identity

        State(Rule rule, int dotIdx, List inputIdxPair, String function) {
            this.rule = rule
            this.dotIdx = dotIdx
            (inputStartIdx, inputDotIdx) = inputIdxPair
            this.function = function
        }

        boolean isComplete() {
            dotIdx == rule.symbols.size()
        }

        String getB() {
            assert !complete
            rule.symbols[dotIdx]
        }

        /**
         * Factory method for a new State with the dot advanced by one component.
         *
         * @param component completed for advance
         * @return new State
         */
        State completer(State component) {
            assert !complete && component.complete
            def result = new State(rule, dotIdx+1, [inputStartIdx, component.inputDotIdx], 'completer')
            result.components[dotIdx] = component
            result
        }

        @Override
        String toString() {
            [name, ruleWithDot, [inputStartIdx, inputDotIdx], function].join('\t')
        }

        private String getRuleWithDot() {
            def withDot = [] + rule.symbols
            withDot.addAll(dotIdx, '∙')     // insert
            "${rule.nonTerminal} -> ${withDot.join(' ')}"
        }

        BigDecimal getProbability() {
            def p = rule.probability
            if (p != null) {
                for (int i = 0; i < dotIdx; i++) {
                    if (rule.symbols[i] in rule.nonTerminalSymbols) {
                        p *= components[i].probability
                    } else {
                        assert !components[i]
                    }
                }
            }
            return p
        }
    }
}
