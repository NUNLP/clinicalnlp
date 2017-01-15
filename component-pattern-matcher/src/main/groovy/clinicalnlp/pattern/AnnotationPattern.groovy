package clinicalnlp.pattern

import groovy.util.logging.Log4j
import org.apache.uima.jcas.tcas.Annotation

/**
 *
 */
@Log4j
abstract class AnnotationPattern {
    /**
     * Create an annotation matcher
     * @param sequence
     * @return
     */
    AnnotationMatcher matcher(List<? extends Annotation> sequence) {
        return new AnnotationMatcher(sequence)
    }

    /**
     * Factory method for atom
     */
    static $A = { final Class<? extends Annotation> type, final Map<String, String> features ->
        return new AtomicAnnotationPattern(type, features)
    }

    /**
     * Factory method for named groups
     */
    static $N = { String name, AnnotationPattern pattern ->
        pattern.name = name
        return pattern
    }

    String name
    IntRange range

    /**
     * Constructor
     * @param regex
     */
    protected AnnotationPattern() {
    }

    AnnotationPattern multiply(IntRange range) {
        this.range = range
        return this
    }

    AnnotationPattern positive() {
    }

    abstract AnnotationPattern and(AnnotationPattern pattern)

    abstract AnnotationPattern or(AnnotationPattern pattern)
}

/**
 *
 */
@Log4j
class AtomicAnnotationPattern extends AnnotationPattern {
    final Class<? extends Annotation> type
    final Map<String, String> features

    protected AtomicAnnotationPattern(Class<? extends Annotation> type, Map<String, String> features) {
        this.type = type
        this.features = features
    }

    @Override
    AnnotationPattern and(AnnotationPattern pattern) {
        SequenceAnnotationPattern seq = new SequenceAnnotationPattern()
        seq.children << this
        seq.children << pattern
        return seq
    }

    @Override
    AnnotationPattern or(AnnotationPattern pattern) {
        OptionsAnnotationPattern options = new OptionsAnnotationPattern()
        options.children << this
        options.children << pattern
        return options
    }
}

/**
 *
 */
@Log4j
class SequenceAnnotationPattern extends AnnotationPattern {
    List<AnnotationPattern> children = new ArrayList<AnnotationPattern>()

    protected SequenceAnnotationPattern() {
    }

    @Override
    AnnotationPattern and(AnnotationPattern pattern) {
        this.children << pattern
        return this
    }

    @Override
    AnnotationPattern or(AnnotationPattern pattern) {
        OptionsAnnotationPattern options = new OptionsAnnotationPattern()
        options.children << this
        options.children << pattern
        return options
    }
}

/**
 *
 */
@Log4j
class OptionsAnnotationPattern extends AnnotationPattern {
    List<AnnotationPattern> children = new ArrayList<AnnotationPattern>()

    protected OptionsAnnotationPattern() {
    }

    @Override
    AnnotationPattern and(AnnotationPattern pattern) {
        SequenceAnnotationPattern seq = new SequenceAnnotationPattern()
        seq.children << this
        seq.children << pattern
        return seq
    }

    @Override
    AnnotationPattern or(AnnotationPattern pattern) {
        this.children << pattern
        return this
    }
}