package gov.va.queri.dsl.ae

import groovy.util.logging.Log4j
import org.apache.uima.UimaContext
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceInitializationException

@Log4j
public class LocalDSLAnnotator extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {

    public static final String PARAM_SCRIPT_FILE = 'scriptFileName'
    public static final String PARAM_BINDING_SCRIPT_FILE = 'bindingScriptFileName'

    @ConfigurationParameter(name = 'scriptFileName', mandatory = true,
            description = 'File holding Groovy script')
    private String scriptFileName

    @ConfigurationParameter(name = 'bindingScriptFileName', mandatory = false,
            description = 'File holding Groovy script for bindings')
    private String bindingScriptFileName

    private DSLAnnotatorImpl impl = new DSLAnnotatorImpl()

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext)
        this.impl.initialize(this.bindingScriptFileName, this.scriptFileName)
    }

    @Override
    public void process(JCas aJCas) {
        this.impl.process(aJCas)
    }
}
