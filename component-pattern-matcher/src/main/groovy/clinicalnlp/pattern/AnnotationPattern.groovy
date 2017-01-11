package clinicalnlp.pattern

import groovy.util.logging.Log4j
import org.apache.uima.jcas.tcas.Annotation

@Log4j
class AnnotationPattern {

    // -----------------------------------------------------------------------------------------------------------------
    // AnnotationMatcher class
    // -----------------------------------------------------------------------------------------------------------------
    class AnnotationMatcher implements Iterator {

        AnnotationMatcher(List<? extends Annotation> sequence) {
        }

        @Override
        boolean hasNext() {
            return false
        }

        @Override
        Object next() {
            return null
        }
    }

    /**
     * Constructor
     * @param regex
     */
    AnnotationPattern(final Node regex) {
    }

    /**
     * Create an annotation matcher
     * @param coveringAnn
     * @return
     */
    AnnotationMatcher matcher(List<? extends Annotation> sequence) {
        return new AnnotationMatcher(sequence)
    }
}
