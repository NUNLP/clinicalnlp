package clinicalnlp.dsl

import clinicalnlp.types.NamedEntityMention
import gov.va.vinci.leo.sentence.types.Sentence
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.DocumentAnnotation
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.util.regex.Matcher

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline
import static org.junit.Assert.assertEquals

@Log4j
class AnnotationMatcherTest {
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

    public static class NamedEntityMentionMatcher2 extends JCasAnnotator_ImplBase {
        @Override
        public void process(JCas jCas) throws AnalysisEngineProcessException {

        }
    }
    @BeforeClass
    public static void setupClass() {
        Class.forName('clinicalnlp.dsl.UIMA_DSL')
    }

    @Before
    public void setUp() throws Exception {
        log.setLevel(Level.INFO)
    }

    @Test
    public void testAnnotationMatcher() {
        // -------------------------------------------------------------------
        // build and run a pipeline to generate annotations
        // -------------------------------------------------------------------

        def sentence = """\
Patient has fever but no cough and pneumonia is ruled out.
The patient does not have pneumonia or sepsis.
        """
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(NamedEntityMentionMatcher))
        }
        AnalysisEngine engine = builder.createAggregate()
        JCas jCas = engine.newJCas()
        jCas.setDocumentText(sentence)
        runPipeline(jCas, engine)
        Collection sents = jCas.select(type:Sentence)
        assert sents.size() == 2
        Sentence sent1 = sents[0]
        Sentence sent2 = sents[1]

        AnnotationMatcher m = (~/(?<d1>pneumonia) or (?<d2>sepsis)/).matcher(coveringAnn:sent2)
        Iterator iter = m.iterator()
        assert iter.hasNext()
        iter.next()
        assert m.group(1) != null
        assertEquals "pneumonia", m.group(1)
        assertEquals 85, m.start(1)
        assertEquals 85 + ('pneumonia').length(),  m.end(1)
        assert m.group(2) != null
        assertEquals "sepsis", m.group(2)
        assertEquals 98, m.start(2)
        assertEquals 98 + ('sepsis').length(), m.end(2)

        m = (~/(cough|fever)/).matcher(coveringAnn:sent1)
        iter = m.iterator()
        assert iter.hasNext()
        iter.next()
        assert m.group(1) != null
        assertEquals "fever", m.group(1)
        assertEquals 12, m.start(1)
        assertEquals 12 + ('fever').length(), m.end(1)
        assert iter.hasNext()
        iter.next()
        assertEquals "cough", m.group(1)
        assertEquals 25, m.start(1)
        assertEquals 25 + ('cough').length(), m.end(1)

        m = (~/(cough|fever)/).matcher(coveringAnn:jCas.documentAnnotationFs)
        iter = m.iterator()
        assert iter.hasNext()
        iter.next()
        assert m.group(1) != null
        assertEquals "fever", m.group(1)
        assertEquals 12, m.start(1)
        assertEquals 12 + ('fever').length(), m.end(1)
        assert iter.hasNext()
        iter.next()
        assertEquals "cough", m.group(1)
        assertEquals 25, m.start(1)
        assertEquals 25 + ('cough').length(), m.end(1)

        // use "each" idiom
        m = (~/(cough|fever)/).matcher(coveringAnn:sent1)
        m.each{
            assert m.group(1) in ['fever', 'cough']
        }
    }

    @Test
    public void testAnnRegexMatch() {
        // -------------------------------------------------------------------
        // build and run a pipeline to generate annotations
        // -------------------------------------------------------------------

        def sentence = """\
        Patient has fever but no cough, pneumonia and sepsis are ruled out.
        """
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(NamedEntityMentionMatcher))
        }
        AnalysisEngine engine = builder.createAggregate()
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(sentence)
        runPipeline(jcas, engine)
        DocumentAnnotation docAnn = jcas.documentAnnotationFs

        AnnotationMatcher m = (~/(?<E1>@NamedEntityMention) but no (?<E2>@NamedEntityMention)/).matcher(
            JCas:jcas,
            coveringAnn:docAnn,
            types:[NamedEntityMention],
            includeText:true)
        Iterator iter = m.iterator()
        assert iter.hasNext()
        def Map binding = iter.next().binding
        assert binding.get("E1").coveredText == "fever"
        assert binding.get("E2").coveredText == "cough"
        assert !iter.hasNext()

        m = (~/(?<E1>@NamedEntityMention) and (?<E2>@NamedEntityMention).+ruled out/).matcher(
                JCas:jcas,
                coveringAnn:docAnn,
                types:[NamedEntityMention],
                includeText:true)
        iter = m.iterator()
        assert iter.hasNext()
        binding = iter.next().binding
        assert binding.get("E1").coveredText == "pneumonia"
        assert binding.get("E2").coveredText == "sepsis"
        assert !iter.hasNext()

        m = (~/(?<E1>@NamedEntityMention)(?<E2>@NamedEntityMention)/).matcher(
                JCas:jcas,
                coveringAnn:docAnn,
                types:[NamedEntityMention],
                includeText:false)
        iter = m.iterator()
        assert iter.hasNext()
        binding = iter.next().binding
        assert binding.get("E1").coveredText == "fever"
        assert binding.get("E2").coveredText == "cough"
        assert iter.hasNext()
        binding = iter.next().binding
        assert binding.get("E1").coveredText == "pneumonia"
        assert binding.get("E2").coveredText == "sepsis"
        assert !iter.hasNext()

        // test non-Annotation groups (vanilla capturing groups not handled yet)
        m = (~/(?<chunk1>(?<E1>@NamedEntityMention)(?<t> and ))(?<chunk2>(?<E2>@NamedEntityMention).+(?:ruled out))/)
                .matcher(
                JCas:jcas,
                coveringAnn:docAnn,
                types:[NamedEntityMention],
                includeText:true)
        iter = m.iterator()
        assert iter.hasNext()
        binding = iter.next().binding
        assert m.matcher.group("t") != null
        assertEquals " and ", m.matcher.group("t")
        assert m.matcher.group("chunk1") != null
        assertEquals "@NamedEntityMention and ", m.matcher.group("chunk1")
        assert m.matcher.group("chunk2") != null
        assertEquals "@NamedEntityMention are ruled out", m.matcher.group("chunk2")
        assert binding.get("E1") != null
        assert binding.get("E1").coveredText == "pneumonia"
        assert binding.get("E2") != null
        assert binding.get("E2").coveredText == "sepsis"
        assert !iter.hasNext()
    }
}
