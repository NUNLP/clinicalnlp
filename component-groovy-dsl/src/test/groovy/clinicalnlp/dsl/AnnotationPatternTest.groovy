package clinicalnlp.dsl

import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.TextSpan
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

@Log4j
class AnnotationPatternTest {
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
        Class.forName('clinicalnlp.dsl.UIMA_DSL')
    }

    @Before
    void setUp() throws Exception {
        log.setLevel(Level.INFO)
    }


    @Test
    void smokeTest() {
        //--------------------------------------------------------------------------------------------------------------
        // generate some annotations
        //--------------------------------------------------------------------------------------------------------------
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(TestAnnotator))
        }
        AnalysisEngine engine = builder.createAggregate()

        String text = 'Tubular adenoma was seen in the sigmoid colon'
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)

        SimplePipeline.runPipeline(jcas, engine)

        //--------------------------------------------------------------------------------------------------------------
        // execute an annotation matcher from a node tree
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationPattern am = new AnnotationPattern(
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

        //--------------------------------------------------------------------------------------------------------------
        // validate results
        //--------------------------------------------------------------------------------------------------------------
        int bindingCount = 0
        am.matcher(jcas.select(type:Window)[0]).each { Binding b ->
            bindingCount++
        }
        assert bindingCount == 1
        Iterator iter = am.matcher(jcas.select(type:Window)[0])
        assert iter.hasNext()
        Binding binding = iter.next()
        assert binding != null
        assert !iter.hasNext()
        NamedEntityMention finding = binding.getVariable('finding')[0]
        assert finding != null
        assert finding.coveredText ==~ /(?i)tubular\s+adenoma/
        NamedEntityMention site = binding.getVariable('site')[0]
        assert site != null
        assert site.coveredText ==~ /sigmoid\s+colon/
        Token token = binding.getVariable('tokens')[0]
        assert token != null
        assert token.coveredText == 'in'
        token = binding.getVariable('tokens')[1]
        assert token != null
        assert token.coveredText == 'the'
    }

    @Test
    void testLookAround() {
        //--------------------------------------------------------------------------------------------------------------
        // generate some annotations
        //--------------------------------------------------------------------------------------------------------------
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(TestAnnotator))
        }
        AnalysisEngine engine = builder.createAggregate()

        String text = 'Tubular adenoma was seen in the sigmoid colon, which was free of other polyps'

        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)

        SimplePipeline.runPipeline(jcas, engine)

        //--------------------------------------------------------------------------------------------------------------
        // execute an annotation matcher from a node tree
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationPattern am = new AnnotationPattern(
            new NodeBuilder().regex (caseInsensitive:true) {
                include(type:NamedEntityMention, feats:['code'])
                include(type:Token)
                lookBehind(positive:true) {
                    node(type:NamedEntityMention, text:/.{0,200}/, name:'nem1', feats:[code:/C0./])
                }
                match {
                    node(type:Token, name:'tokens', range:[1,5])
                }
                lookAhead(positive:false) {
                    node(type:NamedEntityMention, name:'nem2', feats:[code:/C03/])
                }
            }
        )

        //--------------------------------------------------------------------------------------------------------------
        // validate results
        //--------------------------------------------------------------------------------------------------------------
        Iterator iter = am.matcher(jcas.select(type:Window)[0])
        assert iter.hasNext()
        Binding binding = iter.next()
        assert binding != null
        NamedEntityMention nem1 = binding.getVariable('nem1')[0]
        assert nem1 != null && nem1.coveredText == /Tubular adenoma/
        assert !binding.hasVariable('nem2')
        List<Token> tokens = binding.getVariable('tokens')
        assert tokens.size() == 4
        assert tokens[0].coveredText == 'was'
        assert tokens[1].coveredText == 'seen'
        assert tokens[2].coveredText == 'in'
        assert tokens[3].coveredText == 'the'
        assert iter.hasNext()
        binding = iter.next()
        assert binding != null
        nem1 = binding.getVariable('nem1')[0]
        assert nem1 != null && nem1.coveredText == /sigmoid colon/
        NamedEntityMention nem2 = binding.getVariable('nem2')[0]
        assert nem2 != null && nem2.coveredText == /polyps/
        tokens = binding.getVariable('tokens')
        assert tokens.size() == 4
        assert tokens[0].coveredText == 'which'
        assert tokens[1].coveredText == 'was'
        assert tokens[2].coveredText == 'free'
        assert tokens[3].coveredText == 'of'
    }

    @Test
    void testGroups() {
        //--------------------------------------------------------------------------------------------------------------
        // generate some annotations
        //--------------------------------------------------------------------------------------------------------------
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(TestAnnotator))
        }
        AnalysisEngine engine = builder.createAggregate()

        String text = 'Tubular adenoma was seen in the sigmoid colon, which was free of other polyps'

        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)

        SimplePipeline.runPipeline(jcas, engine)

        //--------------------------------------------------------------------------------------------------------------
        // execute an annotation matcher from a node tree
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationPattern am = new AnnotationPattern(
            new NodeBuilder().regex (caseInsensitive:true) {
                include(type:NamedEntityMention)
                include(type:Token)
                match {
                    node(type:'group', name:'conj1') {
                        node(type:Token, name:'t1', range:[1,5])
                        node(type:NamedEntityMention, name:'nem1')
                    }
                    node(type:'group', name:'conj2', range:[0,1]) {
                        node(type:Token, name:'t2', range:[1,5])
                        node(type:NamedEntityMention, name:'nem2')
                    }
                }
            }
        )

        //--------------------------------------------------------------------------------------------------------------
        // validate results
        //--------------------------------------------------------------------------------------------------------------
        Iterator iter = am.matcher(jcas.select(type:Window)[0])
        assert iter.hasNext()
        Binding binding = iter.next()
        assert binding != null

        List<Token> t1 = binding.getVariable('t1')
        assert t1.size() == 4
        assert t1[0].coveredText == 'was'
        assert t1[1].coveredText == 'seen'
        assert t1[2].coveredText == 'in'
        assert t1[3].coveredText == 'the'

        List<Token> t2 = binding.getVariable('t2')
        assert t2.size() == 5
        assert t2[0].coveredText == 'which'
        assert t2[1].coveredText == 'was'
        assert t2[2].coveredText == 'free'
        assert t2[3].coveredText == 'of'
        assert t2[4].coveredText == 'other'

        NamedEntityMention nem1 = binding.getVariable('nem1')[0]
        assert nem1 != null && nem1.code == 'C02'
        NamedEntityMention nem2 = binding.getVariable('nem2')[0]
        assert nem2 != null && nem2.code == 'C03'

        List<Annotation> conj1 = binding.getVariable('conj1')
        assert conj1 && conj1.size() == 5
        assert conj1[0].coveredText == 'was'
        assert conj1[1].coveredText == 'seen'
        assert conj1[2].coveredText == 'in'
        assert conj1[3].coveredText == 'the'
        assert conj1[4].coveredText == 'sigmoid colon'

        List<Annotation> conj2 = binding.getVariable('conj2')
        assert conj2 && conj2.size() == 6
        assert conj2[0].coveredText == 'which'
        assert conj2[1].coveredText == 'was'
        assert conj2[2].coveredText == 'free'
        assert conj2[3].coveredText == 'of'
        assert conj2[4].coveredText == 'other'
        assert conj2[5].coveredText == 'polyps'

        assert !iter.hasNext()
    }

    @Test
    void testUnions() {
        //--------------------------------------------------------------------------------------------------------------
        // generate some annotations
        //--------------------------------------------------------------------------------------------------------------
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(TestAnnotator))
        }
        AnalysisEngine engine = builder.createAggregate()

        String text = 'Tubular adenoma was seen in the sigmoid colon, the sigmoid colon was free of other polyps'

        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)

        SimplePipeline.runPipeline(jcas, engine)

        //--------------------------------------------------------------------------------------------------------------
        // execute an annotation matcher from a node tree
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationPattern am = new AnnotationPattern(
            new NodeBuilder().regex (caseInsensitive:true) {
                include(type:NamedEntityMention, feats:['code'])
                include(type:Token)
                match {
                    node(type:'union', name:'opt') {
                        node(type:NamedEntityMention, name:'nem1', feats:[code:'C01'])
                        node(type:NamedEntityMention, name:'nem2', feats:[code:'C02'])
                    }
                    node(type:'group', name:'conj1') {
                        node(type:Token, name:'t1', range:[1,5])
                        node(type:NamedEntityMention, name:'nem3')
                    }
                }
            }
        )

        //--------------------------------------------------------------------------------------------------------------
        // validate results
        //--------------------------------------------------------------------------------------------------------------
        Iterator iter = am.matcher(jcas.select(type:Window)[0])
        assert iter.hasNext()
        Binding binding = iter.next()
        assert binding != null

        List<Token> opt = binding.getVariable('opt')
        assert opt.size() == 1
        assert opt[0].coveredText == 'Tubular adenoma'

        NamedEntityMention nem1 = binding.getVariable('nem1')[0]
        assert nem1 != null && nem1.code == 'C01'
        assert !binding.hasVariable('nem2')

        List<Annotation> conj1 = binding.getVariable('conj1')
        assert conj1 && conj1.size() == 5
        assert conj1[0].coveredText == 'was'
        assert conj1[1].coveredText == 'seen'
        assert conj1[2].coveredText == 'in'
        assert conj1[3].coveredText == 'the'
        assert conj1[4].coveredText == 'sigmoid colon'
    }

    @Test
    void testGreedyVsLazy() {
        //--------------------------------------------------------------------------------------------------------------
        // generate some annotations
        //--------------------------------------------------------------------------------------------------------------
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(TestAnnotator))
        }
        AnalysisEngine engine = builder.createAggregate()

        String text = 'Tubular adenoma was seen in the sigmoid colon'

        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)

        SimplePipeline.runPipeline(jcas, engine)

        //--------------------------------------------------------------------------------------------------------------
        // execute a *lazy* annotation matcher from a node tree
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationPattern am = new AnnotationPattern(
            new NodeBuilder().regex (caseInsensitive:true) {
                include(type:NamedEntityMention)
                include(type:Token)
                match {
                    node(type:NamedEntityMention, name:'nem1')
                    node(type:Token, greedy:false, name:'t1', range:[1,4])
                }
            }
        )

        //--------------------------------------------------------------------------------------------------------------
        // validate results
        //--------------------------------------------------------------------------------------------------------------
        Iterator iter = am.matcher(jcas.select(type:Window)[0])
        assert iter.hasNext()
        Binding binding = iter.next()
        assert binding != null

        NamedEntityMention nem1 = binding.getVariable('nem1')[0]
        assert nem1 != null && nem1.code == 'C01'

        List<Token> t1 = binding.getVariable('t1')
        assert t1.size() == 1
        assert t1[0].coveredText == 'was'

        //--------------------------------------------------------------------------------------------------------------
        // execute a *greedy* annotation matcher from a node tree
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        am = new AnnotationPattern(
            new NodeBuilder().regex (caseInsensitive:true) {
                include(type:NamedEntityMention)
                include(type:Token)
                match {
                    node(type:NamedEntityMention, name:'nem1')
                    node(type:Token, greedy:true, name:'t1', range:[1,4])
                }
            }
        )

        //--------------------------------------------------------------------------------------------------------------
        // validate results
        //--------------------------------------------------------------------------------------------------------------
        iter = am.matcher(jcas.select(type:Window)[0])
        assert iter.hasNext()
        binding = iter.next()
        assert binding != null

        nem1 = binding.getVariable('nem1')[0]
        assert nem1 != null && nem1.code == 'C01'

        t1 = binding.getVariable('t1')
        assert t1.size() == 4
        assert t1[0].coveredText == 'was'
        assert t1[1].coveredText == 'seen'
        assert t1[2].coveredText == 'in'
        assert t1[3].coveredText == 'the'
    }

    @Test
    void testIncludeText() {
        //--------------------------------------------------------------------------------------------------------------
        // generate some annotations
        //--------------------------------------------------------------------------------------------------------------
        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(createEngineDescription(TestAnnotator))
        }
        AnalysisEngine engine = builder.createAggregate()

        String text = 'Tubular adenoma was seen in the sigmoid colon'

        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)

        SimplePipeline.runPipeline(jcas, engine)

        //--------------------------------------------------------------------------------------------------------------
        // create a TextSpan annotation between two NamedEntityMention annotations
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        AnnotationPattern am = new AnnotationPattern(
            new NodeBuilder().regex (caseInsensitive:true) {
                include(type:NamedEntityMention, feats:['code'])
                match {
                    node(type:NamedEntityMention, name:'nem1', code:'C01')
                    node(type:NamedEntityMention, name:'nem2', code:'C02')
                }
            }
        )
        am.matcher(jcas.select(type:Window)[0]).each { Binding binding ->
            NamedEntityMention nem1 = binding.getVariable('nem1')[0]
            NamedEntityMention nem2 = binding.getVariable('nem2')[0]
            jcas.create(type:TextSpan, begin:nem1.end, end:nem2.begin)
        }
        //--------------------------------------------------------------------------------------------------------------
        // create a pattern to capture the text span annotation
        //--------------------------------------------------------------------------------------------------------------
        //noinspection GroovyAssignabilityCheck
        am = new AnnotationPattern(
            new NodeBuilder().regex (caseInsensitive:true, includeText:true) {
                include(type:NamedEntityMention)
                include(type:TextSpan)
                match {
                    node(type:NamedEntityMention, name:'nem1')
                    node(type:TextSpan, name:'t1', text:/\s+was\s+seen\s+in\s+the\s+/)
                    node(type:NamedEntityMention, name:'nem2')
                }
            }
        )

        //--------------------------------------------------------------------------------------------------------------
        // validate results
        //--------------------------------------------------------------------------------------------------------------
        Iterator iter = am.matcher(jcas.select(type:Window)[0])
        assert iter.hasNext()
        Binding binding = iter.next()
        assert binding != null

        NamedEntityMention nem1 = binding.getVariable('nem1')[0]
        assert nem1 != null && nem1.code == 'C01'
        NamedEntityMention nem2 = binding.getVariable('nem2')[0]
        assert nem2 != null && nem2.code == 'C02'
        TextSpan t1 = binding.getVariable('t1')[0]
        assert t1.coveredText == ' was seen in the '
    }
}
