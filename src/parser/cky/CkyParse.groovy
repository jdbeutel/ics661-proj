package parser.cky

import grammar.Attachment
import grammar.Rule
import parser.Parse

/**
 * A node (i.e., subtree) in a CKY parse tree.
 */
class CkyParse implements Parse {
    Rule rule   // A -> B C, or A -> terminal
    CkyParse B, C

    /**
     * Constructor for a terminal rule.
     *
     * @param r a Rule in terminal form
     */
    CkyParse(Rule r) {
        assert r.terminalForm
        rule = r
    }

    /**
     * Constructor for a binary rule.
     *
     * @param r a Rule in binary form
     * @param b the left subtree node
     * @param c the right subtree node
     */
    CkyParse(Rule r, CkyParse b, CkyParse c) {
        assert r.binaryForm
        rule = r
        B = b
        C = c
    }

    BigDecimal getProbability() {
        def p = rule.probability
        p == null || rule.terminalForm ? p : p * B.probability * C.probability
    }

    String prettyPrint(int level = 0) {
        def indentBy = ' ' * 4
        def indent = '\n' + (indentBy * level)
        def attach = ''
        if (rule.attachment) {
            attach = " {${Attachment.canonicalProbability(probability)}}"
        }
        if (rule.terminalForm) {
            assert !B && !C
            "$indent[${rule.nonTerminal} ${rule.symbols[0]}$attach]"
        } else {
            "$indent[${rule.nonTerminal}${B.prettyPrint(level+1)}${C.prettyPrint(level+1)}$indent$attach]"
        }
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
        if (rule.terminalForm) {
            assert !B && !C
            "[${rule.nonTerminal} ${rule.symbols[0]}$attach]"
        } else {
            "[${rule.nonTerminal} $B $C$attach]"
        }
    }
}
