package clinicalnlp.dsl.ae

import clinicalnlp.dsl.DSL
import com.google.common.io.Resources
import org.apache.uima.UimaContext
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceInitializationException
import org.codehaus.groovy.control.CompilerConfiguration

import java.nio.charset.Charset

class DSLAnnotatorImpl {
    private Script script

    def initialize(UimaContext context, String bindingScriptFile, String scriptFile)
            throws ResourceInitializationException {
        Class.forName(DSL.canonicalName)
        CompilerConfiguration config = new CompilerConfiguration()
        config.setScriptBaseClass(DSL.canonicalName)
        GroovyShell shell = new GroovyShell(config)

        try {
            URL url = Resources.getResource(scriptFile)
            String scriptContents = Resources.toString(url, Charset.forName("UTF-8"))
            this.script = shell.parse(scriptContents)
            if (bindingScriptFile) {
                Script bindingsScript = shell.parse(Resources.toString(
                    Resources.getResource(bindingScriptFile), Charset.forName("UTF-8")))
                bindingsScript.setProperty('context', context)
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
