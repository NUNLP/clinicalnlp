package clinicalnlp.sent.ae

import clinicalnlp.dsl.ae.LocalDSLAnnotator
import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Segment
import com.google.common.base.Charsets
import com.google.common.io.Resources
import gov.va.vinci.leo.sentence.types.Sentence
import groovy.util.logging.Log4j
import opennlp.uima.util.UimaUtil
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.analysis_engine.AnalysisEngineDescription
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.factory.ExternalResourceFactory
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ExternalResourceDescription
import org.apache.uima.resource.ResourceInitializationException
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.util.regex.Matcher
import java.util.regex.Pattern

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline

@Log4j
class LocalSentenceDetectorTest {
    public static class NamedEntityMentionMatcher extends JCasAnnotator_ImplBase {
        public static final String PATTERN = 'patternStr'

        @ConfigurationParameter(name = 'patternStr', mandatory = false,
                description = 'Characters to split on')
        private String patternStr;

        private Pattern pattern;

        @Override
        void initialize(UimaContext context) throws ResourceInitializationException {
            super.initialize(context)
            this.pattern = Pattern.compile(this.patternStr)
        }

        @Override
        public void process(JCas jCas) throws AnalysisEngineProcessException {
            Matcher matcher = jCas.documentText =~ this.pattern
            matcher.each {
                NamedEntityMention nem = new NamedEntityMention(jCas)
                nem.begin = matcher.start(1)
                nem.end = matcher.end(1)
                nem.addToIndexes()
            }
        }

    }
    
    @BeforeClass
    public static void setupClass() {
        Class.forName('clinicalnlp.dsl.UIMA_DSL')
        BasicConfigurator.configure()
    }
    
    @Before
    public void setUp() throws Exception {
        log.setLevel(Level.INFO)
    }

    @Test
    public void smokeTestOpenNLP() {
        AnalysisEngineDescription desc = createEngineDescription(
                opennlp.uima.sentdetect.SentenceDetector,
                'opennlp.uima.SentenceType', Sentence.name,
                'opennlp.uima.ContainerType', Segment.name)
        ExternalResourceFactory.createDependencyAndBind(desc, UimaUtil.MODEL_PARAMETER,
                opennlp.uima.sentdetect.SentenceModelResourceImpl, 'file:clinicalnlp/models/sd-med-model.zip')
        AnalysisEngine engine = AnalysisEngineFactory.createEngine(desc)
        assert engine != null
    }

