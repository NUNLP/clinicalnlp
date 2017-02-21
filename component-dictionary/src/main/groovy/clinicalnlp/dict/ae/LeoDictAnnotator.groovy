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
class LeoDictAnnotator extends LeoBaseAnnotator {
    @LeoConfigurationParameter(mandatory = true)
    protected String dictionaryPath

    @LeoConfigurationParameter(mandatory = true)
    protected String tokenModelPath

    @LeoConfigurationParameter(mandatory = true)
    private String containerClassName

    @LeoConfigurationParameter(mandatory = true)
    private String tokenClassName

    @LeoConfigurationParameter(mandatory = false)
    private Float tolerance = 0

    @LeoConfigurationParameter(mandatory = false)
    private Integer maxRawScore = 0

    @LeoConfigurationParameter(mandatory = false)
    private Boolean caseInsensitive = false

    @LeoConfigurationParameter(mandatory = true)
    private String dictionaryType

    @LeoConfigurationParameter(mandatory = false)
    private String bindingScriptFile

    @LeoConfigurationParameter(mandatory = false)
    private String postScriptFile

    LeoDictAnnotator setDictionaryPath(String dictionaryPath) {
        this.dictionaryPath = dictionaryPath
        return this
    }

    LeoDictAnnotator setDictionaryType(String dictionaryType) {
        this.dictionaryType = dictionaryType
        return this
    }

    LeoDictAnnotator setTokenModelPath(String tokenModelPath) {
        this.tokenModelPath = tokenModelPath
        return this
    }

    LeoDictAnnotator setContainerClassName(String containerClassName) {
        this.containerClassName = containerClassName
        return this
    }

    LeoDictAnnotator setTokenClassName(String tokenClassName) {
        this.tokenClassName = tokenClassName
        return this
    }

    LeoDictAnnotator setBindingScriptFile(String bindingScriptFile) {
        this.bindingScriptFile = bindingScriptFile
        return this
    }

    LeoDictAnnotator setPostScriptFile(String postScriptFile) {
        this.postScriptFile = postScriptFile
        return this
    }

    LeoDictAnnotator setTolerance(Float tolerance) {
        this.tolerance = tolerance
        return this
    }

    LeoDictAnnotator setMaxRawScore(Integer maxRawScore) {
        this.maxRawScore = maxRawScore
        return this
    }

    LeoDictAnnotator setCaseInsensitive(Boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive
        return this
    }

    DictAnnotatorImpl impl;

    @Override
    void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext)

        // TODO: use external resource, same as one supplied to tokenizer annotator
        DefaultResourceLoader loader = new DefaultResourceLoader(ClassLoader.getSystemClassLoader())
        Resource resource = loader.getResource('classpath:clinicalnlp/models/en-token.bin')

        this.impl = new DictAnnotatorImpl()
        this.impl.initialize(this.dictionaryPath,
            this.dictionaryType,
            new TokenizerME(new TokenizerModel(resource.getInputStream())),
            this.caseInsensitive,
            this.bindingScriptFile,
            this.postScriptFile)
    }

    @Override
    void annotate(JCas jcas) throws AnalysisEngineProcessException {
        this.impl.process(jcas,
            this.caseInsensitive,
            this.tolerance,
            this.maxRawScore,
            this.containerClassName,
            this.tokenClassName)
    }
}
