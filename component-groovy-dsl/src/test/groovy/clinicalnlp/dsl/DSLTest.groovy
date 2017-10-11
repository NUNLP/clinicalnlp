package clinicalnlp.dsl

import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
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

import static clinicalnlp.dsl.DSL.*
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline

class DSLTest {
    static class NamedEntityMentionMatcher extends JCasAnnotator_ImplBase {
        @Override
        void process(JCas jCas) throws AnalysisEngineProcessException {
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
    static void setupClass() {
        // TODO: make sure static initializtion always occurs, remove need for this call
        Class.forName('clinicalnlp.dsl.DSL')
    }

    AnalysisEngine engine;

    @Before
    void setUp() throws Exception {
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(NamedEntityMentionMatcher))
        }
        this.engine = builder.createAggregate()
    }

    @Test
    void testJCasCreate() {
        // -------------------------------------------------------------------
        // run pipeline to generate annotations
        // -------------------------------------------------------------------
        def text = """
Patient has fever but no cough and pneumonia is ruled out.
The patient does not have pneumonia or sepsis.
        """
        JCas jcas = engine.newJCas()
        Sentence sent = jcas.create(type:Sentence, begin:0, end:text.length())
        JCas jcas2 = sent.getCAS().getJCas()
        assert jcas == jcas2
        Collection<Sentence> sents = JCasUtil.select(jcas, Sentence)
        assert sents.size() == 1
    }

    @Test
    void testJCasSelect() {
        // -------------------------------------------------------------------
        // run pipeline to generate annotations
        // -------------------------------------------------------------------
        def text = """\
        Patient has fever but no cough and pneumonia is ruled out.
        There is no increase in weakness.
        Patient does not have measles.
        """
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)
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
    void testApplyPattern() {
        // -------------------------------------------------------------------
        // run pipeline to generate annotations
        // -------------------------------------------------------------------
        def text = """\
        Patient has fever but no cough and pneumonia is ruled out.
        There is no increase in weakness.
        Patient does not have measles.
        """
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)
        runPipeline(jcas, engine)

        Collection<Annotation> sents = jcas.select(type:Sentence)
        def pattern1 = ~/Patient/
        def pattern2 = ~/There|has|but/
        applyPatterns(
            anns:sents,
            patterns:[pattern1, pattern2],
            action: { AnnotationMatchResult m ->
                jcas.create(type:Token, begin:m.start(0), end:m.end(0)) }
        )
        Collection<Annotation> tokens = jcas.select(type:Token)
        assert tokens.size() == 5
        assert tokens[0].coveredText == 'Patient'
        assert tokens[1].coveredText == 'has'
        assert tokens[2].coveredText == 'but'
        assert tokens[3].coveredText == 'There'
        assert tokens[4].coveredText == 'Patient'
    }

    @Test
    void testCreateMentions() {
        // -------------------------------------------------------------------
        // run pipeline to generate annotations
        // -------------------------------------------------------------------
        def text = """\
        Patient has fever but no cough and pneumonia is ruled out.
        There is no increase in weakness.
        Patient does not have measles.
        """
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)
        runPipeline(jcas, engine)

        Collection<Annotation> nems = jcas.select(type:NamedEntityMention)
        assert nems.size() == 4

        Collection<Annotation> sents = jcas.select(type:Sentence)
        DSL.createMentions(
            patterns:[
                (~/Patient/):[group:0, code:'PERSON']
            ],
            jcas:jcas,
            searchSet:jcas.select(type:Sentence),
            type:NamedEntityMention,
            longestMatch:true
        )
        nems = jcas.select(type:NamedEntityMention)
        assert nems.size() == 6

        nems = jcas.select(type:NamedEntityMention,
            filter:and({it.coveredText == 'Patient'}, { it.code == 'PERSON'}))
        assert nems.size() == 2
    }
}
