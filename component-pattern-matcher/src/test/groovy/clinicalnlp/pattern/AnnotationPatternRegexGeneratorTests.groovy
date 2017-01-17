package clinicalnlp.pattern

import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.PropertyConfigurator
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.util.regex.Pattern

import static clinicalnlp.pattern.AnnotationPattern.$A
import static clinicalnlp.pattern.AnnotationPattern.$N


/**
 * TODO: create type code map
 * TODO: create feature map
 * TODO: add boundary characters,
 * TODO: transform embedded regex so all non-escaped '.' chars are transformed to negated class
 */
@Log4j
class AnnotationPatternRegexGeneratorTests {

    @BeforeClass
    static void setupClass() {
        //this.getClass().getResource( '/datatest/a.xml' ).text
        def config = new ConfigSlurper().parse(
            AnnotationPatternRegexGeneratorTests.class.getResource('/config.groovy').text)
        PropertyConfigurator.configure(config.toProperties())
    }

    @Before
    void setUp() throws Exception {
        log.setLevel(Level.INFO)
    }

    @Test
    void testAtomicPatterns() {
        AnnotationPattern pattern = $A(Token, [pos:'NN', text:'/Foo/'])
        AnnotationPatternRegexGenerator generator = new AnnotationPatternRegexGenerator(pattern)
        Pattern p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?:①)'

        pattern = (pattern * (0..3))
        generator = new AnnotationPatternRegexGenerator(pattern)
        p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?:①{0,3})'

        pattern = $N('n1', pattern)
        generator = new AnnotationPatternRegexGenerator(pattern)
        p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?<n1>①{0,3})'
    }

    @Test
    void testSequencePatterns() {
        AnnotationPattern pattern1 = $A(Token, [pos:'NN', text:'/Foo/'])
        AnnotationPattern pattern2 = $A(Sentence, [pos:'VB', text:'/Bar/'])
        AnnotationPattern pattern3 = $A(NamedEntityMention, [pos:'VB', text:'/Bar/'])
        AnnotationPattern pattern = (pattern1 & pattern2 & pattern3 & pattern2 & pattern1)
        AnnotationPatternRegexGenerator generator = new AnnotationPatternRegexGenerator(pattern)
        Pattern p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?:(?:①)(?:②)(?:③)(?:②)(?:①))'
    }

    @Test
    void testOptionPatterns() {
        AnnotationPattern pattern1 = $A(Token, [pos:'NN', text:'/Foo/'])
        AnnotationPattern pattern2 = $A(Sentence, [pos:'VB', text:'/Bar/'])
        AnnotationPattern pattern3 = $A(NamedEntityMention, [pos:'VB', text:'/Bar/'])
        AnnotationPattern pattern = (pattern1 | pattern2 | pattern3 | pattern2 | pattern1)
        AnnotationPatternRegexGenerator generator = new AnnotationPatternRegexGenerator(pattern)
        Pattern p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?:(?:①)|(?:②)|(?:③)|(?:②)|(?:①))'
    }

    @Test
    void testMixedPatterns() {
        AnnotationPattern pattern1 = $A(Token, [pos:'NN', text:'/Foo/'])
        AnnotationPattern pattern2 = $A(Sentence, [pos:'VB', text:'/Bar/'])
        AnnotationPattern pattern3 = $A(NamedEntityMention, [pos:'VB', text:'/Bar/'])
        AnnotationPattern pattern = (pattern1 & pattern2 | pattern3 & pattern2 | pattern1)
        AnnotationPatternRegexGenerator generator = new AnnotationPatternRegexGenerator(pattern)

        Pattern p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?:(?:(?:①)(?:②))|(?:(?:③)(?:②))|(?:①))'

        pattern = (pattern1 & (pattern2 | pattern3) & pattern2 | pattern1)
        generator = new AnnotationPatternRegexGenerator(pattern)
        p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?:(?:(?:①)(?:(?:②)|(?:③))(?:②))|(?:①))'
    }
}
