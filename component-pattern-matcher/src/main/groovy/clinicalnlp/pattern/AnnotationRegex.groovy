package clinicalnlp.pattern

import groovy.util.logging.Log4j
import org.apache.uima.jcas.tcas.Annotation

import java.util.regex.Pattern

/**
 * AnnotationRegex class definition
 */
@Log4j
class AnnotationRegex {
    // -----------------------------------------------------------------------------------------------------------------
    // Static fields
    // -----------------------------------------------------------------------------------------------------------------
    static final Integer INIT_TYPE_CODE = 0x2460;
    static final Character LBRACK = (char)0x2039;
    static final Character RBRACK = (char)0x203A;

    // -----------------------------------------------------------------------------------------------------------------
    // Instance fields
    // -----------------------------------------------------------------------------------------------------------------
    Map<Class<? extends Annotation>, Character> typeMap = [:]
    Map<Class<? extends Annotation>, Set<String>> featMap = [:]
    Set<String> groupNames = []
    private Integer typeCode = INIT_TYPE_CODE
    private Pattern pattern

    // -----------------------------------------------------------------------------------------------------------------
    // Public methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor
     * @param annotationPattern
     */
    AnnotationRegex(AnnotationPattern annotationPattern) {
        this.extractTypes(annotationPattern)
        this.pattern = genRegExPattern(annotationPattern)
    }

    /**
     * Create an annotation matcher
     * @param sequence
     * @return
     */
    AnnotationRegexMatcher matcher(List<? extends Annotation> sequence) {
        return new AnnotationRegexMatcher(this, sequence)
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Private methods
    // -----------------------------------------------------------------------------------------------------------------

    // generate Pattern instance from AnnotationPattern structure
    private Pattern genRegExPattern(AnnotationPattern annotationPattern) {
        return Pattern.compile(this.genRegexString(annotationPattern))
    }

    // extract all type and feature information from the regex tree
    private extractTypes(AnnotationPattern pattern) {
        if (pattern.name) { this.groupNames.add(pattern.name) }
        switch (pattern.class) {
            case AtomicAnnotationPattern.class:
                Class<? extends Annotation> type = pattern.type
                if (!typeMap.keySet().contains(type)) {
                    typeMap[type] = (char)this.typeCode
                    typeCode++
                    featMap[type] = new TreeSet<String>()
                }
                featMap[type].addAll(pattern.features.keySet())
                break
            case SequenceAnnotationPattern.class:
                pattern.children.each { extractTypes(it) }
                break
            case OptionAnnotationPattern.class:
                pattern.children.each { extractTypes(it) }
                break
        }
    }

    // Recursive method for generating regex string from AnnotationPattern
    private String genRegexString(AnnotationPattern pattern) {
        switch (pattern.class) {
            case AtomicAnnotationPattern.class:
                return genRegexString((AtomicAnnotationPattern)pattern)
            case SequenceAnnotationPattern.class:
                return genRegexString((SequenceAnnotationPattern)pattern)
            case OptionAnnotationPattern.class:
                return genRegexString((OptionAnnotationPattern)pattern)
        }
    }

    // Base case method for generating regex string from an AtomicAnnotationPattern
    private String genRegexString(AtomicAnnotationPattern pattern) {
        Class<? extends Annotation> type = pattern.type
        Map<String, String> feats = pattern.features
        String featString = featMap[type].inject('') { prefix, featName ->
            String featVal = feats.containsKey(featName) ?
                feats[featName].replaceAll(/(?<!\\)\./, "[^${LBRACK}${RBRACK}]")
                : "[^${LBRACK}${RBRACK}]*"
            prefix + "${LBRACK}(?:${featVal})${RBRACK}"
        }
        return decorate("${typeMap[type]}${featString}", pattern)
    }

    // Recursive method for generating regex string from a SequenceAnnotationPattern
    private String genRegexString(SequenceAnnotationPattern pattern) {
        String result = pattern.children.inject(''){prefix,rest -> "${prefix}${this.genRegexString(rest)}"}
        return decorate(result, pattern)
    }

    // Recursive method for generating regex string from an OptionAnnotationPattern
    private String genRegexString(OptionAnnotationPattern pattern) {
        String first = this.genRegexString(pattern.children.remove(0))
        String result = pattern.children.inject(first){prefix,rest -> "${prefix}|${this.genRegexString(rest)}"}
        return decorate(result, pattern)
    }

    // Add name, group, and quantifier information to regex
    private String decorate(final String baseRegex, final AnnotationPattern p) {
        // initialize return value
        String result = baseRegex

        // add range information, and check for lazy vs. greedy evaluation
        if (p.range) { result = "(?:${result}){${p.range.min()},${p.range.max()}}${p.greedy?'':'?'}" }

        // check for group type
        if (p.name) { result = "(?<${p.name}>${result})" }
        else if (p.lookAhead && p.positive) { result = "(?=${result})" }
        else if (p.lookAhead && !p.positive) { result = "(?!${result})" }
        else if (p.lookBehind && p.positive) { result = "(?<=${result})" }
        else if (p.lookBehind && !p.positive) { result = "(?<!${result})" }
        else { result = "(?:${result})" }

        // return value
        return result
    }
}
