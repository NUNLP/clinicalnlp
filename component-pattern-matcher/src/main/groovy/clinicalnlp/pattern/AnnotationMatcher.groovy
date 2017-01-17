package clinicalnlp.pattern

import org.apache.uima.jcas.tcas.Annotation

/**
 * AnnotationMatcher class definition
 */
class AnnotationMatcher implements Iterator {
    AnnotationPattern pattern
    List<? extends Annotation> sequence

    AnnotationMatcher(AnnotationPattern pattern, List<? extends Annotation> sequence) {
        this.pattern = pattern
        this.sequence = sequence
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
