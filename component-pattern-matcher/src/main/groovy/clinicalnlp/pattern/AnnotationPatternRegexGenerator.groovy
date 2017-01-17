package clinicalnlp.pattern

import groovy.util.logging.Log4j
import org.apache.uima.jcas.tcas.Annotation

import java.util.regex.Pattern

// TODO: implement lookahead and lookbehind, both negative and positive
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
        this.extractTypes(this.pattern)
        return Pattern.compile(this.genRegexString(this.pattern))
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
            prefix + "${LBRACK}${featVal}${RBRACK}"
        }
        return decorate("${typeMap[type]}${featString}", pattern.name, pattern.range, false)
    }

    // Recursive method for generating regex string from a SequenceAnnotationPattern
    private String genRegexString(SequenceAnnotationPattern pattern) {
        String result = pattern.children.inject(''){prefix,rest -> "${prefix}${this.genRegexString(rest)}"}
        return decorate(result, pattern.name, pattern.range, true)
    }

    // Recursive method for generating regex string from an OptionAnnotationPattern
    private String genRegexString(OptionAnnotationPattern pattern) {
        String first = this.genRegexString(pattern.children.remove(0))
        String result = pattern.children.inject(first){prefix,rest -> "${prefix}|${this.genRegexString(rest)}"}
        return decorate(result, pattern.name, pattern.range, true)
    }

    // Add name, group, and quantifier information to regex
    private String decorate(final String baseRegex, final String name, final IntRange range, final boolean addGroup) {
        String result = baseRegex
        if (range) { result = "(?:${result}){${range.min()},${range.max()}}"}
        if (name) { result = "(?<${name}>${result})" }
        else if (addGroup) { result = "(?:${result})"}
        return result
    }
}
