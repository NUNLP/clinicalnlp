package clinicalnlp.annotator

import clinicalnlp.types.NamedEntityMention
import clinicalnlp.types.Segment
import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceInitializationException

import java.util.regex.Matcher
import java.util.regex.Pattern

class NamedEntityMentionMatcher extends JCasAnnotator_ImplBase {

    public static final String PARAM_PATTERN = 'patternStr'
    @ConfigurationParameter(name = 'patternStr', mandatory = true,
            description = 'pattern for detecting named entities')
    private String patternStr

    Pattern pattern

    @Override
    void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context)
        this.pattern = Pattern.compile(this.patternStr)
    }


    @Override
    void process(JCas jcas) throws AnalysisEngineProcessException {
        Segment seg = new Segment(jcas)
        seg.begin = 0
        seg.end = jcas.documentText.length()
        seg.addToIndexes()

        Matcher matcher = jcas.documentText =~ /([A-Z].+\.)/
        matcher.each {
            Sentence sent = new Sentence(jcas)
            sent.begin = matcher.start(1)
            sent.end = matcher.end(1)
            sent.addToIndexes()
        }

        matcher = jcas.documentText =~ /([a-zA-Z0-9]+)/
        matcher.each {
            Token token = new Token(jcas)
            token.begin = matcher.start(1)
            token.end = matcher.end(1)
            token.addToIndexes()
        }

        matcher = jcas.documentText =~ this.pattern
        matcher.each {
            NamedEntityMention nem = new NamedEntityMention(jcas)
            nem.begin = matcher.start(1)
            nem.end = matcher.end(1)
            nem.addToIndexes()
        }
    }
}