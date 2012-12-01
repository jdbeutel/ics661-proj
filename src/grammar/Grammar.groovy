package grammar

/**
 * A context-free grammar with optional probability attachments.
 * This class is not thread-safe.
 */
class Grammar {

    final startSymbol = 'S'
    List<Rule> rules = []
    int dummyCounter = 1

    /**
     * Constructs a Grammar by parsing the given definition String.
     *
     * @param definition    lines defining the grammar, in a String suitable for testing
     */
    Grammar(String definition) {
        for (line in definition.split('\n')) {
            addRule(line)
        }
        validateAttachments()
    }

    private void validateAttachments() {
        if (hasAttachments()) {
            for (s in nonTerminals) {
                def total = rulesFor(s).probability.sum()
                if (total != 1) {
                    throw new IllegalStateException("total probability of rules for $s is $total instead of 1")
                }
            }
        }
    }

    boolean hasAttachments() {
        rules.find {it.attachment} as boolean
    }

    /**
     * Adds a Rule for the given line to this grammar.
     *
     * @param line  the definition of the Rule to add
     * @param i     optional index to insert in rules list, defaults to end
     */
    private void addRule(String line, int i = rules.size()) {
        def newRules = Rule.valuesOf(line, this)
        for (r in newRules) {
            if (rules.contains(r)) {
                throw new IllegalArgumentException("duplicate rule $r")
            }
            rules.add(i++, r)
        }
    }

    /**
     * Constructs a Grammar by parsing the given definition File.
     * This also validates that the given grammar contains a rule for the start symbol.
     * The String version of the constructor is less strict, for testing.
     *
     * @param definition    lines defining the grammar, in a File
     */
    Grammar(File definition) {
        this(canonicalEof(definition))
        if (!(startSymbol in nonTerminals)) {
            throw new IllegalArgumentException("$definition missing rule for start symbol $startSymbol")
        }
    }

    /**
     * Reads the given file into a String and converts EOF to a canonical '\n' regardless of OS.
     *
     * @param definition    the file to read
     * @return  a String of the text in the given file, with canonical EOF
     */
    private static String canonicalEof(File definition) {
        def s = ''
        definition.eachLine {
            s += it + '\n'
        }
        s
    }

    /**
     * @return the set of non-terminal symbols, a.k.a. N, derived from the rules
     */
    Set<String> getNonTerminals() {
        rules.nonTerminal as Set
    }

    /**
     * @return the set of terminal symbols, a.k.a. Sigma (disjoint from N), derived from the rules
     */
    Set<String> getTerminals() {
        new HashSet(rules.symbols.flatten()) - nonTerminals
    }

    /**
     * Converts this grammar to CNF (Chomsky normal form).
     * The conversion is done in place, not copied to a new grammar.
     */
    void normalize() {
        convertTerminalsWithinRulesToDummyNonTerminals()
        convertUnitProductions()
        makeAllRulesBinary()
        removeRulesThatAreUnreachableFromStartSymbol()

        rules.each {assert it.normalForm}
        validateAttachments()
    }

    /**
     * Converts terminals within rules to dummy non-terminals.
     * Only terminals in rules with more than one symbol on the RHS are converted,
     * because a terminal in an RHS with only one symbol is already in normal form.
     * However, once a terminal is converted, all its occurrences are converted
     * to the same dummy; since this grammar is normalized in place,
     * this may generate unit productions, but they will be eliminated in the next step.
     */
    private void convertTerminalsWithinRulesToDummyNonTerminals() {
        for (int i = 0; i < rules.size(); i++) {
            Rule r = rules[i]
            if (r.symbols.size() > 1) {
                String terminal
                while (terminal = r.symbols.find {it in terminals}) {
                    def dummy = nextDummySymbol
                    def attach = hasAttachments() ? '[1.0]' : ''
                    addRule("$dummy -> $terminal $attach", ++i)     // skip on next loop
                    rules.findAll {it.symbols.size() > 1}.each {it.changeSymbols(terminal, dummy)}
                }
            }
        }
    }

    /**
     * Eliminates unit productions, rules with an RHS of just one non-terminal.
     * That RHS is replaced with a copy of every rule with it on the LHS.
     * Each unit production adds an unnecessary step, because the grammar
     * matches the same language without them.
     */
    private void convertUnitProductions() {
        Rule r
        while (r = rules.find {it.unitProduction}) {
            String redundant = r.symbols[0]
            int i = rules.indexOf(r)
            rules.remove(i)
            for (q in rulesFor(redundant)) {
                def attach = ''
                if (hasAttachments()) {
                    def p = r.probability * q.probability
                    attach = "[$p]"
                }
                addRule("${r.nonTerminal} -> ${q.symbols.join(' ')} $attach", i++)
            }
        }
    }

