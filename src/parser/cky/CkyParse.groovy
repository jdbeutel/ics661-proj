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

    /**
     * @return the probability of this node in the parse tree, calculated recursively
     */
    BigDecimal getProbability() {
        def p = rule.probability
        p == null || rule.terminalForm ? p : p * B.probability * C.probability
    }

    /**
     * @return the probability attachment rendered for printing, or the empty String if none.
     *          Note that CkyParse does not support semantic attachments.
     */
    String getAttachmentString() {
        rule.attachment ? " {${Attachment.canonicalProbability(probability)}}" : ''
    }

    /**
     * Renders a pretty, multi-line parse subtree rooted at this node.
     *
     * @param level indentation levels to start from
     * @return a String having one line per node in this subtree
     */
    String prettyPrint(int level = 0) {
        def indentBy = ' ' * 4
        def indent = '\n' + (indentBy * level)
        if (rule.terminalForm) {
            "$indent$this"
        } else {
            "$indent[${rule.nonTerminal}${B.prettyPrint(level+1)}${C.prettyPrint(level+1)}$indent$attachmentString]"
        }
    }

    /**
     * @return render of the subtree rooted at this node (recursively) in bracket format
     */
    @Override
    String toString() {
        if (rule.terminalForm) {
            assert !B && !C
            "[${rule.nonTerminal} ${rule.symbols[0]}$attachmentString]"
        } else {
            "[${rule.nonTerminal} $B $C$attachmentString]"
        }
    }
}
