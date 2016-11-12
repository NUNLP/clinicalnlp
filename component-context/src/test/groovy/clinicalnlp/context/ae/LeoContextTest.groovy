package clinicalnlp.context.ae

import clinicalnlp.dsl.ae.LeoDSLAnnotator
import clinicalnlp.context.reader.TestCollectionReader
import clinicalnlp.types.NamedEntityMention
import gov.va.vinci.leo.descriptors.LeoAEDescriptor
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription
import org.apache.uima.aae.client.UimaAsBaseCallbackListener

import clinicalnlp.context.listener.TestListener

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

class LeoContextTest {

    private static Process process = null;

    @BeforeClass
    public static void startService() {
        String UIMA_HOME = System.getenv('UIMA_HOME')
        String OS_NAME = System.getProperty("os.name")
        String ext = OS_NAME == 'Mac OS X' ? 'sh' : 'bat'
        this.process = new ProcessBuilder("${UIMA_HOME}/bin/startBroker.${ext}").start();
        assert this.process.alive
    }

    @AfterClass
    public static void stopService() {
        this.process.destroy()
    }

    @Test
    public void smokeTest() {
        def text = """\
        Patient has fever but no cough and pneumonia is ruled out.
        There is no increase in weakness.
        Patient does not have measles.
        """

        // pipeline descriptor
        LeoTypeSystemDescription types = new LeoTypeSystemDescription(
                'clinicalnlp/types/CoreTypeSystem', true)
        LeoAEDescriptor pipeline = new LeoAEDescriptor()
        pipeline.addDelegate(
                new LeoDSLAnnotator()
                        .setScriptFileName("groovy/TestConceptDetector.groovy")
                        .setLeoTypeSystemDescription(types)
                        .getLeoAEDescriptor()
        )
        pipeline.addDelegate(
                new LeoDSLAnnotator()
                        .setScriptFileName("groovy/NegEx.groovy")
                        .setLeoTypeSystemDescription(types)
                        .getLeoAEDescriptor()
        )
        pipeline.setIsAsync(false)
        pipeline.setNumberOfInstances(1)

        // create listener
        UimaAsBaseCallbackListener listener = new TestListener()
        listener.typeName = NamedEntityMention.canonicalName

        // run service
        gov.va.vinci.leo.Service service = new gov.va.vinci.leo.Service()
        service.setDescriptorDirectory('src/test/resources/data/output')
        service.setDeleteOnExit(true)
        service.deploy(pipeline)

        // create client
        gov.va.vinci.leo.Client client = new gov.va.vinci.leo.Client();
        client.setBrokerURL('tcp://localhost:61616')
        client.setEndpoint('mySimpleQueueName')
        client.setCasPoolSize(1)
        client.setCCTimeout(1000)
        client.addUABListener(listener)

        // run client
        client.run(new TestCollectionReader(text))

        // validate output
        assert listener.collected.size == 5
        listener.collected.each { String nem ->
            println "${nem}"
        }
    }
}