    /**
     * Converts any rules with more than two symbols on the RHS into rules with just two symbols.
     * This method follows the book's arbitrary method of converting the first two symbols
     * into a dummy rule, and iterating on the same rule if it is still too long.
     * The new dummy is substituted for that pair in all rules where they are the first symbols,
     * and the new dummy rule is inserted after the rule with the last substitution,
     * just for consistency with the example in the book.  That pair could be substituted
     * wherever they appear in the symbols, perhaps, but I would rather keep this code simple
     * than try to optimize it.
     */
    private void makeAllRulesBinary(){
        for (int i = 0; i < rules.size(); i++) {
            Rule r = rules[i]
            if (r.symbols.size() > 2) {
                def leadPair = r.symbols.subList(0, 2)
                def dummy = nextDummySymbol
                int lastIndex = i
                for (int j = i; j < rules.size(); j++) {
                    Rule q = rules[j]
                    if (q.symbols.size() > 2 && q.symbols.subList(0, 2) == leadPair) {
                        def theRest = q.symbols.subList(2, q.symbols.size())
                        q.symbols = [dummy] + theRest
                        lastIndex = j   // to put X2 after the last pair, like the book does
                    }
                }
                def attach = hasAttachments() ? '[1.0]' : ''
                addRule("$dummy -> ${leadPair[0]} ${leadPair[1]} $attach", lastIndex+1)
                i-- // check current rule again
            }
        }
    }

    /**
     * Removes any rules that are not reachable from the start symbol.
     * For converting in place, this is necessary to clean up after
     * rules that have become redundant after eliminating unit productions.
     */
    private void removeRulesThatAreUnreachableFromStartSymbol(){
        def reached = [startSymbol] as Set
        int previous = 0
        while (reached.size() != previous) {
            previous = reached.size()
            def current = reached.collect {rulesFor(it).symbols}
            reached.addAll((List<String>) current.flatten())
        }
        def unreachable = nonTerminals - reached
        rules.removeAll {it.nonTerminal in unreachable}
    }

    /**
     * Finds all rules with the given non-terminal on their LHS.
     * This method scans all the rules, rather than maintaining
     * a map like this throughout the class, because I would rather
     * keep this code simple than try to optimize it.
     *
     *
     * @param nonTerminal   the LHS of the rules to find
     * @return  a list of rules with the given LHS
     */
    List<Rule> rulesFor(String nonTerminal) {
        rules.findAll {it.nonTerminal == nonTerminal}
    }

    /**
     * Finds all rules with an RHS of just the given terminal.
     * This method scans all the rules, rather than maintaining
     * a map like this throughout the class, because I would rather
     * keep this code simple than try to optimize it.
     *
     *
     * @param terminal   the whole RHS of the rules to find
     * @return  a list of rules with the given RHS
     */
    List<Rule> lexiconOf(String terminal) {
        rules.findAll {it.terminalForm && it.symbols[0] == terminal}
    }

    /**
     * Checks whether the given symbol is an LHS in the lexicon.
     * This method scans all the rules, rather than maintaining
     * a map like this throughout the class, because I would rather
     * keep this code simple than try to optimize it.
     *
     *
     * @param nonTerminal   the LHS of the rules to check
     * @return  whether the given symbol just the LHS of rules in terminal form
     */
    boolean isLexicon(String nonTerminal) {
        def matches = rulesFor(nonTerminal)
        matches && !matches.find {!it.terminalForm}
    }

    /**
     * Finds all binary rules with an RHS of the given pair of non-terminals.
     * This method scans all the rules, rather than maintaining
     * a map like this throughout the class, because I would rather
     * keep this code simple than try to optimize it.
     *
     *
     * @param b the first non-terminal on the RHS
     * @param c the second non-terminal on the RHS
     * @return  a list of binary rules with the given RHS
     */
    List<Rule> rulesTo(String b, String c) {
        rules.findAll {it.binaryForm && it.symbols[0] == b && it.symbols[1] == c}
    }

    /**
     * Gets the next available dummy symbol.
     * The symbol is an 'X' followed by an incrementing counter.
     * Symbols already in use are skipped.
     *
     * @return the next dummy symbol not already in use
     */
    private String getNextDummySymbol() {
        String s = null
        while (!s || s in nonTerminals || s in terminals) {
            s = 'X' + (dummyCounter++)
        }
        s
    }

    /**
     * @return the definition of this grammar in a format suitable for creation and comparision
     */
    @Override
    String toString() {
        rules.join('\n')
    }
}
