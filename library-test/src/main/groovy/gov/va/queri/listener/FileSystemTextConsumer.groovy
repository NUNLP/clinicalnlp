package gov.va.queri.listener

import gov.va.vinci.leo.listener.BaseListener
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.cas.CAS
import org.apache.uima.cas.impl.XmiCasSerializer
import org.apache.uima.collection.EntityProcessStatus
import org.apache.uima.fit.component.JCasConsumer_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.cas.TOP
import org.apache.uima.resource.ResourceInitializationException

class FileSystemTextConsumer extends BaseListener {
    private String directoryPath
    private File directory

    public FileSystemTextConsumer setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath
        return this
    }

    @Override
    void initializationComplete(EntityProcessStatus aStatus) {
        super.initializationComplete(aStatus)
        this.directory = new File(this.directoryPath)
    }

    @Override
    public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
        JCas jcas = aCas.getJCas()
        String doc = this.getReferenceLocation(jcas)
        File textOutFile = new File(this.directory, "${doc}.txt")
        textOutFile << jcas.documentText
        println "Wrote to file: ${textOutFile.name}"
    }

    @Override
    public void collectionProcessComplete(EntityProcessStatus aStatus) {
        super.collectionProcessComplete(aStatus)
    }
}
