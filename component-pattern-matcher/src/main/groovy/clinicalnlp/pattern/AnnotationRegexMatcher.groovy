package clinicalnlp.pattern

import org.apache.uima.jcas.tcas.Annotation
import org.codehaus.groovy.runtime.StringGroovyMethods

import java.util.regex.Matcher

class AnnotationRegexMatcher implements Iterator {
    private final Set<String> groupNames
    private final Map<Integer, Annotation> indexMap
    private final Matcher matcher
    private final Iterator matchIter

    AnnotationRegexMatcher(final AnnotationRegex regex, final List<? extends Annotation> sequence) {
        def result = AnnotationStringGenerator.genSequenceString(regex, sequence)
        ////println "Pattern regex: ${regex.pattern.toString()}"
        ////println "Match string: ${result.second}"
        this.indexMap = result.first
        this.matcher = regex.pattern.matcher(result.second)
        this.matchIter = StringGroovyMethods.iterator(this.matcher)
        this.groupNames = regex.groupNames
    }

    @Override
    boolean hasNext() {
        return this.matchIter.hasNext()
    }

    @Override
    Object next() {
        this.matchIter.next()
        Binding binding = new Binding()
        this.groupNames.each { String name ->
            String matchedText = matcher.group(name)
            if (matchedText) {
                List matchedAnns = []
                for (Integer idx : (matcher.start(name)..matcher.end(name)-1)) {
                    if (this.indexMap.containsKey(idx)) { matchedAnns << this.indexMap[idx] }
                }
                binding.setVariable(name, matchedAnns)
            }
        }
        return binding
    }
}
