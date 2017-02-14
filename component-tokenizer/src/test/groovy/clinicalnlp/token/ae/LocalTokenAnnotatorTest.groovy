package clinicalnlp.token.ae

import clinicalnlp.types.Token
import gov.va.vinci.leo.sentence.types.Sentence
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.analysis_engine.AnalysisEngineDescription
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.factory.ExternalResourceFactory
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ExternalResourceDescription
import org.junit.BeforeClass
import org.junit.Test

@Log4j
class LocalTokenAnnotatorTest {

    @BeforeClass
    static void setupClass() {
        BasicConfigurator.configure()
        Class.forName('clinicalnlp.dsl.DSL')
    }

    @Test
    void testBreakIteratorTokenAnnotator() {
        AnalysisEngineDescription desc = AnalysisEngineFactory.createEngineDescription(
                BreakIteratorTokenAnnotator,
                BreakIteratorTokenAnnotator.PARAM_CONTAINER_TYPE,
                'gov.va.vinci.leo.sentence.types.Sentence')

        AnalysisEngine tokenizer = AnalysisEngineFactory.createEngine(desc)
        assert tokenizer != null

        // create a new JCas and seed with sentences
        String text = "There was a tubular adenoma in the sigmoid colon."
        JCas jcas = tokenizer.newJCas()
        jcas.setDocumentText(text)
        jcas.create(type:Sentence, begin:0, end:text.length())

        // apply the tokenizer
        tokenizer.process(jcas)

        // verify number of tokens
        Collection<Token> tokens = jcas.select(type:Token)
        assert tokens.size() == 10
    }
    
    @Test
    void smokeTest() {
		
		ExternalResourceDescription tokenModelRes = ExternalResourceFactory.createExternalResourceDescription(
			 opennlp.uima.tokenize.TokenizerModelResourceImpl, "file:clinicalnlp/models/en-token.bin")

        ExternalResourceDescription posModelRes = ExternalResourceFactory.createExternalResourceDescription(
            opennlp.uima.postag.POSModelResourceImpl, "file:clinicalnlp/models/mayo-pos.zip")
		
        AnalysisEngineDescription desc = AnalysisEngineFactory.createEngineDescription(
            LocalTokenAnnotator,
            LocalTokenAnnotator.PARAM_CONTAINER_TYPE,
            'gov.va.vinci.leo.sentence.types.Sentence',
            LocalTokenAnnotator.TOKEN_MODEL_KEY, tokenModelRes,
            LocalTokenAnnotator.POS_MODEL_KEY, posModelRes,
            LocalTokenAnnotator.PARAM_LEMMATIZER_DICT, '/clinicalnlp/models/en-lemmatizer.dict',
            LocalTokenAnnotator.PARAM_USE_STEMMER, true,
            LocalTokenAnnotator.PARAM_SPLIT_PATTERN, /[\/,\-\/:]/
        )
		
        AnalysisEngine tokenizer = AnalysisEngineFactory.createEngine(desc)
        assert tokenizer != null

        // create a new JCas and seed with sentences
        String text = "X-Y-Z: Exam extent reached: identified the cecum and visualized the ileocecal valve/appendiceal orifice."
        JCas jcas = tokenizer.newJCas()
        jcas.setDocumentText(text)
        jcas.create(type:Sentence, begin:0, end:text.length())

        // apply the tokenizer
        tokenizer.process(jcas)

        // verify number of tokens
        Collection<Token> tokens = jcas.select(type:Token)
        assert tokens.size() == 22
        tokens.each { println "'${it.coveredText}': [pos:$it.pos, lemma:$it.lemma, stem:$it.stem]" }
    }
}
