package clinicalnlp.dict.ae

import clinicalnlp.dict.DictModel
import clinicalnlp.dict.DictModelFactory
import gov.va.vinci.leo.ae.LeoBaseAnnotator
import gov.va.vinci.leo.descriptors.LeoConfigurationParameter
import groovy.util.logging.Log4j
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.tokenize.TokenizerModel
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceInitializationException
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource

@Log4j
public class LeoDictAnnotator extends LeoBaseAnnotator {
    @LeoConfigurationParameter(mandatory = true)
    protected String dictionaryPath;

    @LeoConfigurationParameter(mandatory = true)
    protected String tokenModelPath;

    @LeoConfigurationParameter(mandatory = true)
    private String containerClassName

    @LeoConfigurationParameter(mandatory = true)
    private String tokenClassName

    @LeoConfigurationParameter(mandatory = false)
    private Float tolerance = 0

    @LeoConfigurationParameter(mandatory = false)
    private Boolean longestMatch = false

    @LeoConfigurationParameter(mandatory = false)
    private Boolean caseInsensitive = false

    @LeoConfigurationParameter(mandatory = false)
    private String dictionaryType = DictModelFactory.DICT_MODEL_TYPE_TRIE

    @LeoConfigurationParameter(mandatory = false)
    private String postScriptFile

    private DictModel dict;
    private Script postScript;

    public LeoDictAnnotator setDictionaryPath(String dictionaryPath) {
        this.dictionaryPath = dictionaryPath
        return this
    }

    public LeoDictAnnotator setDictionaryType(String dictionaryType) {
        this.dictionaryType = dictionaryType
        return this
    }

    public LeoDictAnnotator setTokenModelPath(String tokenModelPath) {
        this.tokenModelPath = tokenModelPath
        return this
    }

    public LeoDictAnnotator setContainerClassName(String containerClassName) {
        this.containerClassName = containerClassName
        return this
    }

    public LeoDictAnnotator setTokenClassName(String tokenClassName) {
        this.tokenClassName = tokenClassName
        return this
    }

    public LeoDictAnnotator setPostScriptFile(String postScriptFile) {
        this.postScriptFile = postScriptFile
        return this
    }

    public LeoDictAnnotator setTolerance(Float tolerance) {
        this.tolerance = tolerance
        return this
    }

    public LeoDictAnnotator setLongestMatch(Boolean longestMatch) {
        this.longestMatch = longestMatch
        return this
    }

    public LeoDictAnnotator setCaseInsensitive(Boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive
        return this
    }

    DictAnnotatorImpl impl;

    @Override
    void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext)

        // TODO: use external resource, same as one supplied to tokenizer annotator
        DefaultResourceLoader loader = new DefaultResourceLoader(ClassLoader.getSystemClassLoader());
        Resource resource = loader.getResource('classpath:clinicalnlp/models/en-token.bin');

        this.impl = new DictAnnotatorImpl()
        this.impl.initialize(this.dictionaryPath,
                this.dictionaryType,
                new TokenizerME(new TokenizerModel(resource.getInputStream())),
                this.caseInsensitive,
                this.postScriptFile)
    }

    @Override
    void annotate(JCas jcas) throws AnalysisEngineProcessException {

        this.impl.process(jcas,
                this.longestMatch,
                this.caseInsensitive,
                this.tolerance,
                this.containerClassName,
                this.tokenClassName)
    }
}
