package gov.va.queri.dsl

import gov.va.queri.types.NamedEntityMention
import gov.va.queri.types.Token
import groovy.util.logging.Log4j
import org.junit.Test

class PatternElement {
    public PatternElement or(PatternElement b) {
        return new PatternElement()
    }
    public PatternElement and(PatternElement b) {
        return new PatternElement()
    }
    public PatternElement plus(PatternElement b) {
        return new PatternElement()
    }
    public PatternElement multiply(PatternElement b) {
        return new PatternElement()
    }
    public PatternElement positive(PatternElement b) {
        return this
    }
    public PatternElement negative(PatternElement b) {
        return this
    }
}

@Log4j
class UIMA_DSL2_Test {
    @Test
    public void scratch() {
        def node = new NodeBuilder().regex() {
            atom(name:'t1', 'type':Token, text:/the/, pos:'DT')
            atom(name:'t2', 'type':Token, text:/cat/, pos:'NN')
            atom(name:'n1', 'type':NamedEntityMention, code:'C01')
            seqs(name:'s1', range:[0,1]) {
                atom(Token, range:[0,4])
                opts(NamedEntityMention)
            }
        }

        assert node
        assert node.children().size() == 4
        assert node.atom.size() == 3
        assert node.seqs.size() == 1
        assert node.seqs.atom.size() == 1
        assert node.seqs.opts.size() == 1

        println node.atom.@type
        println node.atom.@name
    }

    @Test public void testOr() {
        PatternElement a = new PatternElement()
        PatternElement b = new PatternElement()
        PatternElement c = (a|b)
        assert c != null
    }

    @Test public void testAnd() {
        PatternElement a = new PatternElement()
        PatternElement b = new PatternElement()
        PatternElement c = (a&b)
        assert c != null
    }
    @Test public void testPlus() {
        PatternElement a = new PatternElement()
        PatternElement b = new PatternElement()
        PatternElement c = (a+b)
        assert c != null
    }
    @Test public void testMultiply() {
        PatternElement a = new PatternElement()
        PatternElement b = new PatternElement()
        PatternElement c = (a*b)
        assert c != null
    }
    @Test public void testPositive() {
        PatternElement a = new PatternElement()
        PatternElement b = +a
        assert b != null
    }
    @Test public void testNegative() {
        PatternElement a = new PatternElement()
        PatternElement b = -a
        assert b != null
    }
    @Test public void testEquation() {
        PatternElement a = new PatternElement()
        PatternElement b = new PatternElement()
        PatternElement c = new PatternElement()
        PatternElement d = (a + b) * c + (b * a)
        assert d != null
    }
}
