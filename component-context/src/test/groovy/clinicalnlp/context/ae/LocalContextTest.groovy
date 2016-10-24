package clinicalnlp.context.ae

import clinicalnlp.dsl.ae.LocalDSLAnnotator
import gov.va.vinci.leo.sentence.types.Sentence;

import static clinicalnlp.dsl.UIMA_DSL.*
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline

import groovy.util.logging.Log4j

import java.util.regex.Matcher

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.jcas.JCas
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import gov.va.queri.types.NamedEntityMention

@Log4j
class LocalContextTest {

    public static class NamedEntityMentionMatcher extends JCasAnnotator_ImplBase {

        @Override
        public void process(JCas jcas) throws AnalysisEngineProcessException {
            Matcher matcher = jcas.documentText =~ /([A-Z].+\.)/
            matcher.each {
                Sentence sent = new Sentence(jcas)
                sent.begin = matcher.start(1)
                sent.end = matcher.end(1)
                sent.addToIndexes()
                println "Sentence: ${sent.coveredText}"
            }

            matcher = jcas.documentText =~ /(?i)(pneumonia|fever|cough|sepsis|weakness|measles)/
            matcher.each {
                NamedEntityMention nem = new NamedEntityMention(jcas)
                nem.begin = matcher.start(1)
                nem.end = matcher.end(1)
                nem.polarity = 1
                nem.addToIndexes()
                println "NamedEntityMention: ${nem.coveredText}"
            }
        }
    }
	
	@BeforeClass
	public static void setupClass() {
		BasicConfigurator.configure()
	}
	
	@Before
	public void setUp() throws Exception {
		log.setLevel(Level.INFO)
	}

	@After
	public void tearDown() throws Exception {
	}

    @Test
    public void testNegationScope() {

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
            add(createEngineDescription(NamedEntityMentionMatcher))
            add(createEngineDescription(LocalDSLAnnotator,
                    LocalDSLAnnotator.PARAM_SCRIPT_FILE, 'groovy/NegEx.groovy'))
        }
        AnalysisEngine engine = builder.createAggregate()
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
        assert jcas.select(type:NamedEntityMention,
            filter:and({it.coveredText=='pneumonia'}, {it.polarity==-1})).size() == 1
        assert jcas.select(type:NamedEntityMention,
            filter:and({it.coveredText=='weakness'}, {it.polarity==1})).size() == 1
        assert jcas.select(type:NamedEntityMention,
            filter:and({it.coveredText=='measles'}, {it.polarity==-1})).size() == 1
    }
}