    @Test
    public void smokeTest() {
        // construct the pipeline
        ExternalResourceDescription extDesc = ExternalResourceFactory.createExternalResourceDescription(
                opennlp.uima.sentdetect.SentenceModelResourceImpl, "file:clinicalnlp/models/sd-med-model.zip")
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(
                    LocalSentenceDetector,
                    LocalSentenceDetector.SENT_MODEL_KEY, extDesc))
        }
        AnalysisEngine engine = builder.createAggregate()

        // load in the text to process
        URL url = Resources.getResource('data/input/test-note-1.txt')
        String text = Resources.toString(url, Charsets.UTF_8)

        // create a new CAS and seed with a Segment
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)
        jcas.create(type:Segment, begin:0, end:text.length())

        // apply the sentence detector
        engine.process(jcas)
        Collection<Sentence> sents = jcas.select(type:Sentence)
        assert sents.size() == 21
        sents.each { log.info "Sentence: $it.coveredText" }
    }

    @Test
    public void testWithNoOptions() {
        // construct the pipeline
        ExternalResourceDescription extDesc = ExternalResourceFactory.createExternalResourceDescription(
                opennlp.uima.sentdetect.SentenceModelResourceImpl, "file:clinicalnlp/models/sd-med-model.zip")
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(LocalDSLAnnotator,
                    LocalDSLAnnotator.PARAM_SCRIPT_FILE,
                    'groovy/TestSegmenter.groovy'))
            add(createEngineDescription(
                    LocalSentenceDetector,
                    LocalSentenceDetector.SENT_MODEL_KEY, extDesc))
        }
        AnalysisEngine engine = builder.createAggregate()

        // load in the text to process
        URL url = Resources.getResource('data/input/test-note-2.txt')
        String text = Resources.toString(url, Charsets.UTF_8)

        // create a new JCas
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)

        // apply the sentence detector
        engine.process(jcas)
        Collection<Sentence> sents = jcas.select(type:Sentence)
        assert sents.size() == 8
        sents.each { log.info "Sentence: $it.coveredText" }
    }

    @Test
    public void testWithSegmentSelected() {
        // construct the pipeline
        ExternalResourceDescription extDesc = ExternalResourceFactory.createExternalResourceDescription(
                opennlp.uima.sentdetect.SentenceModelResourceImpl, "file:clinicalnlp/models/sd-med-model.zip")
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(LocalDSLAnnotator,
                    LocalDSLAnnotator.PARAM_SCRIPT_FILE,
                    'groovy/TestSegmenter.groovy'))
            add(createEngineDescription(
                    LocalSentenceDetector,
                    LocalSentenceDetector.SD_SEGMENTS_TO_PARSE,
                    'groovy/TestSegmentsToParse.groovy',
                    LocalSentenceDetector.SENT_MODEL_KEY, extDesc))
        }
        AnalysisEngine engine = builder.createAggregate()

        // load in the text to process
        URL url = Resources.getResource('data/input/test-note-2.txt')
        String text = Resources.toString(url, Charsets.UTF_8)

        // create a new JCas
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)

        // apply the sentence detector
        engine.process(jcas)
        Collection<Sentence> sents = jcas.select(type:Sentence)
        assert sents.size() == 6
        sents.each { log.info "Sentence: $it.coveredText" }
    }

    @Test
    public void testWithSentenceBreakingPattern() {
        // construct the pipeline
        ExternalResourceDescription extDesc = ExternalResourceFactory.createExternalResourceDescription(
                opennlp.uima.sentdetect.SentenceModelResourceImpl, "file:clinicalnlp/models/sd-med-model.zip")
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(LocalDSLAnnotator,
                    LocalDSLAnnotator.PARAM_SCRIPT_FILE,
                    'groovy/TestSegmenter.groovy'))
            add(createEngineDescription(
                    LocalSentenceDetector,
                    LocalSentenceDetector.PARAM_SPLIT_PATTERN, ':',
                    LocalSentenceDetector.SENT_MODEL_KEY, extDesc))
        }
        AnalysisEngine engine = builder.createAggregate()

        // load in the text to process
        URL url = Resources.getResource('data/input/test-note-2.txt')
        String text = Resources.toString(url, Charsets.UTF_8)

        // create a new JCas
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)

        // apply the sentence detector
        engine.process(jcas)
        Collection<Sentence> sents = jcas.select(type:Sentence)
        assert sents.size() == 13
        sents.each { log.info "Sentence: $it.coveredText" }
    }

    @Test
    public void testWithNewlineSentenceBreakingPattern() {
        // construct the pipeline
        ExternalResourceDescription extDesc = ExternalResourceFactory.createExternalResourceDescription(
                opennlp.uima.sentdetect.SentenceModelResourceImpl, "file:clinicalnlp/models/sd-med-model.zip")
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(LocalDSLAnnotator,
                    LocalDSLAnnotator.PARAM_SCRIPT_FILE,
                    'groovy/SimpleSegmenter.groovy'))
            add(createEngineDescription(
                    LocalSentenceDetector,
                    LocalSentenceDetector.PARAM_SPLIT_PATTERN, '[\\n\\r:]+',
                    LocalSentenceDetector.SENT_MODEL_KEY, extDesc))
        }
        AnalysisEngine engine = builder.createAggregate()

        // load in the text to process
        URL url = Resources.getResource('data/input/test-note-1.txt')
        String text = Resources.toString(url, Charsets.UTF_8)

        // create a new JCas
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)

        // apply the sentence detector
        engine.process(jcas)
        Collection<Sentence> sents = jcas.select(type:Sentence)
        assert sents.size() == 28
        sents.each { log.info "Sentence: $it.coveredText" }
    }

    @Test
    public void testAnchoredSentenceDetection() {

        def text = """\
        Patient has fever but no cough and pneumonia is ruled out.
        There is no increase in weakness.
        Patient does not have measles.
        """

        // construct the pipeline
        ExternalResourceDescription extDesc = ExternalResourceFactory.createExternalResourceDescription(
                opennlp.uima.sentdetect.SentenceModelResourceImpl, "file:clinicalnlp/models/sd-med-model.zip")
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(NamedEntityMentionMatcher,
                    NamedEntityMentionMatcher.PATTERN, '(?i)(cough|pneumonia|measles)'
            ))
            add(createEngineDescription(
                    LocalSentenceDetector,
                    LocalSentenceDetector.ANCHOR_TYPES, [NamedEntityMention],
                    LocalSentenceDetector.SPAN_SIZE, 150,
                    LocalSentenceDetector.PARAM_SPLIT_PATTERN, '[\\n\\r:]+',
                    LocalSentenceDetector.SENT_MODEL_KEY, extDesc))
        }
        AnalysisEngine engine = builder.createAggregate()

        // run the pipeline
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)
        jcas.create(type:Segment, begin:0, end:text.length())
        runPipeline(jcas, engine)

        // test results
        Collection<Sentence> sents = jcas.select(type:Sentence)
        assert sents.size() == 2
        sents.each { log.info "Sentence: $it.coveredText" }
    }
}
