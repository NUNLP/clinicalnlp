package clinicalnlp.listener

import gov.va.vinci.leo.types.CSI
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.fit.component.JCasConsumer_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.resource.ResourceInitializationException

class BratAnnotationFileConsumer extends JCasConsumer_ImplBase {

    public static final String PARAM_DIRECTORY_PATH = 'directoryPath'
    @ConfigurationParameter(name='directoryPath', mandatory=true)
    private String directoryPath

    public static final String PARAM_OUTPUT_TYPE = 'outputTypeName'
    @ConfigurationParameter(name='outputTypeName', mandatory=true)
    private String outputTypeName

    File directory
    Class<Annotation> outputType

    Integer counter = 0

    @Override
    void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context)
        println "Directory path: ${this.directoryPath}"
        this.directory = new File(this.directoryPath)
        this.outputType = Class.forName(outputTypeName)
    }

    @Override
    void process(JCas aJCas) throws AnalysisEngineProcessException {
        assert this.directory != null
        assert this.directory.directory
        assert this.directory.canWrite()
        CSI csi = aJCas.select(type:CSI)[0]
        println "Writing: ${csi.ID.replace('.txt', '.ann')}"
        File bratAnnFile = new File(this.directory, csi.ID.replace('.txt', '.ann'))
        OutputStream bratOut = new FileOutputStream(bratAnnFile)
        int typeCount = 1
        aJCas.select(type:this.outputType).each {
            bratOut << "T${typeCount++}\t${this.outputType.simpleName} ${it.begin} ${it.end}\t${it.coveredText}\n"
        }
        bratOut.close()
    }
}
