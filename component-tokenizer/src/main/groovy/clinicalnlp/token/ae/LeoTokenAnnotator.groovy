package clinicalnlp.token.ae

import gov.va.vinci.leo.ae.LeoBaseAnnotator
import gov.va.vinci.leo.descriptors.LeoConfigurationParameter
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription
import groovy.util.logging.Log4j
import opennlp.tools.lemmatizer.Lemmatizer
import opennlp.tools.lemmatizer.LemmatizerME
import opennlp.tools.lemmatizer.LemmatizerModel
import opennlp.tools.postag.POSTagger
import opennlp.tools.postag.POSTaggerME
import opennlp.tools.stemmer.Stemmer
import opennlp.tools.stemmer.snowball.SnowballStemmer
import opennlp.tools.tokenize.Tokenizer
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.tokenize.TokenizerModel
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceAccessException
import org.apache.uima.resource.ResourceInitializationException
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource

@Log4j
class LeoTokenAnnotator extends LeoBaseAnnotator {

    @LeoConfigurationParameter(mandatory = true)
	protected String tokenModelPath

    @LeoConfigurationParameter(mandatory = false)
    protected String posModelPath

    @LeoConfigurationParameter(mandatory = true)
    private String containerTypeName = 'org.apache.uima.jcas.tcas.DocumentAnnotation'

    @LeoConfigurationParameter(mandatory = true)
    private String tokenTypeName = 'clinicalnlp.types.Token'

    @LeoConfigurationParameter(mandatory = false)
    protected String splitPatternStr

    @LeoConfigurationParameter(mandatory = false)
    protected String lemmatizerDict

    @LeoConfigurationParameter(mandatory = false)
    protected Boolean useStemmer

    LeoTokenAnnotator setTokenModelPath(String tokenModelPath) {
        this.tokenModelPath = tokenModelPath
        return this
    }

    LeoTokenAnnotator setPosModelPath(String posModelPath) {
        this.posModelPath = posModelPath
        return this
    }

    LeoTokenAnnotator setContainerTypeName(String containerTypeName) {
        this.containerTypeName = containerTypeName
        return this
    }

    LeoTokenAnnotator setTokenTypeName(String tokenTypeName) {
        this.tokenTypeName = tokenTypeName
        return this
    }

    LeoTokenAnnotator setSplitPattern(String splitPatternStr) {
        this.splitPatternStr = splitPatternStr
        return this
    }

    LeoTokenAnnotator setLemmatizerDict(String lemmatizerDict) {
        this.lemmatizerDict = lemmatizerDict
        return this
    }

    LeoTokenAnnotator setUseStemmer(Boolean useStemmer) {
        this.useStemmer = useStemmer
        return this
    }

    private TokenAnnotatorImpl impl;

    @Override
	void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext)
        Tokenizer tokenizer
        POSTagger posTagger
        Lemmatizer lemmatizer
        Stemmer stemmer

        try {
            DefaultResourceLoader loader = new DefaultResourceLoader(ClassLoader.getSystemClassLoader())
            Resource tokenModelResource = loader.getResource(this.tokenModelPath)
            tokenizer = new TokenizerME(new TokenizerModel(tokenModelResource.getInputStream()))
            if (this.useStemmer) {
                stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH)
            }
            if (this.posModelPath) {
                Resource posModelResource = loader.getResource(this.posModelPath)
                posTagger = new POSTaggerME(posModelResource.getInputStream())
            }
            if (this.lemmatizerDict) {
//                InputStream is = getClass().getResourceAsStream(this.lemmatizerDict)
//                LemmatizerModel lemmatizerModel = new LemmatizerModel(is)
//                lemmatizer = new LemmatizerME(lemmatizerModel)
//                is.close()
            }
        } catch (ResourceAccessException e) {
            throw new ResourceInitializationException(e)
        }

        this.impl = new TokenAnnotatorImpl(
            tokenizer,
            posTagger,
            lemmatizer,
            stemmer,
            this.containerTypeName,
            this.tokenTypeName,
            this.splitPatternStr
        )
    }

	@Override
	void annotate(JCas aJCas) throws AnalysisEngineProcessException {
        this.impl.process(aJCas)
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