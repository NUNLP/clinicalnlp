package clinicalnlp.pattern

import clinicalnlp.pattern.ae.TestAnnotator
import clinicalnlp.types.Token
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

import static AnnotationStringGenerator.genSequenceString
import static clinicalnlp.pattern.AnnotationPattern.$A
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription

@Log4j
class AnnotationStringGeneratorTests {

    @BeforeClass
    static void setupClass() {
        def config = new ConfigSlurper().parse(
            AnnotationRegexTests.class.getResource('/config.groovy').text)
        PropertyConfigurator.configure(config.toProperties())
        Class.forName('clinicalnlp.dsl.UIMA_DSL')
    }

    JCas jcas

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
    void testSequenceStringGeneration() {
        AnnotationRegex regex = new AnnotationRegex($A(Token, [text:/.+/, pos:/.+/])(11,11))
        AnnotationSequencer sequencer = new AnnotationSequencer(this.jcas.select(type:Window)[0], [Token])
        def result = genSequenceString(regex, sequencer.iterator().next())
        log.info "Sequence string: ${result.second}"
        assert result.second ==
            '①‹JJ›‹Tubular›①‹NN›‹adenoma›①‹AUX›‹was›①‹VBN›‹seen›①‹IN›‹in›①‹DT›‹the›①‹JJ›‹sigmoid›①‹NN›‹colon›'
        result.first.keySet().each {
            assert result.second[it] == '①'
            assert result.first[it] instanceof Token
            println result.first[it].coveredText
        }
    }
}
