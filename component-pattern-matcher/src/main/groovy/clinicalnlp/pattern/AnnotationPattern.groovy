package clinicalnlp.pattern

import groovy.util.logging.Log4j
import org.apache.uima.jcas.tcas.Annotation

/**
 * AnnotationPattern class definition
 */
@Log4j
abstract class AnnotationPattern {
    /**
     * Factory method for atom
     */
    static $A = { final Class<? extends Annotation> type, final Map<String, String> features = [:] ->
        return new AtomicAnnotationPattern(type, features)
    }

    /**
     * Factory method for named groups
     */
    static $N = { String name, AnnotationPattern pattern ->
        pattern.name = name
        return pattern
    }

    static $LA = { AnnotationPattern pattern ->
        pattern.lookAhead = true
        pattern.lookBehind = false
        return pattern
    }

    static $LB = { AnnotationPattern pattern ->
        pattern.lookAhead = false
        pattern.lookBehind = true
        return pattern
    }


    String name
    IntRange range
    Boolean lookAhead
    Boolean positive
    Boolean lookBehind
    Boolean greedy

    /**
     * Constructor
     * @param regex
     */
    protected AnnotationPattern() {
        this.lookAhead = false
        this.lookBehind = false
        this.positive = true
    }

    /**
     *
     * @param min
     * @param max
     * @return
     */
    AnnotationPattern call(Integer min, Integer max, Boolean greedy=true) {
        this.range = (min..max)
        this.greedy = greedy
        return this
    }

    /**
     *
     * @return
     */
    AnnotationPattern positive() {
        this.positive = true
        return this
    }

    /**
     *
     * @return
     */
    AnnotationPattern negative() {
        this.positive = false
        return this
    }

    /**
     *
     * @param positve
     * @return
     */
    AnnotationPattern leftShift(Boolean positive) {
        println "leftShift: ${positive}"
        return this
    }

    /**
     *
     * @param positve
     * @return
     */
    AnnotationPattern rightShift(Boolean positive) {
        println "rightShift: ${positive}"
        return this
    }

    /**
     *
     * @param pattern
     * @return
     */
    abstract AnnotationPattern and(AnnotationPattern pattern)

    /**
     *
     * @param pattern
     * @return
     */
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
        OptionAnnotationPattern options = new OptionAnnotationPattern()
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
        if (this.name || pattern.name) {
            SequenceAnnotationPattern seq = new SequenceAnnotationPattern()
            seq.children << this
            seq.children << pattern
            return seq
        }
        else {
            this.children << pattern
            return this
        }
    }

    @Override
    AnnotationPattern or(AnnotationPattern pattern) {
        OptionAnnotationPattern options = new OptionAnnotationPattern()
        options.children << this
        options.children << pattern
        return options
    }
}

/**
 *
 */
@Log4j
class OptionAnnotationPattern extends AnnotationPattern {
    List<AnnotationPattern> children = new ArrayList<AnnotationPattern>()

    protected OptionAnnotationPattern() {
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
        if (this.name || pattern.name) {
            OptionAnnotationPattern opts = new OptionAnnotationPattern()
            opts.children << this
            opts.children << pattern
            return opts
        }
        else {
            this.children << pattern
            return this
        }
    }
}
