package clinicalnlp.pattern

import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Segment
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline

@Log4j
class AnnotationSequenceGeneratorTests {

    JCas jcas;

    @BeforeClass
    static void setupClass() {
        Class.forName('clinicalnlp.dsl.UIMA_DSL')
        BasicConfigurator.configure()
    }

    @Before
    void setUp() throws Exception {
        log.setLevel(Level.INFO)

        // -------------------------------------------------------------------
        // construct a pipeline
        // -------------------------------------------------------------------
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(
                    clinicalnlp.annotator.NamedEntityMentionMatcher,
                    'patternStr', /(?i)(pneumonia|fever|cough|sepsis|weakness)/))
        }
        AnalysisEngine engine = builder.createAggregate()

        // -------------------------------------------------------------------
        // run pipeline to generate annotations
        // -------------------------------------------------------------------
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
        // -------------------------------------------------------------------
        // test the annotations
        // -------------------------------------------------------------------
        Collection<Segment> segs = jcas.select(type:Segment)
        assert segs.size() == 1
        Collection<Annotation> sents = jcas.select(type:Sentence)
        assert sents.size() == 3
        Collection<Annotation> tokens = jcas.select(type:Token)
        assert tokens.size() == 22
        Collection<NamedEntityMention> nems = jcas.select(type:NamedEntityMention)
        assert nems.size() == 4
    }

    @Test
    void testSequenceGeneration() {
        Segment segment = this.jcas.select(type:Segment)[0]
        Collection<Class<? extends Annotation>> types = [Sentence.class]
        AnnotationSequenceGenerator sequencer = new AnnotationSequenceGenerator(segment, types)
        assert sequencer != null

        Iterator<List<? extends Annotation>> iter = sequencer.iterator()
        assert iter != null
        assert iter.hasNext()
        List<? extends Annotation> sequence = iter.next()
        assert sequence.size() == 3
        assert sequence[0] instanceof Sentence
        assert sequence[1] instanceof Sentence
        assert sequence[2] instanceof Sentence
        assert !iter.hasNext()
    }
}
