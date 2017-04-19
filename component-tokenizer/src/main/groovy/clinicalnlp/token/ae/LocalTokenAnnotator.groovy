package clinicalnlp.token.ae

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
import opennlp.uima.postag.POSModelResource
import opennlp.uima.tokenize.TokenizerModelResource
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.fit.descriptor.ExternalResource
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceAccessException
import org.apache.uima.resource.ResourceInitializationException

@Log4j
final class LocalTokenAnnotator extends JCasAnnotator_ImplBase {
    public static final String TOKEN_MODEL_KEY = 'token_model'
    @ExternalResource(key = 'token_model', mandatory = true)
    TokenizerModelResource tokenModelResource;

    public static final String POS_MODEL_KEY = 'pos_model'
    @ExternalResource(key = 'pos_model', mandatory=false)
    POSModelResource posModelResource;

    public static final String PARAM_TOKEN_TYPE = 'tokenTypeName'
    @ConfigurationParameter(name = 'tokenTypeName', mandatory = true,
        defaultValue = 'clinicalnlp.types.Token')
    private String tokenTypeName;

    public static final String PARAM_CONTAINER_TYPE = 'containerTypeName'
    @ConfigurationParameter(name = 'containerTypeName', mandatory = true,
        defaultValue = 'org.apache.uima.jcas.tcas.DocumentAnnotation')
    private String containerTypeName;

    public static final String PARAM_LEMMATIZER_DICT = 'lemmatizerDict'
    @ConfigurationParameter(name = 'lemmatizerDict', mandatory = false,
        description = 'Dictionary for lemmatizing tokens')
    private String lemmatizerDict;

    public static final String PARAM_SPLIT_PATTERN = 'splitPatternStr'
    @ConfigurationParameter(name = 'splitPatternStr', mandatory = false,
        description = 'Characters to split on')
    private String splitPatternStr;

    public static final String PARAM_USE_STEMMER = 'useStemmer'
    @ConfigurationParameter(name = 'useStemmer', mandatory = false,
        description = 'Flag to use or not use stemmer')
    private Boolean useStemmer;

    private TokenAnnotatorImpl impl

    @Override
    void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context)

        Tokenizer tokenizer;
        POSTagger posTagger;
        Lemmatizer lemmatizer;
        Stemmer stemmer;

        try {
            tokenizer = new TokenizerME(tokenModelResource.getModel())
            if (this.useStemmer) {
                stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH)
            }
            if (posModelResource) {
                posTagger = new POSTaggerME( posModelResource.getModel())
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
    void process(JCas aJCas) throws AnalysisEngineProcessException {
        this.impl.process(aJCas)
    }
}
