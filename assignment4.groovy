// script for assignment 4

if (args.size() != 1) {
    System.err.println "usage: groovy assignment4 grammarFile < sentenceLines"
    System.exit 1
}

def g = new Grammar(new File(args[0]))
System.in.eachLine {line ->
    def words = line.tokenize()
    def p = new Parser(words, g)
    println p.completedParsesString
    null    // just avoiding a warning about not returning a value to eachLine
}
