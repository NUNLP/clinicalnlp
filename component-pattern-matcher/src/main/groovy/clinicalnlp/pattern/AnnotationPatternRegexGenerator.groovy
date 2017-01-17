package clinicalnlp.pattern

import groovy.util.logging.Log4j
import org.apache.uima.jcas.tcas.Annotation

import java.util.regex.Pattern

@Log4j
class AnnotationPatternRegexGenerator {

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
    Integer typeCode = INIT_TYPE_CODE
    AnnotationPattern pattern

    // -----------------------------------------------------------------------------------------------------------------
    // Public methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     *
     * @param pattern
     */
    AnnotationPatternRegexGenerator(AnnotationPattern pattern) {
        this.pattern = pattern
    }

    /**
     *
     * @return
     */
    Pattern genRegExPattern() {
        extractTypes(this.pattern)
        return Pattern.compile(handlePattern(this.pattern))
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Private methods
    // -----------------------------------------------------------------------------------------------------------------

    // extract all type and feature information from the regex tree
    private extractTypes(AnnotationPattern pattern) {
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

    // recursive method for generating regex string
    private String handlePattern(AnnotationPattern pattern) {
        switch (pattern.class) {
            case AtomicAnnotationPattern.class:
                return handlePattern((AtomicAnnotationPattern)pattern)
            case SequenceAnnotationPattern.class:
                return handlePattern((SequenceAnnotationPattern)pattern)
            case OptionAnnotationPattern.class:
                return handlePattern((OptionAnnotationPattern)pattern)
        }
    }

    // base case method for generating regex string
    private String handlePattern(AtomicAnnotationPattern pattern) {
        Class<? extends Annotation> type = pattern.type
        Map<String, String> feats = pattern.features
        String featString = featMap[type].inject('') { prefix, featName ->
            String featVal = feats.containsKey(featName) ?
                feats[featName].replaceAll(/(?<!\\)\./, "[^${LBRACK}${RBRACK}]")
                : "[^${LBRACK}${RBRACK}]*"
            prefix + "${LBRACK}${featVal}${RBRACK}"
        }
        return annotate("(?:${typeMap[type]}${featString})", pattern.name, pattern.range, false)
    }

    // recursive method for generating regex string
    private String handlePattern(SequenceAnnotationPattern pattern) {
        String result = pattern.children.inject(''){prefix,rest -> "${prefix}${this.handlePattern(rest)}"}
        return "(?:${result})"
    }

    // recursive method for generating regex string
    private String handlePattern(OptionAnnotationPattern pattern) {
        String first = this.handlePattern(pattern.children.remove(0))
        String result = pattern.children.inject(first){prefix,rest -> "${prefix}|${this.handlePattern(rest)}"}
        return "(?:${result})"
    }

    // add name, group, and quantifier information to regex
    private String annotate(final String baseRegex, final String name, final IntRange range, final boolean addGroup) {
        String result = baseRegex
        if (range) { result = "${result}{${range.min()},${range.max()}}"}
        if (name) { result = "(?<${name}>${result})" }
        else if (addGroup) { result = "(?:${result})"}
        return result
    }

    private String replaceRegexDot(String regex) {
        regex.replaceAll(/(?<!\\)\./, '[^‹›]')
    }
}
