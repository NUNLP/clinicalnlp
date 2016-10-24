package clinicalnlp.token.ae

import groovy.util.logging.Log4j
import opennlp.tools.lemmatizer.SimpleLemmatizer
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
public final class LocalTokenAnnotator extends JCasAnnotator_ImplBase {
    public static final String TOKEN_MODEL_KEY = 'token_model'
    @ExternalResource(key = 'token_model', mandatory = true)
    TokenizerModelResource tokenModelResource;

    public static final String POS_MODEL_KEY = 'pos_model'
    @ExternalResource(key = 'pos_model', mandatory=false)
    POSModelResource posModelResource;

    public static final String PARAM_CONTAINER_TYPE = 'containerTypeName'
    @ConfigurationParameter(name = 'containerTypeName', mandatory = false,
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

    private TokenAnnotatorImpl impl;

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context)

        Tokenizer tokenizer;
        POSTagger posTagger;
        SimpleLemmatizer lemmatizer;
        Stemmer stemmer;

        try {
            tokenizer = new TokenizerME(tokenModelResource.getModel())
            if (this.useStemmer) {
                stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH)
            }
            if (posModelResource) {
                posTagger = new POSTaggerME( posModelResource.getModel(), POSTaggerME.DEFAULT_BEAM_SIZE, 0)
            }
            if (this.lemmatizerDict) {
                InputStream is = getClass().getResourceAsStream(this.lemmatizerDict)
                lemmatizer = new SimpleLemmatizer(is)
                is.close()
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
            this.splitPatternStr
        )
    }

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        this.impl.process(aJCas)
    }
}
