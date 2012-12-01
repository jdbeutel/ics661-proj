package grammar

/**
 * A production in a context-free grammar.
 * This class is not thread-safe.
 */
class Rule {

    String nonTerminal
    List<String> symbols
    Attachment attachment
    Grammar grammar

    /**
     * Constructs one or more Rule from a line of a context-free grammar definition with optional attachments.
     * The rules are not added to the Grammar; the caller needs to do that,
     * because it may have a specific place where it wants them to appear.
     *
     * @param line the single rule to parse, containing ' -> ' separator, and optional ' | ' separators and '[]' attachment
     * @param g the Grammar that will contain these Rules, determining whether or not a given symbol is a terminal
     * @return the rules represented by the given line
     */
    static List<Rule> valuesOf(String line, Grammar g) {
        def parts = line.split(' -> ')
        if (parts.size() < 2) {
            throw new IllegalArgumentException("missing -> separator: $line")
        }
        if (parts.size() > 2) {
            throw new IllegalArgumentException("extra -> separators: $line")
        }
        def nonTerminal = parts[0].trim()
        def groups = parts[1].split(/ \| /)

        if (!nonTerminal) {
            throw new IllegalArgumentException("missing non-terminal to the left of -> separator: $line")
        }
        def rules = []
        for (i in 0..groups.size()-1) {
            def group = groups[i].trim()
            def description = "| group $i to the right of -> separator: $line"
            def hasAttachment = group.contains('[')
            def attIdx = group.indexOf('[')
            def symbols = group.substring(0, hasAttachment ? attIdx : group.length()).tokenize()
            if (!symbols) {
                throw new IllegalArgumentException("missing symbol(s) in $description")
            }
            def attachment = null
            if (hasAttachment) {
                if (!group.endsWith(']')) {
                    throw new IllegalArgumentException("missing attachment end ] in $description")
                }
                try {
                    attachment = new Attachment(group.substring(attIdx+1, group.length()-1))
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("bad attachment in $description", e)
                }
            }
            rules << new Rule(nonTerminal: nonTerminal, symbols: symbols, attachment: attachment, grammar: g)
        }
        rules
    }

    /**
     * @return whether this rule is in normalized terminal form
     */
    boolean isTerminalForm() {
        symbols.size() == 1 && !unitProduction
    }

    /**
     * @return whether this rule is in normalized binary form
     */
    boolean isBinaryForm() {
        symbols.size() == 2 && symbols == nonTerminalSymbols
    }

    /**
     * @return  whether this Rule is valid for CNF
     */
    boolean isNormalForm() {
        (terminalForm || binaryForm)
    }

    /**
     * Checks whether this rule is a unit production.
     * A unit production is a rule with an RHS of just one non-terminal.
     *
     * @return whether this rule is a unit production
     */
    boolean isUnitProduction() {
        symbols.size() == 1 && symbols == nonTerminalSymbols
    }

    /**
     * @return List of symbols of this Rule that are non-terminals.
     * This does not include this Rule's nonTerminal property, which is obviously non-terminal.
     */
    List<String> getNonTerminalSymbols() {
        symbols.findAll {it in grammar.nonTerminals}
    }

    /**
     * @return List of terminal symbols of this Rule.
     */
    List<String> getTerminals() {
        symbols - nonTerminalSymbols
    }

    /**
     * Replaces any and all occurrences of {@code from} with {@code to} on the right-hand side of this rule.
     * The left-hand side of this rule is not changed.  The right-hand side remains the same List instance,
     * but its contents may change.
     *
     * @param from  the symbol to change
     * @param to    the new symbol
     */
    void changeSymbols(String from, String to) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols[i] == from) {
                symbols[i] = to
            }
        }
    }

    BigDecimal getProbability() {
        attachment?.probability
    }

    // for convenient duplicate check in List
    @Override
    boolean equals(Object other) {
        other instanceof Rule && nonTerminal == other.nonTerminal && symbols == other.symbols
    }

    // for consistency with equals()
    @Override
    int hashCode() {
        nonTerminal.hashCode() + symbols.hashCode()
    }

    /**
     * @return the definition of this rule in a format suitable for creation.
     */
    @Override
    String toString() {
        "$nonTerminal -> ${symbols.join(' ')}" + (attachment ? " [$attachment]" : '')
    }
}
