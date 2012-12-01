package parser.earley

import grammar.Attachment
import grammar.Rule
import groovy.transform.EqualsAndHashCode

/**
 * Chart entries in an Earley parse.
 * These are also nodes (i.e., sub-trees) in the parse tree.
 * As such, completed components are part of the identity; this differs from the textbook algorithm,
 * which could map different completed components to the same state (as an optimization).
 */
@EqualsAndHashCode(excludes = ['function', 'name'])
class EarleyState {

    Rule rule
    int dotIdx
    int inputStartIdx, inputDotIdx
    List<EarleyState> components = []
    String function     // not part of the identity
    String name         // not part of the identity

    EarleyState(Rule rule, int dotIdx, List inputIdxPair, String function) {
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
     * Factory method for a new EarleyState with the dot advanced over one completed component.
     *
     * @param component completed for advance
     * @return new EarleyState
     */
    EarleyState completer(EarleyState component) {
        assert !complete && component.complete
        def result = new EarleyState(rule, dotIdx+1, [inputStartIdx, component.inputDotIdx], 'completer')
        if (dotIdx > 0) {
            result.components += components[0..<dotIdx]
        }
        result.components[dotIdx] = component
        result
    }

    /**
     * Factory method for a new EarleyState with the dot advanced over one terminal symbol.
     * This is an optimization of the textbook's optimization of scanner(),
     * to support terminal symbols in non-terminal-form rules (i.e., in lexicon, or part of speech).
     *
     * @return new EarleyState
     */
    EarleyState advancing() {
        assert !complete && b in rule.terminals
        def result = new EarleyState(rule, dotIdx+1, [inputStartIdx, inputDotIdx+1], 'advancing')
        if (dotIdx > 0) {
            result.components += components[0..<dotIdx]
        }
        result.components[dotIdx] = null    // just the terminal symbol here
        result
    }

    /**
     * @return render of the subtree rooted at this node (recursively) in bracket format
     */
    @Override
    String toString() {
        def attach = ''
        if (rule.attachment) {
            attach = " {${Attachment.canonicalProbability(probability)}}"
        }
        def subtrees = []
        for (i in 0..<rule.symbols.size()) {
            def s = rule.symbols[i]
            subtrees << (i < dotIdx && s in rule.nonTerminalSymbols ? components[i] : s)
        }
        "[$name ${rule.nonTerminal} ${subtrees.join(' ')} ($inputStartIdx,$inputDotIdx)$attach]"
    }

    String toFlatString() {
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
        p
    }
}
