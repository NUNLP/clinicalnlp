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
        return Pattern.compile(handlePattern(this.pattern))
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Private methods
    // -----------------------------------------------------------------------------------------------------------------

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

    private String handlePattern(AtomicAnnotationPattern pattern) {
        log.info 'atomic pattern'
        Class<? extends Annotation> type = pattern.type
        if (!typeMap.keySet().contains(type)) {
            typeMap[type] = (char)this.typeCode
            typeCode++
        }
        return annotate("${typeMap[type]}", pattern.name, pattern.range)
    }

    private String handlePattern(SequenceAnnotationPattern pattern) {
        log.info 'sequence pattern'
        String result = pattern.children.inject(''){prefix,rest -> "${prefix}${this.handlePattern(rest)}"}
        return "(?:${result})"
    }

    private String handlePattern(OptionAnnotationPattern pattern) {
        log.info 'option pattern'
        String first = this.handlePattern(pattern.children.remove(0))
        String result = pattern.children.inject(first){prefix,rest -> "${prefix}|${this.handlePattern(rest)}"}
        return "(?:${result})"
    }

    private String annotate(final String baseString, final String name, final IntRange range) {
        String result = baseString
        if (range) { result = "${result}{${range.min()},${range.max()}}"}
        if (name) { result = "(?<${name}>${result})" }
        else { result = "(?:${result})"}
        return result
    }
}
