package clinicalnlp.dict.ae

import clinicalnlp.dict.automaton.LevenshteinAutomatonModel
import clinicalnlp.dict.trie.TrieDictModel
import clinicalnlp.sent.ae.LocalSentenceDetector
import clinicalnlp.token.ae.LocalTokenAnnotator
import clinicalnlp.types.DictMatch
import clinicalnlp.types.Segment
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.fit.factory.ExternalResourceFactory
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ExternalResourceDescription
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline

@Log4j
class LocalDictAnnotatorTest {

    AnalysisEngine engine

    @BeforeClass
    static void setupClass() {
        Class.forName('clinicalnlp.dsl.DSL')
        BasicConfigurator.configure()
    }

    @Before
    void setUp() throws Exception {
        log.setLevel(Level.INFO)

        // -------------------------------------------------------------------
        // Construct the pipeline
        // -------------------------------------------------------------------
        ExternalResourceDescription tokenResDesc = ExternalResourceFactory.createExternalResourceDescription(
            opennlp.uima.tokenize.TokenizerModelResourceImpl, "file:clinicalnlp/models/en-token.bin")

        ExternalResourceDescription sentResDesc = ExternalResourceFactory.createExternalResourceDescription(
            opennlp.uima.sentdetect.SentenceModelResourceImpl, "file:clinicalnlp/models/sd-med-model.zip")

        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(LocalSentenceDetector,
                LocalSentenceDetector.SENT_MODEL_KEY, sentResDesc)
            )
            add(createEngineDescription(LocalTokenAnnotator,
                LocalTokenAnnotator.PARAM_CONTAINER_TYPE, Sentence.canonicalName,
                LocalTokenAnnotator.TOKEN_MODEL_KEY, tokenResDesc)
            )
            add(createEngineDescription(LocalDictAnnotator,
                LocalDictAnnotator.PARAM_CONTAINER_CLASS, Sentence.canonicalName,
                LocalDictAnnotator.PARAM_TOKEN_CLASS, Token.canonicalName,
                LocalDictAnnotator.TOKEN_MODEL_KEY, tokenResDesc,
                LocalDictAnnotator.PARAM_DICTIONARY_PATH, 'classpath:abstractionSchema/histology.json',
                LocalDictAnnotator.PARAM_DICTIONARY_TYPE, TrieDictModel.canonicalName)
            )
        }
        this.engine = builder.createAggregate()
    }

	@Test
	void trieDictTest() {
        String text = """\
        The patient has a diagnosis of spongioblastoma multiforme.  GBM does not have a good prognosis.
        But I can't rule out meningioma in the brain and spinal cord.
        """

        // -------------------------------------------------------------------
        // Run the pipeline
        // -------------------------------------------------------------------
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)
        jcas.create(type:Segment, begin:0, end:text.length())
        runPipeline(jcas, engine)

        // -------------------------------------------------------------------
        // Test results
        // -------------------------------------------------------------------
        Collection<Sentence> sents = jcas.select(type:Sentence)
        assert sents.size() == 3

        Collection<Token> tokens = jcas.select(type:Token)
        assert tokens.size() == 31

        Collection<DictMatch> matches = jcas.select(type:DictMatch)
        assert matches.size() == 2

        // -------------------------------------------------------------------
        // Load a different dictionary
        // -------------------------------------------------------------------
        engine.setConfigParameterValue('clinicalnlp.dict.ae.LocalDictAnnotator/dictionaryPath',
                'classpath:abstractionSchema/morphology.json')
        engine.reconfigure()

        // -------------------------------------------------------------------
        // Run the pipeline
        // -------------------------------------------------------------------
        jcas.reset()
        jcas.setDocumentText(text)
        jcas.create(type:Segment, begin:0, end:text.length())
        runPipeline(jcas, engine)

        // -------------------------------------------------------------------
        // Test results
        // -------------------------------------------------------------------
        tokens = jcas.select(type:Token)
        assert tokens.size() == 31

        matches = jcas.select(type:DictMatch)
        assert matches.size() == 3
    }

    @Test
    void trieDictStringDistTest() {
        String text = """\
        The patient has a diagnosis of spngioblastoma multifourme.  GBM does not have a good prognosis.
        But I can't rule out meningiomal.
        """

        // run the pipeline
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)
        jcas.create(type:Segment, begin:0, end:text.length())
        runPipeline(jcas, engine)

        // test results
        Collection<Sentence> sents = jcas.select(type:Sentence)
        assert sents.size() == 3

        Collection<Token> tokens = jcas.select(type:Token)
        assert tokens.size() == 25

        Collection<DictMatch> matches = jcas.select(type:DictMatch)
        matches.each { println it.coveredText }
        assert matches.size() == 0

        // run the pipeline again with looser tolerance
        engine.setConfigParameterValue('clinicalnlp.dict.ae.LocalDictAnnotator/tolerance', new Float(0.1))
        engine.setConfigParameterValue('clinicalnlp.dict.ae.LocalDictAnnotator/maxDistance', new Integer(3))
        engine.reconfigure()

        jcas.reset()
        jcas.setDocumentText(text)
        jcas.create(type:Segment, begin:0, end:text.length())
        runPipeline(jcas, engine)

        // test results
        matches = jcas.select(type:DictMatch)
        assert matches.size() == 2
    }

    @Test
    void levenshteinDictTest() {
        String text = """\
        The patient has a diagnosis of spongioblastoma multiforme.  GBM does not have a good prognosis.
        But I can't rule out meningioma in the brain and spinal cord.
        """

        // -------------------------------------------------------------------
        // Load a LevenshteinAutomatonModel dictionary
        // -------------------------------------------------------------------

        engine.setConfigParameterValue(
            "clinicalnlp.dict.ae.LocalDictAnnotator/${LocalDictAnnotator.PARAM_DICTIONARY_TYPE}",
            LevenshteinAutomatonModel.canonicalName)
        engine.reconfigure()

        // -------------------------------------------------------------------
        // Run the pipeline
        // -------------------------------------------------------------------
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)
        jcas.create(type:Segment, begin:0, end:text.length())
        runPipeline(jcas, engine)

        // -------------------------------------------------------------------
        // Test results
        // -------------------------------------------------------------------
        Collection<Segment> segs = jcas.select(type:Segment)
        assert segs.size() == 1

        Collection<Token> tokens = jcas.select(type:Token)
        assert tokens.size() == 31

        Collection<DictMatch> matches = jcas.select(type:DictMatch)
        assert matches.size() == 2

        // -------------------------------------------------------------------
        // Load a different dictionary
        // -------------------------------------------------------------------
        engine.setConfigParameterValue('clinicalnlp.dict.ae.LocalDictAnnotator/dictionaryPath',
            'classpath:abstractionSchema/morphology.json')
        engine.reconfigure()

        // -------------------------------------------------------------------
        // Run the pipeline
        // -------------------------------------------------------------------
        jcas.reset()
        jcas.setDocumentText(text)
        jcas.create(type:Segment, begin:0, end:text.length())
        runPipeline(jcas, engine)

        // -------------------------------------------------------------------
        // Test results
        // -------------------------------------------------------------------
        tokens = jcas.select(type:Token)
        assert tokens.size() == 31

        matches = jcas.select(type:DictMatch)
        assert matches.size() == 3
    }
}
