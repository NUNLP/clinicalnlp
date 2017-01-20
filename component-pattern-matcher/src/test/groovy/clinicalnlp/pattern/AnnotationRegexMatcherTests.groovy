package clinicalnlp.pattern

import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Segment
import clinicalnlp.types.TextSpan
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
import gov.va.vinci.leo.window.types.Window
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.PropertyConfigurator
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.fit.pipeline.SimplePipeline
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.util.regex.Matcher

import static clinicalnlp.pattern.AnnotationPattern.$A
import static clinicalnlp.pattern.AnnotationPattern.$N
import static clinicalnlp.pattern.AnnotationPattern.$LA
import static clinicalnlp.pattern.AnnotationPattern.$LB
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription

@Log4j
class AnnotationRegexMatcherTests {

    /**
     * TestAnnotator
     */
    static class TestAnnotator extends JCasAnnotator_ImplBase {
        @Override
        void process(JCas jcas) throws AnalysisEngineProcessException {
            String text = jcas.documentText
            jcas.create(type: Segment, begin: 0, end: text.length())
            jcas.create(type: Sentence, begin: 0, end: text.length())
            jcas.create(type: Window, begin: 0, end: text.length())
            Matcher m = (text =~ /\b\w+\b/)
            m.each {
                Token t = jcas.create(type: Token, begin: m.start(0), end: m.end(0))
                switch (t.coveredText) {
                    case 'Tubular': t.pos = 'JJ'; t.lemma = 'tube'; t.stem = 'Tub'; break;
                    case 'adenoma': t.pos = 'NN'; break;
                    case 'was': t.pos = 'AUX'; t.lemma = 'is'; break;
                    case 'seen': t.pos = 'VBN'; t.lemma = 'see'; t.stem = 'see'; break;
                    case 'in': t.pos = 'IN'; t.lemma = 'in'; break;
                    case 'the': t.pos = 'DT'; t.lemma = 'the'; break;
                    case 'sigmoid': t.pos = 'JJ'; break;
                    case 'colon': t.pos = 'NN'; break;
                    case '.': t.pos = 'PUNC'; break;
                }
            }
            m = (text =~ /(?i)\b(sigmoid\s+colon)|(tubular\s+adenoma)|(polyps)\b/)
            m.each {
                NamedEntityMention nem = jcas.create(type: NamedEntityMention, begin: m.start(0), end: m.end(0))
                switch (nem.coveredText) {
                    case 'Tubular adenoma': nem.code = 'C01'; break;
                    case 'sigmoid colon': nem.code = 'C02'; break;
                    case 'polyps': nem.code = 'C03'; break;
                }
            }
        }
    }

    @BeforeClass
    static void setupClass() {
        def config = new ConfigSlurper().parse(
            AnnotationRegexMatcherTests.class.getResource('/config.groovy').text)
        PropertyConfigurator.configure(config.toProperties())
        Class.forName('clinicalnlp.dsl.DSL')
    }

    JCas jcas;

