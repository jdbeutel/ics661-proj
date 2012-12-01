package grammar

/**
 * Data attached to a Rule.
 */
class Attachment {
    BigDecimal probability

    Attachment(String content) {
        try {
            probability = new BigDecimal(content)
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("could not parse probability $content", e)
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
