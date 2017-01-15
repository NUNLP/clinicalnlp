package clinicalnlp.pattern

import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.uima.jcas.tcas.Annotation


@Log4j
class PatternElement {
    static {
        log.level = Level.INFO
    }

    PatternElement(Map args) {
    }

    static N = { Map args ->
        return new PatternElement(args)
    }

    static G = { String name, PatternElement pat ->
        return new PatternElement()
    }

    boolean evaluate(List<? extends Annotation> matchSequence) {
        return this.operation(matchSequence)
    }

    PatternElement $(PatternElement pe) {
        log.info '$'
        return new PatternElement()
    }

    PatternElement or(PatternElement b) {
        log.info 'or'
        return new PatternElement()
    }
    PatternElement and(PatternElement b) {
        log.info 'and'
        return new PatternElement()
    }
    PatternElement plus(PatternElement b) {
        log.info 'plus'
        return new PatternElement()
    }
    PatternElement multiply(PatternElement b) {
        log.info 'multiply'
        return new PatternElement()
    }
    PatternElement multiply(IntRange r) {
        log.info 'multiply'
        return new PatternElement()
    }
    PatternElement multiply(Integer i) {
        log.info 'multiply'
        return new PatternElement()
    }
    PatternElement positive(PatternElement b) {
        log.info 'positive'
        return this
    }
    PatternElement negative(PatternElement b) {
        log.info 'negative'
        return this
    }
    PatternElement leftShift(PatternElement b) {
        log.info 'leftShift'
        return this
    }
    PatternElement rightShift(PatternElement b) {
        log.info 'rightShift'
        return this
    }
}
