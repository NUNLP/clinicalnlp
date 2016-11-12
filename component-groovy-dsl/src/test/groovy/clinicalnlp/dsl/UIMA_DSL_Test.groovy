package clinicalnlp.dsl

import clinicalnlp.types.NamedEntityMention
import gov.va.vinci.leo.AnnotationLibrarian
import gov.va.vinci.leo.sentence.types.Sentence
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.fit.util.JCasUtil
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.util.regex.Matcher

import static UIMA_DSL.*
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline

@Log4j
class UIMA_DSL_Test {
    public static class NamedEntityMentionMatcher extends JCasAnnotator_ImplBase {
        @Override
        public void process(JCas jCas) throws AnalysisEngineProcessException {
            Matcher matcher = jCas.documentText =~ /([A-Z].+\.)/
            matcher.each {
                Sentence sent = new Sentence(jCas)
                sent.begin = matcher.start(1)
                sent.end = matcher.end(1)
                sent.addToIndexes()
            }

            matcher = jCas.documentText =~ /(?i)(pneumonia|fever|cough|sepsis|weakness)/
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
        // TODO: make sure static initializtion always occurs, remove need for this call
        Class.forName('clinicalnlp.dsl.UIMA_DSL')
    }

    @Before
    public void setUp() throws Exception {
        log.setLevel(Level.INFO)
    }

    @Test
    public void testJCasCreate() {
        def sentence = """
Patient has fever but no cough and pneumonia is ruled out.
The patient does not have pneumonia or sepsis.
        """
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(NamedEntityMentionMatcher))
        }
        AnalysisEngine engine = builder.createAggregate()
        JCas jcas = engine.newJCas()
        Sentence sent = jcas.create(type:Sentence, begin:0, end:sentence.length())
        JCas jcas2 = sent.getCAS().getJCas()
        assert jcas == jcas2
        Collection<Sentence> sents = JCasUtil.select(jcas, Sentence)
        assert sents.size() == 1
    }

    @Test
    public void testJCasSelect() {
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
        }
        AnalysisEngine engine = builder.createAggregate()
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(sentence)
        runPipeline(jcas, engine)

        // -------------------------------------------------------------------
        // test the results by selecting annotations with
        // miscellaneous filter arguments
        // -------------------------------------------------------------------

        assert jcas.select(type:NamedEntityMention).size() == 4

        assert jcas.select(type:Sentence,
                filter:not(contains(NamedEntityMention))).size() == 1

        assert jcas.select(type:Sentence,
                filter:and({it.coveredText.startsWith('Patient')},
                        {it.coveredText.endsWith('out.') })).size() == 1

        def sentsWithMentions = jcas.select(type:Sentence,
                filter:contains(NamedEntityMention))
        assert sentsWithMentions.size() == 2

        assert jcas.select(type:NamedEntityMention,
                filter:coveredBy(sentsWithMentions[0])).size() == 3

        assert jcas.select(type:NamedEntityMention,
                filter:not(coveredBy(sentsWithMentions[0]))).size() == 1

        assert jcas.select(type:NamedEntityMention,
                filter:between(0, 60)).size() == 3

        assert jcas.select(type:NamedEntityMention,
                filter:before(60)).size() == 3

        assert jcas.select(type:NamedEntityMention,
                filter:after(60)).size() == 1
    }

    @Test
    public void testRemoveCovered() {
        // -------------------------------------------------------------------
        // build and run a pipeline to generate annotations
        // -------------------------------------------------------------------

        def text = """\
        Patient has fever but no cough and pneumonia is ruled out.
        There is no increase in weakness.
        Patient does not have measles.
        """
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(NamedEntityMentionMatcher))
        }
        AnalysisEngine engine = builder.createAggregate()
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)
        jcas.create(type:NamedEntityMention, begin:19, end:23)
        jcas.create(type:NamedEntityMention, begin:19, end:26)
        jcas.create(type:NamedEntityMention, begin:19, end:26)
        jcas.create(type:NamedEntityMention, begin:20, end:25)
        jcas.create(type:NamedEntityMention, begin:20, end:25)
        jcas.create(type:NamedEntityMention, begin:20, end:25)

        Collection<NamedEntityMention> nems = jcas.select(type:NamedEntityMention)
        assert nems.size() == 6

        jcas.removeCovered(
                anns:jcas.select(type:NamedEntityMention),
                types:[NamedEntityMention]
        )

        nems = jcas.select(type:NamedEntityMention)
        assert nems.size() == 1
    }

    @Test
    public void testAnnotationLibrarian() {
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
        }
        AnalysisEngine engine = builder.createAggregate()
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(sentence)
        runPipeline(jcas, engine)

        // -------------------------------------------------------------------
        // test the results by selecting annotations with
        // miscellaneous filter arguments
        // -------------------------------------------------------------------

        Collection<Annotation> sents = jcas.select(type:Sentence)
        sents.each { sent ->
            println "Sentence: ${sent.coveredText}"
        }

        Collection<Annotation> nems = jcas.select(type:NamedEntityMention)
        nems.each { nem ->
            println "NamedEntityMention: ${nem.coveredText}"
        }

        assert AnnotationLibrarian.completelyCovers(sents[0], nems[0])
        assert AnnotationLibrarian.completelyCovers(sents[0], nems[1])
        assert AnnotationLibrarian.completelyCovers(sents[0], nems[2])
        assert !AnnotationLibrarian.completelyCovers(sents[0], nems[3])
        assert !AnnotationLibrarian.completelyCovers(sents[1], nems[0])
        assert !AnnotationLibrarian.completelyCovers(sents[1], nems[1])
        assert !AnnotationLibrarian.completelyCovers(sents[1], nems[2])
        assert AnnotationLibrarian.completelyCovers(sents[1], nems[3])
        assert !AnnotationLibrarian.completelyCovers(sents[2], nems[0])
        assert !AnnotationLibrarian.completelyCovers(sents[2], nems[1])
        assert !AnnotationLibrarian.completelyCovers(sents[2], nems[2])
        assert !AnnotationLibrarian.completelyCovers(sents[2], nems[3])
        assert AnnotationLibrarian.completelyCovers(nems[3], nems[3])
    }
}
