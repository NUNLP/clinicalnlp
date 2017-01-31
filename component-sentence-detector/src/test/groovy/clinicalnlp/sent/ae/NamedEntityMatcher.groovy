package clinicalnlp.sent.ae

import clinicalnlp.types.NamedEntityMention
import gov.va.vinci.leo.ae.LeoBaseAnnotator
import gov.va.vinci.leo.descriptors.LeoConfigurationParameter
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription
import groovy.util.logging.Log4j2
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceInitializationException

import java.util.regex.Matcher
import java.util.regex.Pattern

@Log4j2
class NamedEntityMatcher extends LeoBaseAnnotator {
    public static final String PATTERN = 'patternStr'

    @LeoConfigurationParameter(mandatory = false)
    protected String patternStr;

    private Pattern pattern;

    NamedEntityMatcher setPatternStr(String patternStr) {
        this.patternStr = patternStr
        return this
    }

    @Override
    void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context)
        this.pattern = Pattern.compile(this.patternStr)
    }

    @Override
    void annotate(JCas jcas) throws AnalysisEngineProcessException {
        Matcher matcher = jcas.documentText =~ this.pattern
        matcher.each {
            NamedEntityMention nem = new NamedEntityMention(jcas)
            nem.begin = matcher.start(1)
            nem.end = matcher.end(1)
            nem.addToIndexes()
        }
    }

    @Override
    LeoTypeSystemDescription getLeoTypeSystemDescription() {
        return super.getLeoTypeSystemDescription()
    }

    @Override
    <T extends LeoBaseAnnotator> T setLeoTypeSystemDescription(LeoTypeSystemDescription typeSystemDescription) {
        return super.setLeoTypeSystemDescription(typeSystemDescription)
    }
}
