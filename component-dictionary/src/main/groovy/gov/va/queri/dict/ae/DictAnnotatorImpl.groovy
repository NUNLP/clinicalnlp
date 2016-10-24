package gov.va.queri.dict.ae

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.io.Resources
import gov.va.queri.dict.*
import gov.va.queri.dict.stringdist.MinEditDist
import gov.va.queri.dsl.UIMA_DSL
import gov.va.queri.types.DictMatch
import opennlp.tools.tokenize.TokenizerME
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.codehaus.groovy.control.CompilerConfiguration
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource

class DictAnnotatorImpl {
    private DictModel dict
    private Script postScript

    void initialize(String dictionaryPath, String dictionaryType,
                    TokenizerME tokenizer,
                    Boolean caseInsensitive,
                    String postScriptFile) {


        ObjectMapper mapper = new ObjectMapper()
        DefaultResourceLoader loader = new DefaultResourceLoader(ClassLoader.getSystemClassLoader());
        Resource schemaResource = loader.getResource(dictionaryPath)
        AbstractionSchema schema = mapper.readValue(schemaResource.inputStream, AbstractionSchema);
        this.dict = DictModelFactory.make(dictionaryType, schema, tokenizer, caseInsensitive)

        if (postScriptFile) {
            CompilerConfiguration config = new CompilerConfiguration()
            config.setScriptBaseClass("gov.va.queri.dsl.UIMA_DSL")
            GroovyShell shell = new GroovyShell(config)
            this.postScript = shell.parse(Resources.toString(
                    Resources.getResource(postScriptFile),
                    org.apache.commons.io.Charsets.UTF_8))
        }
    }

    void process(JCas jcas, Boolean longestMatch, Boolean caseInsensitive, Float tolerance,
                 String containerClassName, String tokenClassName) {
        Class<Annotation> ContainerClass = Class.forName(containerClassName)
        Class<Annotation> TokenClass = Class.forName(tokenClassName)

        jcas.select(type:ContainerClass).each { Annotation container ->
            Collection<Annotation> anns = jcas.select(type:TokenClass,
                    filter:UIMA_DSL.coveredBy(container))
            Collection<CharSequence> tokens = new ArrayList<>()
            anns.each { Annotation ann ->
                tokens << (caseInsensitive ? ann.coveredText.toLowerCase() : ann.coveredText)
            }
            Collection<TokenMatch<DictEntry>> matches = this.dict.matches(tokens,
                    new MinEditDist(),
                    tolerance,
                    longestMatch)
            matches.each { TokenMatch m ->
                Collection<Annotation> matched = new ArrayList<>()
                for (int i = m.begin; i <= m.end; i++) {
                    matched << anns.get(i)
                }
                jcas.create(type:DictMatch,
                        begin:matched[0].begin,
                        end:matched[matched.size()-1].end,
                        canonical:m.value.canonical,
                        code:m.value.code,
                        vocabulary:m.value.vocab,
                        container:container,
                        matchedTokens:matched
                )
            }
        }

        // execute the post script, if specified
        if (this.postScript) {
            this.postScript.setProperty('jcas', jcas)
            this.postScript.run()
        }
    }
}