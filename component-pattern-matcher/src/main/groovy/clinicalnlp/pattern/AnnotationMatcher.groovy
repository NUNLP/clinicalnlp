package clinicalnlp.pattern

import org.apache.uima.jcas.tcas.Annotation

/**
 * AnnotationMatcher class definition
 */
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
