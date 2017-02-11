package clinicalnlp.pattern

import org.apache.uima.jcas.tcas.Annotation
import org.codehaus.groovy.runtime.StringGroovyMethods

import java.util.regex.Matcher

import static clinicalnlp.pattern.AnnotationRegex.LBRACK
import static clinicalnlp.pattern.AnnotationRegex.RBRACK

/**
 * AnnotationRegexMatcher class definition
 */
class AnnotationRegexMatcher implements Iterator {
    // -----------------------------------------------------------------------------------------------------------------
    // Instance fields
    // -----------------------------------------------------------------------------------------------------------------
    private final Set<String> groupNames
    private final String seqString
    private final Map<Integer, Annotation> indexMap = [:]
    private final Matcher matcher
    private final Iterator iterator

    // -----------------------------------------------------------------------------------------------------------------
    // Public methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor
     * @param regex
     * @param sequence
     */
    AnnotationRegexMatcher(final AnnotationRegex regex, final List<? extends Annotation> sequence) {
        this.groupNames = regex.groupNames
        this.seqString = sequence.inject('') { String resultPrefix, Annotation ann ->
            String typeCode = regex.typeMap[ann.class]
            String featString = regex.featMap[ann.class].inject('') { featPrefix, featName ->
                String featVal = (featName == 'text' ? ann.coveredText : ann."${featName}" ?: '')
                featPrefix + "${LBRACK}${featVal}${RBRACK}"
            }
            this.indexMap[resultPrefix.size()] = ann
            resultPrefix + typeCode + featString
        }

        println "Pattern: ${regex.pattern}"
        println "Match string: ${this.seqString}"
        this.matcher = regex.pattern.matcher(this.seqString)
        this.iterator = StringGroovyMethods.iterator(this.matcher)
    }

    @Override
    boolean hasNext() {
        return this.iterator.hasNext()
    }

    @Override
    Object next() {
        this.iterator.next()
        Binding binding = new Binding()
        binding.$matchString = this.seqString
        this.groupNames.each { String name ->
            String matchedText = this.matcher.group(name)
            if (matchedText) {
                List matchedAnns = []
                for (Integer idx : (this.matcher.start(name)..this.matcher.end(name)-1)) {
                    if (this.indexMap.containsKey(idx)) { matchedAnns << this.indexMap[idx] }
                }
                binding.setVariable(name, matchedAnns)
            }
        }
        return binding
    }

    @Override
    String toString() {
        return this.seqString
    }
}
