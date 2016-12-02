package clinicalnlp.dsl

import org.apache.uima.jcas.tcas.Annotation
import org.codehaus.groovy.runtime.StringGroovyMethods

import java.util.regex.MatchResult
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * AnnotationMatchResult class
 * @author Will Thompson
 *
 */
class AnnotationMatchResult implements MatchResult {
    // -----------------------------------------------------------------------------------------------------------------
    // AnnotationMatcherIterator
    // -----------------------------------------------------------------------------------------------------------------
    class AnnotationMatcherIterator implements Iterator {
        Iterator iterator
        Object result

        AnnotationMatcherIterator() {
            iterator = StringGroovyMethods.iterator(AnnotationMatchResult.this.matcher)
        }

        boolean hasNext() {
            return this.iterator.hasNext()
        }

        Object next() {
            this.result = this.iterator.next()
            AnnotationMatchResult outer = AnnotationMatchResult.this
            return outer
        }

        void remove() {
            this.iterator.remove()
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Fields
    // -----------------------------------------------------------------------------------------------------------------
    public final Annotation annotation
    public final Matcher matcher

    // -----------------------------------------------------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------------------------------------------------
    AnnotationMatchResult(Pattern pattern, Annotation annotation) {
        this.annotation = annotation
        this.matcher = pattern.matcher(this.annotation.coveredText)
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------------------------------------------------------
    
    int start() {
        return (this.matcher.start() + this.annotation.begin)
    }

    int start(int group) {
        return (this.matcher.start(group) + this.annotation.begin)
    }

    int start(String name) {
        return (this.matcher.start(name) + this.annotation.begin)
    }

    int end() {
        return (this.matcher.end() + this.annotation.begin)
    }

    int end(int group) {
        return (this.matcher.end(group) + this.annotation.begin)
    }
    
    int end(String name) {
        return (this.matcher.end(name) + this.annotation.begin)
    }

    String group() {
        return this.matcher.group()
    }

    String group(String name) {
        return this.matcher.group(name)
    }
    
    String group(int group) {
        return this.matcher.group(group)
    } 

    int groupCount() {
        return this.matcher.groupCount()
    }
    
    Iterator iterator() {
        return new AnnotationMatcherIterator()
    }
}
