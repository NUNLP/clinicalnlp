package clinicalnlp.pattern

import org.apache.uima.jcas.tcas.Annotation

import static clinicalnlp.pattern.AnnotationRegex.LBRACK
import static clinicalnlp.pattern.AnnotationRegex.RBRACK

class AnnotationStringGenerator {
    private AnnotationStringGenerator() {}

    static String genSequenceString(AnnotationRegex regex, List<? extends Annotation> sequence) {
        String result = sequence.inject('') { String resultPrefix, Annotation ann ->
            String typeCode = regex.typeMap[ann.class]
            String featString = regex.featMap[ann.class].inject('') { featPrefix, featName ->
                String featVal = (featName == 'text' ? ann.coveredText : ann."${featName}")
                featPrefix + "${LBRACK}${featVal}${RBRACK}"
            }
            resultPrefix + typeCode + featString
        }
        return result
    }
}
