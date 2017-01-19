package clinicalnlp.pattern

import clinicalnlp.pattern.ae.TestAnnotator
import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
import gov.va.vinci.leo.window.types.Window
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.PropertyConfigurator
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.fit.pipeline.SimplePipeline
import org.apache.uima.jcas.JCas
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import javax.lang.model.element.Name

import static clinicalnlp.pattern.AnnotationPattern.$A
import static clinicalnlp.pattern.AnnotationPattern.$N
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription

@Log4j
class AnnotationRegexMatcherTests {
    @BeforeClass
    static void setupClass() {
        def config = new ConfigSlurper().parse(
            AnnotationRegexMatcherTests.class.getResource('/config.groovy').text)
        PropertyConfigurator.configure(config.toProperties())
        Class.forName('clinicalnlp.dsl.UIMA_DSL')
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
        AnnotationRegex regex = new AnnotationRegex($N('tokens', $A(Token)(1,3)))

        //--------------------------------------------------------------------------------------------------------------
        // Create a sequence of annotations and a matcher
        //--------------------------------------------------------------------------------------------------------------
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0], [Token])
        List sequence = sequencer.iterator().next()
        AnnotationRegexMatcher matcher = regex.matcher(sequence)
        matcher.each { Binding binding ->
            assert binding.hasVariable('tokens')
        }
        matcher = regex.matcher(sequence)

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
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
        AnnotationRegexMatcher matcher = regex.matcher(sequencer.iterator().next())

        //--------------------------------------------------------------------------------------------------------------
        // Validate the matches
        //--------------------------------------------------------------------------------------------------------------
        int bindingCount = 0
        matcher.each() { Binding b ->
            bindingCount++
        }
        assert bindingCount == 1
        matcher = regex.matcher(sequencer.iterator().next())
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
}
