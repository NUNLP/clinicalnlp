package clinicalnlp.pattern

import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Segment
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
import gov.va.vinci.leo.window.types.Window
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.PropertyConfigurator
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.fit.pipeline.SimplePipeline
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import java.util.regex.Matcher

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline

import static clinicalnlp.pattern.AnnotationPattern.$A
import static AnnotationStringGenerator.genSequenceString


@Log4j
class AnnotationStringGeneratorTests {
    static class TestAnnotator extends JCasAnnotator_ImplBase {
        @Override
        void process(JCas jCas) throws AnalysisEngineProcessException {
            String text = jCas.documentText
            jCas.create(type: Window, begin: 0, end: text.length())
            Matcher m = (text =~ /\b\w+\b/)
            m.each {
                Token t = jCas.create(type: Token, begin: m.start(0), end: m.end(0))
                switch (t.coveredText) {
                    case 'Tubular':t.pos = 'JJ'; break;
                    case 'adenoma':t.pos = 'NN'; break;
                    case 'was':t.pos = 'AUX'; break;
                    case 'seen':t.pos = 'VBN'; break;
                    case 'in':t.pos = 'IN'; break;
                    case 'the':t.pos = 'DT'; break;
                    case 'sigmoid':t.pos = 'JJ'; break;
                    case 'colon':t.pos = 'NN'; break;
                    case '.':t.pos = 'PUNC'; break;
                }
            }
            m = (text =~ /(?i)\b(sigmoid\s+colon)|(tubular\s+adenoma)|(polyps)\b/)
            m.each {
                NamedEntityMention nem = jCas.create(type: NamedEntityMention, begin: m.start(0), end: m.end(0))
                switch (nem.coveredText) {
                    case 'Tubular adenoma':nem.code = 'C01'; break;
                    case 'sigmoid colon':nem.code = 'C02'; break;
                    case 'polyps':nem.code = 'C03'; break;
                }
            }
        }
    }

    @BeforeClass
    static void setupClass() {
        def config = new ConfigSlurper().parse(
            AnnotationRegexTests.class.getResource('/config.groovy').text)
        PropertyConfigurator.configure(config.toProperties())
        Class.forName('clinicalnlp.dsl.UIMA_DSL')
    }

    AnalysisEngine engine;

    @Before
    void setUp() throws Exception {
        log.setLevel(Level.INFO)

        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(TestAnnotator))
        }
        this.engine = builder.createAggregate()
    }


    @Test
    void testSequenceStringGeneration() {
        // generate some annotations
        String text = 'Tubular adenoma was seen in the sigmoid colon'
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)
        SimplePipeline.runPipeline(jcas, engine)

        AnnotationRegex regex = new AnnotationRegex($A(Token, [text:/.+/, pos:/.+/])(11,11))
        AnnotationSequencer sequencer = new AnnotationSequencer(jcas.select(type:Window)[0], [Token])
        String sequenceString = genSequenceString(regex, sequencer.iterator().next())
        log.info "Sequence string: ${sequenceString}"
        assert sequenceString ==
            '①‹JJ›‹Tubular›①‹NN›‹adenoma›①‹AUX›‹was›①‹VBN›‹seen›①‹IN›‹in›①‹DT›‹the›①‹JJ›‹sigmoid›①‹NN›‹colon›'
    }
}
