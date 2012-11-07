/**
 * Implementation of the CKY algorithm, a bottom-up parse of a CNF grammar, with probability.
 * Instead of optimizing the parse by limiting it to the rule with the highest probability
 * for any given non-terminal, this generates all possible parses (like without probability)
 * and then sorts by probability, for the sake of debugging.
 */
class Parser {

    List<String> words
    Grammar grammar
    List<List<List<Parse>>> table = [].withDefault {[].withDefault {[]}}

    /**
     * Constructs a Parser, parsing the given line with the given Grammar.
     *
     * @param line the line of words to parse (i.e., a sentence)
     * @param g the grammar to use for the parse
     */
    Parser(String line, Grammar g) {
        g.normalize()     // CKY requires CNF, so just in case g is not already
        grammar = g
        words = line.tokenize()
        parse()
    }

    /**
     * Does the CKY parse on this parser's line of words, according to its grammar.
     * This is the CKY algorithm from the book.
     */
    private void parse() {

        for (j in 1..words.size()) {    // for each word (or column from left)

            // The cell on the diagonal gets all terminal lexicon parses (A -> word) for this word.
            String word = words[j-1]    // j-1 adjusts the algorithm to the 0-based list
            List<Rule> lexicon = grammar.lexiconOf(word)
            List<Parse> lexiconParses = lexicon.collect {new Parse(it)}
            table[j-1][j].addAll(lexiconParses)     // diagonal (starting from row 0 and column 1)

            // Each cell in this column, from above the diagonal to row 0 (i.e., table[i][j]),
            // gets all possible combinations of binary parses (A -> B C) on the parses so far.
            for (int i = j-2; i >= 0; i--) {    // using Java for() loop to skip j == 1 (the first column)
                for (k in i+1..j-1) {           // for each combination for [i,k] of row i to [k,j] of column j
                    for (B in table[i][k]) {        // for each B = [i,k]
                        for (C in table[k][j]) {    // for each C = [k,j]
                            def matching = grammar.rulesTo(B.rule.nonTerminal, C.rule.nonTerminal)  // all A -> B C
                            table[i][j].addAll(matching.collect {new Parse(it, B, C)})
                        }
                    }
                }
            }
        }
    }

    /**
     * @return a list of roots of all accepted, full parse trees, or the empty list if none are accepted
     */
    List<Parse> getCompletedParses() {
        def fullParses = table[0][words.size()]                                         // all A for [0,N]
        def sParses = fullParses.findAll {it.rule.nonTerminal == grammar.startSymbol}   // all S for [0,N]
        sParses.sort {-it.probability}      // most-probable first
    }

    /**
     * Renders all accepted, full parses as required for assignment 4.
     *
     * @return a rendering of all possible parses, or "not S" if none are accepted
     */
    String getCompletedParsesString() {
        completedParses?.join(';') ?: "not ${grammar.startSymbol}"
    }

    /**
     * Renders the whole parse table, for debugging.  (This is not required for assignment 4.)
     *
     * @return a rendering of the parse table along the diagonals, from the terminals to the apex
     */
    @Override
    String toString() {
        def s = ''
        for (j in 1..table.size()) {
            for (i in 0..table.size()-j) {
                s += "table[$i][${i+j}] = ${table[i][i+j].sort {-it.probability}}\n"
            }
        }
        s
    }
}

/**
 * A node (i.e., subtree) in a CKY parse tree.
 */
class Parse {
    Rule rule   // A -> B C, or A -> terminal
    Parse B, C

    /**
     * Constructor for a terminal rule.
     *
     * @param r a Rule in terminal form
     */
    Parse(Rule r) {
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
    Parse(Rule r, Parse b, Parse c) {
        assert r.binaryForm
        rule = r
        B = b
        C = c
    }

    BigDecimal getProbability() {
        def p = rule.attachment.probability
        rule.terminalForm ? p : p * B.probability * C.probability
    }

    /**
     * @return render of the subtree rooted at this node (recursively) in bracket format
     */
    @Override
    String toString() {
        def p = Attachment.canonicalProbability(probability)
        if (rule.terminalForm) {
            assert !B && !C
            "[${rule.nonTerminal} ${rule.symbols[0]}][$p]"
        } else {
            "[${rule.nonTerminal} $B $C][$p]"
        }
    }
}
