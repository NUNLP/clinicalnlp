package clinicalnlp.pattern

import org.apache.uima.jcas.tcas.Annotation

/**
 * AnnotationPattern class definition
 */
abstract class AnnotationPattern {
    //------------------------------------------------------------------------------------------------------------------
    // Factory Methods
    //------------------------------------------------------------------------------------------------------------------

    static $A = { final Class<? extends Annotation> type, final Map<String, String> features = [:] ->
        return new AtomicAnnotationPattern(type, features)
    }

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


    //------------------------------------------------------------------------------------------------------------------
    // Instance Fields
    //------------------------------------------------------------------------------------------------------------------

    String name
    IntRange range
    Boolean lookAhead
    Boolean positive
    Boolean lookBehind
    Boolean greedy

    //------------------------------------------------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------------------------------------------------

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
     * @param positive
     * @return
     */
    AnnotationPattern leftShift(Boolean positive) {
        this.lookBehind = true
        this.positive = positive
        return this
    }

    /**
     *
     * @param positive
     * @return
     */
    AnnotationPattern rightShift(Boolean positive) {
        this.lookAhead = true
        this.positive = positive
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
 * AtomicAnnotationPattern class definition
 */
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
 * SequenceAnnotationPattern class definition
 */
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
 * OptionAnnotationPattern class definition
 */
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
