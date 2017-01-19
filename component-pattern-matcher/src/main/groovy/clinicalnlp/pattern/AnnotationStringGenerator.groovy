package clinicalnlp.pattern

import org.apache.uima.jcas.tcas.Annotation

import static clinicalnlp.pattern.AnnotationRegex.LBRACK
import static clinicalnlp.pattern.AnnotationRegex.RBRACK

class AnnotationStringGenerator {
    private AnnotationStringGenerator() {}

    static Tuple2<Map<Integer, Annotation>, String> genSequenceString(AnnotationRegex regex, List<? extends Annotation> sequence) {
        Map<Integer, Annotation> indexMap = [:]

        String result = sequence.inject('') { String resultPrefix, Annotation ann ->
            String typeCode = regex.typeMap[ann.class]
            String featString = regex.featMap[ann.class].inject('') { featPrefix, featName ->
                String featVal = (featName == 'text' ? ann.coveredText : ann."${featName}")
                featPrefix + "${LBRACK}${featVal}${RBRACK}"
            }
            indexMap[resultPrefix.size()] = ann
            resultPrefix + typeCode + featString
        }

        return new Tuple2<Map<Integer, Annotation>, String>(indexMap, result)
    }
}
