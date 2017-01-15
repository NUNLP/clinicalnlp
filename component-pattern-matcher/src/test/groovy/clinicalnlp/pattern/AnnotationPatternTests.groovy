package clinicalnlp.pattern

import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
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
import static clinicalnlp.pattern.AnnotationPattern.$A
import static clinicalnlp.pattern.AnnotationPattern.$N

/**
 * TODO: create type code map
 * TODO: create feature map
 * TODO: add boundary characters,
 * TODO: transform embedded regex so all non-escaped '.' chars are transformed to negated class
 */
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

    AnalysisEngine engine

    @BeforeClass
    static void setupClass() {
        Class.forName('clinicalnlp.dsl.UIMA_DSL')
    }

    @Before
    void setUp() throws Exception {
        log.setLevel(Level.INFO)
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(TestAnnotator))
        }
        this.engine = builder.createAggregate()

//        //--------------------------------------------------------------------------------------------------------------
//        String text = 'Tubular adenoma was seen in the sigmoid colon'
//        JCas jcas = engine.newJCas()
//        jcas.setDocumentText(text)
//        SimplePipeline.runPipeline(jcas, engine)
//
//        AnnotationSequenceGenerator sequencer =
//                new AnnotationSequenceGenerator(jcas.select(type:Window)[0], [NamedEntityMention, Token])
//        Iterator<List<? extends Annotation>> iter = sequencer.iterator()
//        Iterator matcher = pattern.matcher(iter.next())
    }

    @Test
    void testAtomicPatterns() {
        AnnotationPattern pattern1 = $A(Token, [pos:'NN', text:'/Foo/'])
        assert pattern1 != null
        assert pattern1.type == Token
        assert pattern1.features == [pos:'NN', text:'/Foo/']
        assert pattern1.range == null
        assert pattern1.name == null

        AnnotationPattern pattern2 = $N('t1', pattern1*(0..3))
        assert pattern2 != null
        assert pattern2 == pattern1
        assert pattern2.range == (0..3)
        assert pattern2.name == 't1'
    }

    @Test
    void testSequencePatterns() {
        AnnotationPattern pattern1 = $A(Token, [pos:'NN', text:'/Foo/'])
        AnnotationPattern pattern2 = $A(Token, [pos:'VB', text:'/Bar/'])
        AnnotationPattern pattern3 = $A(NamedEntityMention, [cui:'C01'])
        AnnotationPattern pattern4 = pattern1 & pattern2 & pattern3
        assert pattern4 != null
        assert pattern4 instanceof SequenceAnnotationPattern
        assert pattern4.children.size() == 3
        assert pattern4.children[0] == pattern1
        assert pattern4.children[1] == pattern2
        assert pattern4.children[2] == pattern3

        AnnotationPattern pattern5 = pattern1 & (pattern2 & pattern3)
        assert pattern5 != null
        assert pattern5 instanceof SequenceAnnotationPattern
        assert pattern5.children.size() == 2
        assert pattern5.children[0] == pattern1
        assert pattern5.children[1] instanceof SequenceAnnotationPattern
        assert pattern5.children[1].children[0] == pattern2
        assert pattern5.children[1].children[1] == pattern3

        pattern5 = $N('seq1', pattern5)
        assert pattern5.name == 'seq1'

        AnnotationPattern pattern6 =
                $N('seq1',
                        $A(Token, [pos:'NN', text:'/Foo/']) &
                                $N('seq2', $A(Token, [pos:'VB', text:'/Bar/']) & $A(NamedEntityMention, [cui:'C01'])))
        assert pattern6 != null
        assert pattern6 instanceof SequenceAnnotationPattern
        assert pattern6.name == 'seq1'
        assert pattern6.children.size() == 2
        assert pattern6.children[1].name == 'seq2'
        assert pattern6.children[1].children.size() == 2
    }

    @Test
    void testOptionPatterns() {
        AnnotationPattern pattern1 = $A(Token, [pos:'NN', text:'/Foo/'])
        AnnotationPattern pattern2 = $A(Token, [pos:'VB', text:'/Bar/'])
        AnnotationPattern pattern3 = $A(NamedEntityMention, [cui:'C01'])
        AnnotationPattern pattern4 = $N('opts', pattern1 | pattern2 | pattern3)
        assert pattern4 != null
        assert pattern4 instanceof OptionsAnnotationPattern
        assert pattern4.children.size() == 3
        assert pattern4.children[0] == pattern1
        assert pattern4.children[1] == pattern2
        assert pattern4.children[2] == pattern3

        AnnotationPattern pattern5 = (pattern1 | (pattern2 | pattern3))
        assert pattern5 != null
        assert pattern5 instanceof OptionsAnnotationPattern
        assert pattern5.children.size() == 2
        assert pattern5.children[0] == pattern1
        assert pattern5.children[1] instanceof OptionsAnnotationPattern
        assert pattern5.children[1].children[0] == pattern2
        assert pattern5.children[1].children[1] == pattern3
    }

    @Test
    void testMixedPatterns() {
        AnnotationPattern p1 = $A(Token, [pos:'NN', text:'/Foo/'])
        AnnotationPattern p2 = $A(Sentence, [text:'/The coffee is great./'])
        AnnotationPattern p3 = $A(NamedEntityMention, [cui:'C01'])

        def pattern = p1 | p2 & p3
        assert pattern instanceof OptionsAnnotationPattern
        assert pattern.children[0] instanceof AtomicAnnotationPattern
        assert pattern.children[1] instanceof SequenceAnnotationPattern

        pattern = (p1 | p2) & p3
        assert pattern instanceof SequenceAnnotationPattern
        assert pattern.children[0] instanceof OptionsAnnotationPattern
        assert pattern.children[1] instanceof AtomicAnnotationPattern

        pattern = p1 & p2 & p3 & p1 | p2 & p3
        assert pattern instanceof OptionsAnnotationPattern
        assert pattern.children.size() == 2
        assert pattern.children[0] instanceof SequenceAnnotationPattern
        assert pattern.children[0].children.size() == 4
        assert pattern.children[1] instanceof SequenceAnnotationPattern
        assert pattern.children[1].children.size() == 2

        pattern = p1 & p2 & p3 & (p1 | p2) & p3
        assert pattern instanceof SequenceAnnotationPattern
        assert pattern.children.size() == 5
        assert pattern.children[0] instanceof AtomicAnnotationPattern
        assert pattern.children[1] instanceof AtomicAnnotationPattern
        assert pattern.children[2] instanceof AtomicAnnotationPattern
        assert pattern.children[3] instanceof OptionsAnnotationPattern
        assert pattern.children[4] instanceof AtomicAnnotationPattern

    }
}
