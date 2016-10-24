package clinicalnlp.sent.ae

import groovy.util.logging.Log4j
import opennlp.tools.sentdetect.SentenceDetectorME
import opennlp.uima.sentdetect.SentenceModelResource
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.fit.descriptor.ExternalResource
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceInitializationException

@Log4j
class LocalSentenceDetector extends JCasAnnotator_ImplBase {

    public static final String SD_SEGMENTS_TO_PARSE = 'segmentsToParse'
    @ConfigurationParameter(name = 'segmentsToParse', mandatory = false,
            description = 'Script providing input segments')
    private String segmentsToParse;

    public static final String PARAM_SPLIT_PATTERN = 'splitPatternStr'
    @ConfigurationParameter(name = 'splitPatternStr', mandatory = false,
            description = 'Characters to split on')
    private String splitPatternStr;

    public static final String ANCHOR_TYPES = 'anchorTypes'
    @ConfigurationParameter(name = 'anchorTypes', mandatory = false,
            description = 'Anchor types for anchored sentence detection')
    private String[] anchorTypes;

    public static final String SPAN_SIZE = 'spanSize'
    @ConfigurationParameter(name = 'spanSize', mandatory = true,
            defaultValue = '150',
            description = 'Span size for anchored sentence')
    private Integer spanSize;

    public static final String SENT_MODEL_KEY = 'sent_model'
	@ExternalResource(key = 'sent_model')
	private SentenceModelResource modelResource;

    private SentenceDetectorImpl impl

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext)
        try {
            SentenceDetectorME sentDetect = new SentenceDetectorME(modelResource.getModel())
            this.impl = new SentenceDetectorImpl(sentDetect,
                    this.splitPatternStr,
                    this.segmentsToParse,
                    this.spanSize,
                    this.anchorTypes)
        } catch (Exception ace) {
            throw new ResourceInitializationException(ace);
        }
    }

    @Override
    public void process(JCas jcas) throws AnalysisEngineProcessException {
        this.impl.process(jcas)
    }
}
