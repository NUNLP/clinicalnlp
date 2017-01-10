package clinicalnlp.pattern

import groovy.util.logging.Log4j
import org.apache.uima.jcas.tcas.Annotation

@Log4j
class AnnotationPattern {

    // -----------------------------------------------------------------------------------------------------------------
    // AnnotationMatcher class
    // -----------------------------------------------------------------------------------------------------------------
    class AnnotationMatcher implements Iterator {
        Annotation annotation

        AnnotationMatcher(Annotation annotation) {
            this.annotation = annotation
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

    // -----------------------------------------------------------------------------------------------------------------
    // instance fields
    // -----------------------------------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------------------------------
    // public methods
    // -----------------------------------------------------------------------------------------------------------------

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
    AnnotationMatcher matcher(Annotation annotation) {
        return new AnnotationMatcher(annotation)
    }


    // -----------------------------------------------------------------------------------------------------------------
    // private methods
    // -----------------------------------------------------------------------------------------------------------------

}
