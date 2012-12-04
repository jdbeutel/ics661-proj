package grammar

import fol.lambda.SingleTerm
import parser.earley.EarleyState
import fol.lambda.Application
import fol.FirstOrderLogic

/**
 * Data attached to a Rule.
 */
class Attachment {
    BigDecimal probability
    Closure lambdaClosure

    Attachment(String content) {
        def idx = content.indexOf(',')
        def p = (idx == -1 ? content : content.substring(0, idx))
        try {
            probability = new BigDecimal(p)
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("could not parse probability $p from $content", e)
        }
        if (idx != -1) {    // semantic Lambda term
            parseLambda(content.substring(idx+1).trim())
        }
    }

    private void parseLambda(String s) {
        if (s ==~ /\$\d/) {                         // e.g., Nominal -> Noun  [1.0, $0]
            lambdaClosure = { EarleyState es ->
                es.components[s[1] as int].lambda
            }
        } else if (s ==~ /\$\d\(\$\d\)/) {          // e.g., S -> NP VP  [1.0, $0($1)]
            lambdaClosure = { EarleyState es ->
                SingleTerm x = es.components[s[1] as int].lambda
                SingleTerm y = es.components[s[4] as int].lambda
                new Application(abstractionTerm: x, term: y)
            }
        } else {                                    // e.g., Noun -> restaurant [1.0, λr.Restaurant(r)]
            lambdaClosure = { FirstOrderLogic.parseLambda(s) }
        }
    }

    /**
     * @return a minimal String representation of the probability
     */
    static String canonicalProbability(p) {
        def s = p as String
        if (s.endsWith('.0')) {
            s = s[0..-3]
        }
        if (s.startsWith('0.') && s.length() > 2) {
            s = s[1..-1]
        }
        s
    }

    /**
     * @return the definition of this Attachment in a format suitable for creation.
     */
    @Override
    String toString() {
        canonicalProbability(probability)
    }
}
