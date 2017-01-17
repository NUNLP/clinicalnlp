package clinicalnlp.pattern

import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Segment
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.PropertyConfigurator
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline

import static clinicalnlp.pattern.AnnotationPattern.$A
import static clinicalnlp.pattern.AnnotationPattern.$N

@Log4j
class AnnotationMatcherTests {
    JCas jcas;

    @BeforeClass
    static void setupClass() {
        def config = new ConfigSlurper().parse(
            AnnotationPatternRegexGeneratorTests.class.getResource('/config.groovy').text)
        PropertyConfigurator.configure(config.toProperties())
        Class.forName('clinicalnlp.dsl.UIMA_DSL')
    }

    @Before
    void setUp() throws Exception {
        log.setLevel(Level.INFO)

        // construct a pipeline
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(
                clinicalnlp.annotator.NamedEntityMentionMatcher,
                'patternStr', /(?i)(pneumonia|fever|cough|sepsis|weakness|measles)/)) }
        AnalysisEngine engine = builder.createAggregate()

        // run pipeline to generate annotations
        def text = """\
        Patient has fever but no cough and pneumonia is ruled out.
        There is no increase in weakness.
        Patient does not have measles.
        """
        this.jcas = engine.newJCas()
        jcas.setDocumentText(text)
        runPipeline(jcas, engine)
    }

    @Test
    void smokeTest() {
        Collection<Segment> segs = jcas.select(type:Segment)
        assert segs.size() == 1
        Collection<Annotation> sents = jcas.select(type:Sentence)
        assert sents.size() == 3
        Collection<Annotation> tokens = jcas.select(type:Token)
        assert tokens.size() == 22
        Collection<NamedEntityMention> nems = jcas.select(type:NamedEntityMention)
        assert nems.size() == 5
    }

    @Test
    void testBasicPatternMatch() {
        Collection<Class<? extends Annotation>> types = [Sentence]
        AnnotationSequenceGenerator sequencer = new AnnotationSequenceGenerator(jcas.select(type:Segment)[0], types)

        AnnotationPattern pattern = $A(Sentence)(3,3)
        AnnotationMatcher matcher = pattern.matcher(sequencer.iterator().next())
        while (matcher.hasNext()) {
            matcher.next()
        }
    }
}
