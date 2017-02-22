package clinicalnlp.dict.ae

import groovy.util.logging.Log4j
import opennlp.tools.tokenize.TokenizerME
import opennlp.uima.tokenize.TokenizerModelResource
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.fit.descriptor.ExternalResource
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceInitializationException

@Log4j
class LocalDictAnnotator extends JCasAnnotator_ImplBase {

	public static final String TOKEN_MODEL_KEY = 'token_model'
	@ExternalResource(key = 'token_model')
	TokenizerModelResource tokenizerModelResource

	public static final String PARAM_DICTIONARY_PATH = 'dictionaryPath'
	@ConfigurationParameter(name='dictionaryPath', mandatory=true)
	private String dictionaryPath

	public static final String PARAM_DICTIONARY_TYPE = 'dictionaryType'
	@ConfigurationParameter(name='dictionaryType', mandatory=true)
	private String dictionaryType

	public static final String PARAM_CONTAINER_CLASS = 'containerClassName'
	@ConfigurationParameter(name='containerClassName', mandatory=true)
	private String containerClassName

	public static final String PARAM_TOKEN_CLASS = 'tokenClassName'
	@ConfigurationParameter(name='tokenClassName', mandatory=true)
	private String tokenClassName

	public static final String PARAM_TOLERANCE = 'tolerance'
	@ConfigurationParameter(name='tolerance', mandatory=true, defaultValue='0.0')
	private Float tolerance

	public static final String PARAM_MAX_DISTANCE = 'maxDistance'
	@ConfigurationParameter(name='maxDistance', mandatory=true, defaultValue='0')
	private Integer maxDistance

	public static final String PARAM_CASE_INSENSITIVE = 'caseInsensitive'
	@ConfigurationParameter(name='caseInsensitive', mandatory=true, defaultValue='false')
	private Boolean caseInsensitive

	public static final String PARAM_INIT_SCRIPT_FILE = 'initScriptFile'
	@ConfigurationParameter(name = 'initScriptFile', mandatory = false,
		description = 'Script for execution before dictionary runs')
	private String initScriptFile

	public static final String PARAM_POST_SCRIPT_FILE = 'postScriptFile'
	@ConfigurationParameter(name = 'postScriptFile', mandatory = false,
			description = 'Script for execution after dictionary runs')
	private String postScriptFile

	DictAnnotatorImpl impl

    @Override
	void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context)
        this.impl = new DictAnnotatorImpl()
        this.impl.initialize(this.dictionaryPath,
			this.dictionaryType,
			new TokenizerME(this.tokenizerModelResource.model),
			this.caseInsensitive,
			this.initScriptFile,
			this.postScriptFile)
    }

	@Override
	void process(JCas jcas) throws AnalysisEngineProcessException {
        this.impl.process(jcas,
			this.caseInsensitive,
			this.tolerance,
			this.maxDistance,
			this.containerClassName,
			this.tokenClassName)
	}
}
