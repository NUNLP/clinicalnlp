package gov.va.queri.sent.ae

import gov.va.vinci.leo.ae.LeoBaseAnnotator
import gov.va.vinci.leo.descriptors.LeoConfigurationParameter
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription
import groovy.util.logging.Log4j
import opennlp.tools.sentdetect.SentenceDetectorME
import opennlp.tools.sentdetect.SentenceModel
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceInitializationException
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource

@Log4j
public class LeoSentenceDetector extends LeoBaseAnnotator {

    @LeoConfigurationParameter(mandatory = false)
	protected String segmentsToParse;

    @LeoConfigurationParameter(mandatory = false)
	protected String splitPatternStr;

    @LeoConfigurationParameter(mandatory = true)
	protected String sentModelPath;

    @LeoConfigurationParameter(mandatory = false)
    protected String[] anchorTypes;

    @LeoConfigurationParameter(mandatory = false)
    protected Integer spanSize = 150;

    private SentenceDetectorImpl impl;

    public LeoSentenceDetector setSegmentsToParse(String segmentsToParse) {
        this.segmentsToParse = segmentsToParse
        return this
    }

    public LeoSentenceDetector setSplitPatternStr(String splitPatternStr) {
        this.splitPatternStr = splitPatternStr
        return this
    }

    public LeoSentenceDetector setSentModelPath(String sentModelPath) {
        this.sentModelPath = sentModelPath
        return this
    }

    public LeoSentenceDetector setAnchorTypes(Collection<String> anchorTypes) {
        this.anchorTypes = anchorTypes.toArray()
        return this
    }

    @Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext)
		DefaultResourceLoader loader = new DefaultResourceLoader(ClassLoader.getSystemClassLoader());
		Resource resource = loader.getResource(this.sentModelPath);
        try {
            SentenceDetectorME sentDetect = new SentenceDetectorME(new SentenceModel(resource.getInputStream()))
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
	public void annotate(JCas jcas) throws AnalysisEngineProcessException {
        this.impl.process(jcas)
	}

	@Override
	LeoTypeSystemDescription getLeoTypeSystemDescription() {
		return super.getLeoTypeSystemDescription()
	}

	@Override
	def <T extends LeoBaseAnnotator> T setLeoTypeSystemDescription(LeoTypeSystemDescription typeSystemDescription) {
		return super.setLeoTypeSystemDescription(typeSystemDescription)
	}
}