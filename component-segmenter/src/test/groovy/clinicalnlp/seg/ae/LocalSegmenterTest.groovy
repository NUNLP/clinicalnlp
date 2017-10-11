package clinicalnlp.seg.ae

import clinicalnlp.dsl.ae.LocalDSLAnnotator
import clinicalnlp.types.Segment
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.metadata.TypeSystemDescription
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline

class LocalSegmenterTest {
	
	@BeforeClass
	static void setupClass() {
    }
	
	@Before
	void setUp() throws Exception {
	}

	@After
	void tearDown() throws Exception {
	}

    @Test
    void testSegmenter() {

        //build type system description
        TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription()
        tsd.resolveImports()

        // -------------------------------------------------------------------
        // build and run a pipeline to generate annotations
        // -------------------------------------------------------------------

        def sentence = """\
        FINDINGS:
        Patient has fever but no cough and pneumonia is ruled out.
        There is no increase in weakness.
        Patient does not have measles.
        IMPRESSION:
        Patient is sick.
        """
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(LocalDSLAnnotator,
                    LocalDSLAnnotator.PARAM_SCRIPT_FILE, 'groovy/SimpleSegmenter.groovy'))
        }
        AnalysisEngine engine = builder.createAggregate()
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(sentence)

        runPipeline(jcas, engine)

        // -------------------------------------------------------------------
        // test the results
        // -------------------------------------------------------------------

        assert jcas.select(type:Segment,
            filter:{it.coveredText==jcas.documentText}).size() == 1
    }
}
