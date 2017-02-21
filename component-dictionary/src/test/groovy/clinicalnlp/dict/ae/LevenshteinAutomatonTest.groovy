package clinicalnlp.dict.ae

import clinicalnlp.dsl.ae.LocalDSLAnnotator
import clinicalnlp.token.ae.LocalTokenAnnotator
import clinicalnlp.types.DictMatch
import clinicalnlp.types.Segment
import clinicalnlp.types.Token
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
class LevenshteinAutomatonTest {

    @BeforeClass
    static void setupClass() {
        Class.forName('clinicalnlp.dsl.DSL')
        BasicConfigurator.configure()
    }

    @Before
    void setUp() throws Exception {
        log.setLevel(Level.INFO)
    }

    @Test
    void smokeTest() {
        String text = """\
        The patient has a diagnosis of spongioblastoma multiforme.  GBM does not have a good prognosis.
        But I can't rule out meningioma in the brain and spinal cord.
        """

        // -------------------------------------------------------------------
        // Construct the pipeline
        // -------------------------------------------------------------------
        ExternalResourceDescription tokenResDesc = ExternalResourceFactory.createExternalResourceDescription(
            opennlp.uima.tokenize.TokenizerModelResourceImpl, "file:clinicalnlp/models/en-token.bin")

        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(
                LocalTokenAnnotator,
                LocalTokenAnnotator.PARAM_CONTAINER_TYPE,
                Segment.canonicalName,
                LocalTokenAnnotator.TOKEN_MODEL_KEY, tokenResDesc
            ))
        }
        AnalysisEngine engine = builder.createAggregate()

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
        Collection<Token> tokens = jcas.select(type:Token)
        assert tokens.size() == 31

//        Collection<DictMatch> matches = jcas.select(type:DictMatch)
//        assert matches.size() == 2
    }
}
