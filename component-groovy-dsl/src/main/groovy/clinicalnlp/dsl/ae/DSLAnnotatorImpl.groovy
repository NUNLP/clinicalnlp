package clinicalnlp.dsl.ae

import com.google.common.io.Resources
import clinicalnlp.dsl.UIMA_DSL
import groovy.util.logging.Log4j
import org.apache.commons.io.Charsets
import org.apache.log4j.Level
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceInitializationException
import org.codehaus.groovy.control.CompilerConfiguration

@Log4j
class DSLAnnotatorImpl {
    private Script script

    def initialize(String bindingsScriptFileName, String scriptFileName)
            throws ResourceInitializationException {
        log.level = Level.INFO
        Class.forName(UIMA_DSL.canonicalName)
        CompilerConfiguration config = new CompilerConfiguration()
        config.setScriptBaseClass(UIMA_DSL.canonicalName)
        GroovyShell shell = new GroovyShell(config)

        try {
            log.info "Loading groovy config file: ${scriptFileName}"
            URL url = Resources.getResource(scriptFileName)
            String scriptContents = Resources.toString(url, Charsets.UTF_8)
            this.script = shell.parse(scriptContents)
            if (bindingsScriptFileName) {
                log.info "Loading groovy config binding file: ${bindingsScriptFileName}"
                Script bindingsScript = shell.parse(Resources.toString(
                    Resources.getResource(bindingsScriptFileName), Charsets.UTF_8))
                this.script.setBinding(new Binding(bindingsScript.run()))
            }

        } catch (IOException e) {
            throw new ResourceInitializationException()
        }
    }

    def process(JCas jcas) {
        this.script.setProperty('jcas', jcas)
        return this.script.run()
    }
}
