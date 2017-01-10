package clinicalnlp.pattern

import org.apache.uima.jcas.tcas.Annotation


class AnnotationSequencer {
    Annotation cover
    Collection<Class<? extends  Annotation>> types

    AnnotationSequencer(Annotation cover, Collection<Class<? extends  Annotation>> types) {
        this.cover = cover
        this.types = types
    }
}
