package clinicalnlp.pattern

import groovy.util.logging.Log4j
import org.apache.uima.jcas.tcas.Annotation

import java.util.regex.Pattern

@Log4j
class AnnotationPattern {

    // -----------------------------------------------------------------------------------------------------------------
    // static fields
    // -----------------------------------------------------------------------------------------------------------------
    static final Integer INIT_TYPE_CODE = 0x2460
    static final Character LBRACK = (char)0x2039
    static final Character RBRACK = (char)0x203A

    // -----------------------------------------------------------------------------------------------------------------
    // instance fields
    // -----------------------------------------------------------------------------------------------------------------
    Integer groupCounter = 0
    Set<String> groupNames = []
    Map<Class<Annotation>, Character> annClassToCodeMap = [:]
    Map<Class<Annotation>, String> annClassToFeatMap = [:]
    List<Character> typeCodes = []
    Pattern pattern

    // -----------------------------------------------------------------------------------------------------------------
    // AnnotationMatcher class
    // -----------------------------------------------------------------------------------------------------------------
    class AnnotationMatcher implements Iterator {

        AnnotationMatcher(List<? extends Annotation> sequence) {
            this.mapToString(sequence)
        }

        @Override
        boolean hasNext() {
            return false
        }

        @Override
        Object next() {
            return null
        }

        String mapToString(List<? extends Annotation> sequence) {
            String code = AnnotationPattern.this.annClassToCodeMap
            println "Code: $code"
            String feat = AnnotationPattern.this.annClassToFeatMap
            println "Feat: $feat"
        }
    }

    /**
     * Constructor
     * @param regex
     */
    AnnotationPattern(final Node regex) {
        // create mapping indices
        Integer currTypeCode = AnnotationPattern.INIT_TYPE_CODE
        List<Node> includeTypes = regex.include; assert includeTypes
        includeTypes.each { Node n ->
            Character typeCode = (char)currTypeCode++
            typeCodes << typeCode
            this.annClassToCodeMap[n.@type] = typeCode
            this.annClassToFeatMap[n.@type] = n.@feats
        }

        this.annClassToCodeMap.each { println it }

        // generate the pattern from the elements
        this.pattern = genPattern(regex)
    }

    /**
     * Create an annotation matcher
     * @param coveringAnn
     * @return
     */
    AnnotationMatcher matcher(List<? extends Annotation> sequence) {
        return new AnnotationMatcher(sequence)
    }

    // -----------------------------------------------------------------------------------------------------------------
    // private methods
    // -----------------------------------------------------------------------------------------------------------------

    Pattern genPattern(Node regex) {
        return null
    }
}
