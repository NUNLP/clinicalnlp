package gov.va.queri.context.reader

import gov.va.vinci.leo.cr.BaseLeoCollectionReader
import gov.va.vinci.leo.descriptors.LeoConfigurationParameter
import gov.va.vinci.leo.types.CSI
import org.apache.uima.cas.CAS
import org.apache.uima.cas.CASException
import org.apache.uima.collection.CollectionException
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.cas.StringArray
import org.apache.uima.util.Progress
import org.apache.uima.util.ProgressImpl
/**
 * This reader populates a specified number of CAS objects with random strings.  Useful primarily as an example of the
 * implementation of the BaseLeoCollectionReader API.
 *
 * User: Thomas Ginter
 * Date: 10/27/14
 * Time: 09:15
 */
public class TestCollectionReader extends BaseLeoCollectionReader {

    private int numberOfStrings = 1
    private int currentString = 0

    @LeoConfigurationParameter
    protected String text

    public TestCollectionReader() {
    }

    public TestCollectionReader(String text) {
        this.text = text
    }

    public void setText(String text) {
        this.text = text
    }

    /**
     * @param aCAS the CAS to populate with the next document;
     * @throws CollectionException if there is a problem getting the next and populating the CAS.
     */
    @Override
    public void getNext(CAS aCAS) throws CollectionException, IOException {
        int length = this.text.length()
        aCAS.setDocumentText(text);

        //Create the CSI annotation and set the properties
        try {
            JCas jCas = aCAS.getJCas()
            CSI csi = new CSI(jCas)
            csi.setBegin(0)
            csi.setEnd(length)
            csi.setID("" + currentString)
            csi.addToIndexes()

            csi.setRowData(new StringArray(jCas, length))
            for(int i = 0; i < length; i++)
                csi.setRowData(i, "testRowData" + UUID.randomUUID().toString())

            csi.setPropertiesKeys(new StringArray(jCas, length))
            for(int i = 0; i < length; i++)
                csi.setPropertiesKeys(i, "testKeys" + UUID.randomUUID().toString())

            csi.setPropertiesValues(new StringArray(jCas, length))
            for(int i = 0; i < length; i++)
                csi.setPropertiesValues(i, "testValues" + UUID.randomUUID().toString())
        } catch (CASException e) {
            throw new CollectionException(e)
        }

        currentString++
    }

    /**
     * @return true if and only if there are more elements available from this CollectionReader.
     * @throws IOException
     * @throws CollectionException
     */
    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return currentString < numberOfStrings;
    }

    /**
     * Gets information about the number of entities and/or amount of data that has been read from
     * this <code>CollectionReader</code>, and the total amount that remains (if that information
     * is available).
     * <p/>
     * This method returns an array of <code>Progress</code> objects so that results can be reported
     * using different units. For example, the CollectionReader could report progress in terms of the
     * number of documents that have been read and also in terms of the number of bytes that have been
     * read. In many cases, it will be sufficient to return just one <code>Progress</code> object.
     *
     * @return an array of <code>Progress</code> objects. Each object may have different units (for
     * example number of entities or bytes).
     */
    @Override
    public Progress[] getProgress() {
        return [ new ProgressImpl(currentString, numberOfStrings, Progress.ENTITIES) ]
    }


}
