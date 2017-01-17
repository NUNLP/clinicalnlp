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

@Log4j
class AnnotationPatternRegexGeneratorTests {

    @BeforeClass
    static void setupClass() {
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
        AnnotationPattern pattern = $A(Token, [pos:'NN', shape:'all_caps', text:/Foo/])
        AnnotationPatternRegexGenerator generator = new AnnotationPatternRegexGenerator(pattern)
        Pattern p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?:①‹NN›‹all_caps›‹Foo›)'

        pattern = (pattern(0,3))
        generator = new AnnotationPatternRegexGenerator(pattern)
        p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?:①‹NN›‹all_caps›‹Foo›){0,3}'

        pattern = $N('n1', pattern)
        generator = new AnnotationPatternRegexGenerator(pattern)
        p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?<n1>(?:①‹NN›‹all_caps›‹Foo›){0,3})'
    }

    @Test
    void testSequencePatterns() {
        AnnotationPattern pattern1 = $A(Token, [pos:'NN', text:/.+/])
        AnnotationPattern pattern2 = $A(Sentence, [text:/(?i)Bar/])
        AnnotationPattern pattern3 = $A(NamedEntityMention, [text:/Bar/])
        AnnotationPattern pattern4 = $A(Token, [text:/Baz/])

        AnnotationPattern pattern = (pattern1 & pattern2 & pattern3 & pattern2 & pattern4)
        pattern = $N('group1', pattern(0,3))
        AnnotationPatternRegexGenerator generator = new AnnotationPatternRegexGenerator(pattern)
        Pattern p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?:(?:①‹NN›‹[^‹›]+›)(?:②‹(?i)Bar›)(?:③‹Bar›)(?:②‹(?i)Bar›)(?:①‹[^‹›]*›‹Baz›))'
    }

    @Test
    void testOptionPatterns() {
        AnnotationPattern pattern1 = $A(Token, [text:/Foo/])
        AnnotationPattern pattern2 = $A(Sentence, [text:/Bar/])
        AnnotationPattern pattern3 = $A(NamedEntityMention, [text:/Bar/])
        AnnotationPattern pattern = (pattern1 | pattern2 | pattern3 | pattern2 | pattern1)
        AnnotationPatternRegexGenerator generator = new AnnotationPatternRegexGenerator(pattern)
        Pattern p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?:(?:①‹Foo›)|(?:②‹Bar›)|(?:③‹Bar›)|(?:②‹Bar›)|(?:①‹Foo›))'
    }

    @Test
    void testMixedPatterns() {
        AnnotationPattern pattern1 = $A(Token, [pos:'NN'])
        AnnotationPattern pattern2 = $A(Sentence, [modality:'Assertion'])
        AnnotationPattern pattern3 = $A(NamedEntityMention, [type:'Disease'])
        AnnotationPattern pattern = (pattern1 & pattern2 | pattern3 & pattern2 | pattern1)
        AnnotationPatternRegexGenerator generator = new AnnotationPatternRegexGenerator(pattern)

        Pattern p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?:(?:(?:①‹NN›)(?:②‹Assertion›))|(?:(?:③‹Disease›)(?:②‹Assertion›))|(?:①‹NN›))'

        pattern = (pattern1 & (pattern2 | pattern3) & pattern2 | pattern1)
        generator = new AnnotationPatternRegexGenerator(pattern)
        p = generator.genRegExPattern()
        log.info "Pattern: ${p.toString()}"
        assert p.toString() == '(?:(?:(?:①‹NN›)(?:(?:②‹Assertion›)|(?:③‹Disease›))(?:②‹Assertion›))|(?:①‹NN›))'
    }
}
