package clinicalnlp.pattern.ae

import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Segment
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
import gov.va.vinci.leo.window.types.Window
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.jcas.JCas

import java.util.regex.Matcher

class TestAnnotator extends JCasAnnotator_ImplBase {
    @Override
    void process(JCas jCas) throws AnalysisEngineProcessException {
        String text = jCas.documentText
        jCas.create(type: Segment, begin: 0, end: text.length())
        jCas.create(type: Sentence, begin: 0, end: text.length())
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
