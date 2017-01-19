package clinicalnlp.pattern

import clinicalnlp.pattern.ae.TestAnnotator
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
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
    void testBasicPatternMatch() {
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Sentence)[0], [Token])
        List sequence = sequencer.iterator().next()
        AnnotationRegex regex = new AnnotationRegex($N('tokens', $A(Token)(1,3)))
        AnnotationRegexMatcher matcher = regex.matcher(sequence)
        matcher.each { Binding binding ->
            assert binding.hasVariable('tokens')
        }


    }
}
