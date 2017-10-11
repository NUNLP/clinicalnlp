package clinicalnlp.sent.ae

import clinicalnlp.dsl.ae.LocalDSLAnnotator
import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Segment
import com.google.common.base.Charsets
import com.google.common.io.Resources
import gov.va.vinci.leo.sentence.types.Sentence
import opennlp.uima.util.UimaUtil
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

class LocalSentenceDetectorTest {
    static class NamedEntityMentionMatcher extends JCasAnnotator_ImplBase {
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
        void process(JCas jCas) throws AnalysisEngineProcessException {
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
    static void setupClass() {
        Class.forName('clinicalnlp.dsl.DSL')
    }
    
    @Before
    void setUp() throws Exception {
    }

    @Test
    void smokeTestOpenNLP() {
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
    void smokeTest() {
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
    }

    @Test
    void testWithNoOptions() {
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
    }

    @Test
    void testWithSegmentSelected() {
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
    }

    @Test
    void testWithSentenceBreakingPattern() {
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
    }

    @Test
    void testWithNewlineSentenceBreakingPattern() {
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
    }
}
