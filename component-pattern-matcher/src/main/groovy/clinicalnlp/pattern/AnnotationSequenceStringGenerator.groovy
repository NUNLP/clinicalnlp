package clinicalnlp.pattern

import org.apache.uima.jcas.tcas.Annotation


class AnnotationSequenceStringGenerator {
    private AnnotationSequenceStringGenerator() {}

    static String genSequenceString(AnnotationPattern pattern, List<? extends Annotation> sequence) {
        String result = ''

        sequence.each { Annotation ann ->
            ////Character code = pattern.typeMap
        }


        return result
    }
}
