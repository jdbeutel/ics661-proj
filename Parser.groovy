/**
 * Implementation of the CKY algorithm, a bottom-up parse of a CNF grammar.
 */
class Parser {

    List<String> words
    Grammar grammar
    List<List<List<Parse>>> table = [].withDefault {[].withDefault {[]}}

    Parser(List<String> words, Grammar g) {
        g.normalize()     // CKY requires CNF
        grammar = g
        words.add(0, 'dummy')  // start words from index 1, to correspond with alg in book
        this.words = words
        parse()
    }

    private void parse() {
        for (j in 1..words.size()) {
            table[j-1][j].addAll(grammar.lexiconOf(words[j]).collect {new Parse(it)})
            for (int i = j-2; i >= 0; i--) {    // skips j == 1
                for (k in i+1..j-1) {
                    for (B in table[i][k]) {
                        for (C in table[k][j]) {
                            def matching = grammar.rulesTo(B.rule.nonTerminal, C.rule.nonTerminal)
                            table[i][j].addAll(matching.collect {new Parse(it, B, C)})
                        }
                    }
                }
            }
        }
    }

    List<Parse> getParses() {
        def completeParses = table[0][words.size()]
        completeParses.findAll {it.rule.nonTerminal == grammar.startSymbol}
    }
}

class Parse {
    Rule rule   // A -> B C
    Parse B, C

    Parse(Rule r) {
        assert r.terminalForm
        rule = r
    }

    Parse(Rule r, Parse b, Parse c) {
        rule = r
        B = b
        C = c
    }

    @Override
    String toString() {
        if (rule.terminalForm) {
            assert !B && !C
            "[${rule.nonTerminal} ${rule.symbols[0]}]"
        } else {
            "[${rule.nonTerminal} $B $C]"
        }
    }
}