    @Before
    void setUp() throws Exception {
        log.setLevel(Level.INFO)
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(TestAnnotator))
        }
        AnalysisEngine engine = builder.createAggregate()
        String text = 'Tubular adenoma was seen in the sigmoid colon'
        this.jcas = engine.newJCas()
        this.jcas.setDocumentText(text)
        SimplePipeline.runPipeline(this.jcas, engine)
    }

    @Test
    void testMatch1() {
        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationRegex regex = new AnnotationRegex($N('tokens', $A(Token)(1,3)))

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0], [Token])
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        matcher.each { Binding binding ->
            assert binding.hasVariable('tokens')
        }
        // create a new matcher to start over
        matcher = regex.matcher(sequencer.first())
        assert matcher.hasNext()
        Binding binding = matcher.next()
        List<Token> tokens = binding.getVariable('tokens')
        assert tokens.size() == 3
        assert tokens[0].coveredText == 'Tubular'
        assert tokens[1].coveredText == 'adenoma'
        assert tokens[2].coveredText == 'was'
        assert matcher.hasNext()
        binding = matcher.next()
        tokens = binding.getVariable('tokens')
        assert tokens.size() == 3
        assert tokens[0].coveredText == 'seen'
        assert tokens[1].coveredText == 'in'
        assert tokens[2].coveredText == 'the'
        assert matcher.hasNext()
        binding = matcher.next()
        tokens = binding.getVariable('tokens')
        assert tokens.size() == 2
        assert tokens[0].coveredText == 'sigmoid'
        assert tokens[1].coveredText == 'colon'
        assert !matcher.hasNext()
    }

    @Test
    void testMatch2() {
        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance
        //--------------------------------------------------------------------------------------------------------------
        AnnotationRegex regex = new AnnotationRegex(
            $N('finding', $A(NamedEntityMention, [text:/(?i)tubular\s+adenoma/])) &
                $A(Token)(0,2) &
                $N('seen', $A(Token, [text:/seen/, pos:/V.*/])) &
                $N('tokens', $A(Token, [text:/in|the/])(0,2)) &
                $N('site', $A(NamedEntityMention, [text:/(?i)Sigmoid\s+colon/, code:/C.2/]))
        )

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0],
            [NamedEntityMention, Token])
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        assert matcher.hasNext()
        Binding binding = matcher.next()
        assert binding != null
        assert !matcher.hasNext()
        NamedEntityMention finding = binding.getVariable('finding')[0]
        assert finding != null
        assert finding.coveredText ==~ /(?i)tubular\s+adenoma/
        NamedEntityMention site = binding.getVariable('site')[0]
        assert site != null
        assert site.coveredText ==~ /sigmoid\s+colon/
        Token token = binding.getVariable('tokens')[0]
        assert token != null
        assert token.coveredText == 'in'
        token = binding.getVariable('tokens')[1]
        assert token != null
        assert token.coveredText == 'the'
    }

    @Test
    void testGroups() {
        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationRegex regex = new AnnotationRegex(
            $N('finding', $A(NamedEntityMention, [text:/(?i)tubular\s+adenoma/]) & $A(Token)(1,5)) &
                $N('site', $A(Token)(1,3) & $A(NamedEntityMention))
        )

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0],
            [NamedEntityMention, Token])
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        assert matcher.hasNext()
        Binding binding = matcher.next()
        assert !matcher.hasNext()
        List<? extends Annotation> finding = binding.getVariable('finding')
        assert finding.size() == 4
        assert finding[0].coveredText == 'Tubular adenoma'
        assert finding[1].coveredText == 'was'
        assert finding[2].coveredText == 'seen'
        assert finding[3].coveredText == 'in'
        List<? extends Annotation> site = binding.getVariable('site')
        assert site.size() == 2
        assert site[0].coveredText == 'the'
        assert site[1].coveredText == 'sigmoid colon'
    }

    @Test
    void testUnions() {
        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationRegex regex = new AnnotationRegex(
            $A(Token)(0,5) &
                $N('nem',
                    $A(NamedEntityMention, [text:/(?i)tubular\s+adenoma/]) |
                    $A(NamedEntityMention, [text:/(?i)sigmoid\s+colon/])) &
                $A(Token)(0,5)
        )

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0],
            [NamedEntityMention, Token])
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        assert matcher.hasNext()
        Binding binding = matcher.next()
        List<? extends Annotation> nem = binding.getVariable('nem')
        assert nem.size() == 1
        assert nem[0].coveredText == 'Tubular adenoma'
        binding = matcher.next()
        nem = binding.getVariable('nem')
        assert nem.size() == 1
        assert nem[0].coveredText == 'sigmoid colon'
        assert !matcher.hasNext()
    }

    @Test
    void testPositiveLookAhead() {
        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance
        //--------------------------------------------------------------------------------------------------------------
        AnnotationRegex regex = new AnnotationRegex(
            $N('tok', $A(Token)) & +$LA($A(Token, [text:/adenoma/]))
        )

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0], [Token])
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        assert matcher.hasNext()
        Binding binding = matcher.next()
        List<? extends Annotation> tok = binding.getVariable('tok')
        assert tok.size() == 1
        assert tok[0].coveredText == 'Tubular'
        assert !matcher.hasNext()
    }

    @Test
    void testNegativeLookAhead() {
        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance
        //--------------------------------------------------------------------------------------------------------------
        AnnotationRegex regex = new AnnotationRegex(
            $N('tok', $A(Token)) & -$LA($A(Token, [text:/adenoma|seen|sigmoid|colon/]))
        )

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0], [Token])
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        assert matcher.hasNext()
        Binding binding = matcher.next()
        List<? extends Annotation> tok = binding.getVariable('tok')
        assert tok.size() == 1
        assert tok[0].coveredText == 'adenoma'
        binding = matcher.next()
        tok = binding.getVariable('tok')
        assert tok.size() == 1
        assert tok[0].coveredText == 'seen'
        binding = matcher.next()
        tok = binding.getVariable('tok')
        assert tok.size() == 1
        assert tok[0].coveredText == 'in'
        binding = matcher.next()
        tok = binding.getVariable('tok')
        assert tok.size() == 1
        assert tok[0].coveredText == 'colon'
        assert !matcher.hasNext()
    }

    @Test
    void testPositiveLookBehind() {
        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationRegex regex = new AnnotationRegex(
            +$LB($A(Token, [text:/adenoma/])) & $N('tok', $A(Token)(3,3))
        )

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0], [Token])
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        assert matcher.hasNext()
        Binding binding = matcher.next()
        List<? extends Annotation> tok = binding.getVariable('tok')
        assert tok.size() == 3
        assert tok[0].coveredText == 'was'
        assert tok[1].coveredText == 'seen'
        assert tok[2].coveredText == 'in'
        assert !matcher.hasNext()
    }

    @Test
    void testNegativeLookBehind() {
        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationRegex regex = new AnnotationRegex(
            -$LB($A(Token, [text:/adenoma|seen|sigmoid/])) & $N('tok', $A(Token))
        )

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0], [Token])
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        assert matcher.hasNext()
        Binding binding = matcher.next()
        List<? extends Annotation> tok = binding.getVariable('tok')
        assert tok.size() == 1
        assert tok[0].coveredText == 'Tubular'
        binding = matcher.next()
        tok = binding.getVariable('tok')
        assert tok.size() == 1
        assert tok[0].coveredText == 'adenoma'
        binding = matcher.next()
        tok = binding.getVariable('tok')
        assert tok.size() == 1
        assert tok[0].coveredText == 'seen'
        binding = matcher.next()
        tok = binding.getVariable('tok')
        assert tok.size() == 1
        assert tok[0].coveredText == 'the'
        binding = matcher.next()
        tok = binding.getVariable('tok')
        assert tok.size() == 1
        assert tok[0].coveredText == 'sigmoid'
        assert !matcher.hasNext()
    }

    @Test
    void testLookAround() {
        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationRegex regex = new AnnotationRegex(
            +$LB($A(Token, [text:/was/])) &
                $N('tok', $A(Token)(0,3)) &
                +$LA($A(Token, [text:/sigmoid/]))
        )

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0], [Token])
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        assert matcher.hasNext()
        Binding binding = matcher.next()
        List<? extends Annotation> tok = binding.getVariable('tok')
        assert tok.size() == 3
        assert tok[0].coveredText == 'seen'
        assert tok[1].coveredText == 'in'
        assert tok[2].coveredText == 'the'
    }

    @Test
    void testLazyQuantifier() {
        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationRegex regex = new AnnotationRegex(
            $N('tokens', $A(Token)(3,5, false))
        )

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0], [Token])
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        assert matcher.hasNext()
        Binding binding = matcher.next()
        def tokens = binding.getVariable('tokens')
        assert tokens.size() == 3
        assert matcher.hasNext()
        binding = matcher.next()
        tokens = binding.getVariable('tokens')
        assert tokens.size() == 3
        assert !matcher.hasNext()
    }

    @Test
    void testTextSpan() {
        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationRegex regex = new AnnotationRegex(
            +$LB($A(NamedEntityMention, [code:'C01'])) &
                $N('tok', $A(Token)(0,10)) &
                +$LA($A(NamedEntityMention, [code:'C02']))
        )

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0],
            [NamedEntityMention, Token])
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        assert matcher.hasNext()
        Binding binding = matcher.next()
        List<? extends Annotation> tok = binding.getVariable('tok')
        assert tok.size() == 4
        assert tok[0].coveredText == 'was'
        assert tok[1].coveredText == 'seen'
        assert tok[2].coveredText == 'in'
        assert tok[3].coveredText == 'the'
        jcas.create(type:TextSpan, begin:tok[0].begin, end:tok[3].end)
        assert jcas.select(type:TextSpan).size() == 1

        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance that looks for the TextSpan
        //--------------------------------------------------------------------------------------------------------------
        AnnotationRegex regex2 = new AnnotationRegex(
            $N('span', $A(TextSpan))
        )

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0], [TextSpan])
        matcher = regex2.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        assert matcher.hasNext()
        binding = matcher.next()
        def span = binding.getVariable('span')
        assert span.size() == 1
        assert span[0].coveredText == 'was seen in the'
        assert !matcher.hasNext()
    }

    @Test
    void testWildcards() {
        //--------------------------------------------------------------------------------------------------------------
        // Create an AnnotationRegex instance
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationRegex regex = new AnnotationRegex(
            $N('tokens', $A(Token, [pos:/(N|V|D).+/, lemma:/.+/, stem:/.*/, text:/.+/]))
        )

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0], [Token])
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.first())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        assert matcher.hasNext()
        Binding binding = matcher.next()
        List<? extends Annotation> tok = binding.getVariable('tokens')
        tok = binding.getVariable('tokens')
        assert tok.size() == 1
        assert tok[0].coveredText == 'seen'
        binding = matcher.next()
        tok = binding.getVariable('tokens')
        assert tok.size() == 1
        assert tok[0].coveredText == 'the'
        assert !matcher.hasNext()
    }
}
