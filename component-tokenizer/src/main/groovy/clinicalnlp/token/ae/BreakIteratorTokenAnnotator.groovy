package clinicalnlp.token.ae

import clinicalnlp.types.Token
import com.ibm.icu.text.BreakIterator
import groovy.util.logging.Log4j
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasAnnotator_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.resource.ResourceInitializationException

import static com.ibm.icu.text.BreakIterator.wordInstance

@Log4j
public final class BreakIteratorTokenAnnotator extends JCasAnnotator_ImplBase {
    public static final String PARAM_CONTAINER_TYPE = 'containerTypeName'
    @ConfigurationParameter(name = 'containerTypeName', mandatory = false,
            defaultValue = 'org.apache.uima.jcas.tcas.DocumentAnnotation')
    private String containerTypeName;

    private Class<Annotation> containerType;

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context)
        try {
            this.containerType = Class.forName(this.containerTypeName)
        }
        catch (Exception e) {
            throw new ResourceInitializationException(e)
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        aJCas.select(type: (this.containerType)).each {
            int offset = it.getBegin()
            BreakIterator boundary = wordInstance
            boundary.setText(it.coveredText)
            int begin = boundary.first()
            int end = boundary.next()
            while (end != BreakIterator.DONE) {
                if (!it.coveredText.substring(begin, end).allWhitespace) {
                    aJCas.create(type:Token, begin:offset+begin, end:offset+end)
                }
                begin = end
                end = boundary.next()
            }
        }
    }
}
