package gov.va.queri.reader

import gov.va.vinci.leo.types.CSI
import org.apache.uima.UimaContext
import org.apache.uima.cas.impl.FeatureImpl
import org.apache.uima.collection.CollectionException
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.DocumentAnnotation
import org.apache.uima.resource.ResourceInitializationException
import org.apache.uima.util.Progress

import javax.print.Doc
import javax.swing.text.Document


class FileSystemCollectionReader extends JCasCollectionReader_ImplBase {
    public static final String PARAM_DIRECTORY_PATH = 'directoryPath'
    @ConfigurationParameter(name='directoryPath', mandatory=true)
    private String directoryPath

    File[] inputFiles
    Integer currentFileIdx = 0

    @Override
    void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context)
        File directory = new File(this.directoryPath)
        this.inputFiles = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        });
    }

    @Override
    void getNext(JCas jCas) throws IOException, CollectionException {
        File file = this.inputFiles[this.currentFileIdx]
        ////println "Processing file: ${file.name}"
        jCas.setDocumentText(file.text)
        CSI csi = jCas.create(type:CSI)
        csi.setID(file.name)
        csi.setLocator(file.toURI().toString())
        this.currentFileIdx++
    }

    @Override
    boolean hasNext() throws IOException, CollectionException {
        if (this.currentFileIdx >= this.inputFiles.length) {
            return false
        }
        return true
    }

    @Override
    Progress[] getProgress() {
        return new Progress[0]
    }
}
