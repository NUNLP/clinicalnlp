package clinicalnlp.pattern

import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
import groovy.util.logging.Log4j
import org.apache.log4j.Level
import org.apache.log4j.PropertyConfigurator
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import static clinicalnlp.pattern.PatternElement.G
import static clinicalnlp.pattern.PatternElement.N

@Log4j
class SratchTest {
    @BeforeClass
    static void setupClass() {
        def config = new ConfigSlurper().parse(new File('src/test/resources/config.groovy').toURL())
        PropertyConfigurator.configure(config.toProperties())
    }

    @Before
    void setup() {
        log.level = Level.INFO
        log.info '->running test'
    }

    @Test
    void scratch() {
        def node = new NodeBuilder().regex() {
            elem(name:'t1', 'type':Token, text:/the/, pos:'DT')
            elem(name:'t2', 'type':Token, text:/cat/, pos:'NN')
            elem(name:'n1', 'type':NamedEntityMention, code:'C01')
            seqs(name:'s1', range:[0,1]) {
                elem(Token, range:[0,4])
                opts(NamedEntityMention)
            }
        }

        assert node
        assert node.children().size() == 4
        assert node.elem.size() == 3
        assert node.seqs.size() == 1
        assert node.seqs.elem.size() == 1
        assert node.seqs.opts.size() == 1

//        println node.elem.@type
//        println node.elem.@name
    }

    @Test void testOr() {
        def p = N(type:NamedEntityMention, name:'foo') |
                N(type:NamedEntityMention, name:'bar')
        assert p != null
    }

    @Test void testAnd() {
        def p = N(type:NamedEntityMention, name:'foo') &
                N(type:NamedEntityMention, name:'bar')
        assert p != null
    }

    @Test void testPlus() {
        def p = N(type:NamedEntityMention, name:'foo') +
                N(type:NamedEntityMention, name:'bar')
        assert p != null
    }

    @Test void testMultiply() {
        def p = N(type:NamedEntityMention, name:'foo') *
                N(type:NamedEntityMention, name:'bar')
        assert p != null
    }

    @Test void testMultiply2() {
        def p = N(type:NamedEntityMention, name:'foo') * (0..5)
        assert p != null
    }

    @Test void testPositive() {
        def p = +N(type:NamedEntityMention, name:'foo')
        assert p != null
    }

    @Test void testNegative() {
        def p = -N(type:NamedEntityMention, name:'foo')
        assert p != null
    }

    @Test void testEquation() {
        def a = N(type:NamedEntityMention, name:'foo1')
        def b = N(type:NamedEntityMention, name:'foo2')
        def c = N(type:NamedEntityMention, name:'foo3')
        def d = (a + b) * c + (b * a)
        assert d != null
    }

    @Test void testPattern() {
        def p1 = (N(type: NamedEntityMention, name: 'finding', text: /tubular\s+adenoma/) &
                N(type: Token, range: [0, 2]) &
                N(type: Token, name: 'seen', text: /seen/, feats: [pos: /V../]) &
                N(type: Token, name: 'tokens', text: /in|the/, range: [0, 2]) &
                N(type: NamedEntityMention, name: 'site', text: /sigmoid\s+colon/, feats: [code: /C.2/])
        )
        assert p1 instanceof PatternElement

        // positive lookahead
        def p2 = N(type: Token, name: 'tokens') * 5 &
                +N(type: NamedEntityMention, name: 'nem2', feats: [code: /C02/])
        assert p2 instanceof PatternElement

        // negative lookahead
        def p3 = N(type: Token, name: 'tokens') * (3..5) &
                -N(type: NamedEntityMention, name: 'nem2', feats: [code: /C02/])
        assert p3 instanceof PatternElement

        // positive lookaround
        def p4 = +N(type: NamedEntityMention, text: /.{0,200}/, name: 'nem1', feats: [code: /C0./]) &
                N(type: Token, name: 'tokens', range: [1, 5]) &
                +N(type: NamedEntityMention, name: 'nem2', feats: [code: /C03/])

        // groups
        //noinspection GroovyAssignabilityCheck
        G('g1', N(type: Token, name: 't1') &
                N(type: NamedEntityMention, name: 'nem1'))*(1..5) &
                G('g2', N(type: Token, name: 't2') &
                        N(type: NamedEntityMention, name: 'nem2'))*(1..5)

        // unions (alternatives)
        //noinspection GroovyAssignabilityCheck
        G('g1', (
                N(type: NamedEntityMention, name: 'nem1', feats: [code: 'C01']) |
                N(type: NamedEntityMention, name: 'nem2', feats: [code: 'C02'])
        ) & N(type: Token, name: 't1') * (1..5) & N(type: NamedEntityMention, name: 'nem3'))

        //noinspection GroovyAssignabilityCheck
        G('g1', N(type: NamedEntityMention) | N(type: Token) | N(type: Sentence)) &
                G('g2', N(type: Token) * (1..5) & N(type: NamedEntityMention))

        // use parens to modify operator order
        log.info 'first try'
        N(type: NamedEntityMention) + N(type: NamedEntityMention) | N(type: NamedEntityMention)
        log.info 'second try'
        N(type: NamedEntityMention) + (N(type: NamedEntityMention) | N(type: NamedEntityMention))
    }

    @Test
    void testPrecedence() {
        N(type: Token, name: 't1') & N(type: Token, name: 't2') | N(type: Token, name: 't3') & N(type: Token, name: 't4')
    }
}
