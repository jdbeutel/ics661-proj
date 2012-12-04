package parser

/**
 * Common interface for the results of a parse, representing the root of a subtree of the parse tree.
 */
interface Parse {

    /**
     * Renders a pretty, multi-line parse subtree rooted at this node.
     *
     * @param level indentation levels to start from
     * @return a String having one line per node in this subtree
     */
    String prettyPrint(int level)
}
