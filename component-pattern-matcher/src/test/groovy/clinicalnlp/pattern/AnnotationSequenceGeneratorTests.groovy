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
    void testSequenceGeneration1() {
        Segment newSegment = this.jcas.create(type:Segment, begin:0, end:this.jcas.documentText.length())
        Collection<Class<? extends Annotation>> types = [Segment]
        AnnotationSequenceGenerator sequencer = new AnnotationSequenceGenerator(newSegment, types)
        assert sequencer != null

        Iterator<List<? extends Annotation>> iter = sequencer.iterator()
        assert iter != null
        assert iter.hasNext()
        List<? extends Annotation> sequence = iter.next()
        assert sequence.size() == 1
        assert sequence[0] instanceof Segment
        assert !iter.hasNext()
    }

    @Test
    void testSequenceGeneration2() {
        Segment segment = this.jcas.select(type:Segment)[0]
        Collection<Class<? extends Annotation>> types = [Sentence]
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

    @Test
    void testSequenceGeneration3() {
        Sentence sentence = this.jcas.select(type:Sentence)[0]
        Collection<Class<? extends Annotation>> types = [Token]
        AnnotationSequenceGenerator sequencer = new AnnotationSequenceGenerator(sentence, types)
        assert sequencer != null

        Iterator<List<? extends Annotation>> iter = sequencer.iterator()
        assert iter != null
        assert iter.hasNext()
        List<? extends Annotation> sequence = iter.next()
        assert sequence.size() == 11
        assert sequence[0] instanceof Token
        assert sequence[1] instanceof Token
        assert sequence[2] instanceof Token
        assert sequence[3] instanceof Token
        assert sequence[4] instanceof Token
        assert sequence[5] instanceof Token
        assert sequence[6] instanceof Token
        assert sequence[7] instanceof Token
        assert sequence[8] instanceof Token
        assert sequence[9] instanceof Token
        assert sequence[10] instanceof Token
        assert !iter.hasNext()
    }

    @Test
    void testSequenceGeneration4() {
        Sentence sentence = this.jcas.select(type:Sentence)[2]
        AnnotationSequenceGenerator sequencer = new AnnotationSequenceGenerator(sentence, [NamedEntityMention, Token])
        Iterator<List<? extends Annotation>> iter = sequencer.iterator()

        assert iter.hasNext()
        List<? extends Annotation> sequence = iter.next()
        assert sequence.size() == 5
        assert sequence[0] instanceof Token
        assert sequence[1] instanceof Token
        assert sequence[2] instanceof Token
        assert sequence[3] instanceof Token
        assert sequence[4] instanceof NamedEntityMention

        assert iter.hasNext()
        sequence = iter.next()
        assert sequence.size() == 5
        assert sequence[0] instanceof Token
        assert sequence[1] instanceof Token
        assert sequence[2] instanceof Token
        assert sequence[3] instanceof Token
        assert sequence[4] instanceof Token

        assert !iter.hasNext()


        // reverse order in which types are declared
        sequencer = new AnnotationSequenceGenerator(sentence, [Token, NamedEntityMention])
        iter = sequencer.iterator()

        assert iter.hasNext()
        sequence = iter.next()
        assert sequence.size() == 5
        assert sequence[0] instanceof Token
        assert sequence[1] instanceof Token
        assert sequence[2] instanceof Token
        assert sequence[3] instanceof Token
        assert sequence[4] instanceof Token

        assert iter.hasNext()
        sequence = iter.next()
        assert sequence.size() == 5
        assert sequence[0] instanceof Token
        assert sequence[1] instanceof Token
        assert sequence[2] instanceof Token
        assert sequence[3] instanceof Token
        assert sequence[4] instanceof NamedEntityMention

        assert !iter.hasNext()
    }
}
