package gov.va.queri.seg.ae

import gov.va.queri.dsl.ae.LeoDSLAnnotator

import gov.va.queri.types.Segment
import gov.va.vinci.leo.descriptors.LeoAEDescriptor
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.uima.aae.client.UimaAsBaseCallbackListener
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import gov.va.queri.listener.TestListener
import gov.va.queri.reader.TestCollectionReader

@Log4j
class LeoSegmenterTest {

    private static Process process = null;

    @BeforeClass
	public static void setupClass() {
		BasicConfigurator.configure()
    }

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

    @Before
	public void setUp() throws Exception {
		log.setLevel(Level.INFO)
	}

	@After
	public void tearDown() throws Exception {
	}

    @Test
    public void testSegmenter() {

        def text = """\
        Patient has fever but no cough and pneumonia is ruled out.
        There is no increase in weakness.
        Patient does not have measles.
        """

        // type descriptor
        LeoTypeSystemDescription types = new LeoTypeSystemDescription('gov/va/queri/types/CoreTypeSystem', true)

        // pipeline descriptor
        LeoAEDescriptor pipeline = new LeoAEDescriptor()
        pipeline.addDelegate(
                new LeoDSLAnnotator()
                        .setScriptFileName("groovy/SimpleSegmenter.groovy")
                        .setLeoTypeSystemDescription(types)
                        .getLeoAEDescriptor()
        )
        pipeline.setIsAsync(false)
        pipeline.setNumberOfInstances(1)

        // create listener
        UimaAsBaseCallbackListener listener = new TestListener()
        listener.typeName = Segment.canonicalName

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
        assert listener.collected.size == 1
    }
}