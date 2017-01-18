package clinicalnlp.pattern

import org.apache.uima.jcas.tcas.Annotation

class AnnotationRegexMatcher implements Iterator {
    AnnotationRegex regex
    List<? extends Annotation> sequence

    AnnotationRegexMatcher(AnnotationRegex regex, List<? extends Annotation> sequence) {
        this.regex = regex
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
