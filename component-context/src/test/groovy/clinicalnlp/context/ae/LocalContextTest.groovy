package clinicalnlp.context.ae

import clinicalnlp.dsl.ae.LocalDSLAnnotator
import clinicalnlp.types.NamedEntityMention
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.jcas.JCas
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import static clinicalnlp.dsl.UIMA_DSL.getAnd
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline

@Log4j
class LocalContextTest {
	
	@BeforeClass
	static void setupClass() {
        Class.forName('clinicalnlp.dsl.UIMA_DSL')
		BasicConfigurator.configure()
	}
	
	@Before
	void setUp() throws Exception {
		log.setLevel(Level.INFO)
	}

	@After
	void tearDown() throws Exception {
	}

    @Test
    void testNegationScope() {

        // -------------------------------------------------------------------
        // build and run a pipeline to generate annotations
        // -------------------------------------------------------------------

        def sentence = """\
        Patient has fever but no cough and pneumonia is ruled out.
        There is no increase in weakness.
        Patient does not have measles.
        """
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(LocalDSLAnnotator,
                LocalDSLAnnotator.PARAM_SCRIPT_FILE, 'groovy/TestConceptDetector.groovy'))
            add(createEngineDescription(LocalDSLAnnotator,
                    LocalDSLAnnotator.PARAM_SCRIPT_FILE, 'groovy/NegEx.groovy'))
        }
        AnalysisEngine engine = builder.createAggregate()
        File descriptorLocation = new File('src/test/resources/descriptors/LocalContextPipeline.xml')
        builder.createAggregateDescription().toXML(new PrintWriter(descriptorLocation))
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(sentence)

        runPipeline(jcas, engine)

        // -------------------------------------------------------------------
        // test the results
        // -------------------------------------------------------------------

        assert jcas.select(type:NamedEntityMention).size() == 5
        assert jcas.select(type:NamedEntityMention,
            filter:and({it.coveredText=='fever'}, {it.polarity==1})).size() == 1
        assert jcas.select(type:NamedEntityMention,
            filter:and({it.coveredText=='cough'}, {it.polarity==-1})).size() == 1
//        assert jcas.select(type:NamedEntityMention,
//            filter:and({it.coveredText=='pneumonia'}, {it.polarity==-1})).size() == 1
        assert jcas.select(type:NamedEntityMention,
            filter:and({it.coveredText=='weakness'}, {it.polarity==1})).size() == 1
//        assert jcas.select(type:NamedEntityMention,
//            filter:and({it.coveredText=='measles'}, {it.polarity==-1})).size() == 1
    }
}
