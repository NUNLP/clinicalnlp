package clinicalnlp.pattern

import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Token
import gov.va.vinci.leo.window.types.Window
import groovy.util.logging.Log4j
import org.apache.log4j.Level
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

@Log4j
class AnnotationPatternTests {

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

    AnalysisEngine engine;

    @BeforeClass
    static void setupClass() {
        Class.forName('clinicalnlp.dsl.UIMA_DSL')
    }

    @Before
    void setUp() throws Exception {
        log.setLevel(Level.INFO)
        log.setLevel(Level.INFO)
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(TestAnnotator))
        }
        this.engine = builder.createAggregate()
    }

    @Test
    void smokeTest() {
        //--------------------------------------------------------------------------------------------------------------
        // construct an AnnotationPattern instance
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationPattern pattern = new AnnotationPattern(
                new NodeBuilder().regex (caseInsensitive:true) {
                    include(type:NamedEntityMention, feats:['code'])
                    include(type:Token, feats:['pos'])
                    match {
                        node(type:NamedEntityMention, name:'finding', text:/tubular\s+adenoma/)
                        node(type:Token, range:[0,2])
                        node(type:Token, name:'seen', text:/seen/, feats:[pos:/V../])
                        node(type:Token, name:'tokens', text:/in|the/, range:[0,2])
                        node(type:NamedEntityMention, name:'site', text:/sigmoid\s+colon/, feats:[code:/C.2/])
                    }
                }
        )
        assert pattern != null

        //--------------------------------------------------------------------------------------------------------------
        // generate some annotations
        //--------------------------------------------------------------------------------------------------------------
        String text = 'Tubular adenoma was seen in the sigmoid colon'
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)
        SimplePipeline.runPipeline(jcas, engine)

        AnnotationSequenceGenerator sequencer =
                new AnnotationSequenceGenerator(jcas.select(type:Window)[0], [NamedEntityMention, Token])
        Iterator<List<? extends Annotation>> iter = sequencer.iterator()
        Iterator matcher = pattern.matcher(iter.next())
    }
}
