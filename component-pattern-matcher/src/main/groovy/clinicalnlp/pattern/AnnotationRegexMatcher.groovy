package clinicalnlp.pattern

import org.apache.uima.jcas.tcas.Annotation
import org.codehaus.groovy.runtime.StringGroovyMethods

import java.util.regex.Matcher

import static clinicalnlp.pattern.AnnotationRegex.getLBRACK
import static clinicalnlp.pattern.AnnotationRegex.getRBRACK

class AnnotationRegexMatcher implements Iterator {
    private final Set<String> groupNames
    private final Map<Integer, Annotation> indexMap = [:]
    private final Matcher matcher
    private final Iterator matchIter

    AnnotationRegexMatcher(final AnnotationRegex regex, final List<? extends Annotation> sequence) {
        this.groupNames = regex.groupNames
        String matchStr = sequence.inject('') { String resultPrefix, Annotation ann ->
            String typeCode = regex.typeMap[ann.class]
            String featString = regex.featMap[ann.class].inject('') { featPrefix, featName ->
                String featVal = (featName == 'text' ? ann.coveredText : ann."${featName}")
                featPrefix + "${LBRACK}${featVal}${RBRACK}"
            }
            this.indexMap[resultPrefix.size()] = ann
            resultPrefix + typeCode + featString
        }
        this.matcher = regex.pattern.matcher(matchStr)
        this.matchIter = StringGroovyMethods.iterator(this.matcher)
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
