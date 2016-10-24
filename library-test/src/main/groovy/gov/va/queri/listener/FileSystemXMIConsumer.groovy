package gov.va.queri.listener

import gov.va.vinci.leo.types.CSI
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.cas.impl.XmiCasSerializer
import org.apache.uima.fit.component.JCasConsumer_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceInitializationException

class FileSystemXMIConsumer extends JCasConsumer_ImplBase {
    public static final String PARAM_DIRECTORY_PATH = 'directoryPath'
    @ConfigurationParameter(name='directoryPath', mandatory=true)
    private String directoryPath

    File directory

    Integer counter = 0

    @Override
    void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context)
        println "Directory path: ${this.directoryPath}"
        this.directory = new File(this.directoryPath)
    }

    @Override
    void process(JCas aJCas) throws AnalysisEngineProcessException {
        assert this.directory != null
        assert this.directory.directory
        assert this.directory.canWrite()
        CSI csi = aJCas.select(type:CSI)[0]
        println "Writing: ${csi.ID.replace('.txt', '.ann')}"
        File xmiOutFile = new File(this.directory, csi.ID.replace('.txt', '.xmi'))
        OutputStream xmiOut = new FileOutputStream(xmiOutFile);
        XmiCasSerializer.serialize(aJCas.getCas(), xmiOut);
    }
}
