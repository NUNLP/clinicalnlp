package clinicalnlp.token.ae

import clinicalnlp.dsl.ae.LeoDSLAnnotator
import clinicalnlp.listener.TestListener
import clinicalnlp.reader.TestCollectionReader
import clinicalnlp.sent.ae.LeoSentenceDetector
import clinicalnlp.types.Token
import gov.va.vinci.leo.descriptors.LeoAEDescriptor
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.uima.aae.client.UimaAsBaseCallbackListener
import org.junit.*

@Log4j
class LeoTokenAnnotatorTest {

    private static Process process = null;

    @BeforeClass
	static void setupClass() {
		BasicConfigurator.configure()
    }

    @BeforeClass
    static void startService() {
        String UIMA_HOME = System.getenv('UIMA_HOME')
        String OS_NAME = System.getProperty("os.name")
        String ext = OS_NAME == 'Mac OS X' ? 'sh' : 'bat'
        this.process = new ProcessBuilder("${UIMA_HOME}/bin/startBroker.${ext}").start();
        assert this.process.alive
    }

    @AfterClass
    static void stopService() {
        this.process.destroy()
    }

    @Before
	void setUp() throws Exception {
		log.setLevel(Level.INFO)
	}

	@After
	void tearDown() throws Exception {
	}

    @Test
    void testTokenAnnotator() {

        def text = """\
        Patient has fever but no cough and pneumonia is ruled out.
        There is no increase in weakness.
        Patient does not have measles.
        """

        LeoTypeSystemDescription types = new LeoTypeSystemDescription('clinicalnlp.types.CoreTypeSystem', true)

        // pipeline descriptor
        LeoAEDescriptor pipeline = new LeoAEDescriptor()
        pipeline.addDelegate(
                new LeoDSLAnnotator()
                        .setScriptFileName('groovy/SimpleSegmenter.groovy')
                        .setLeoTypeSystemDescription(types)
                        .getLeoAEDescriptor()
        )
        pipeline.addDelegate(
                new LeoSentenceDetector()
                        .setSentModelPath('classpath:clinicalnlp/models/sd-med-model.zip')
                        .setLeoTypeSystemDescription(types)
                        .getLeoAEDescriptor()
        )
        pipeline.addDelegate(
                new LeoTokenAnnotator()
                        .setContainerTypeName('gov.va.vinci.leo.sentence.types.Sentence')
                        .setTokenModelPath('classpath:clinicalnlp/models/en-token.bin')
                        .setLeoTypeSystemDescription(types)
                        .getLeoAEDescriptor()
        )
        pipeline.setIsAsync(false)
        pipeline.setNumberOfInstances(1)

        // create listener
        UimaAsBaseCallbackListener listener = new TestListener()
        listener.typeName = Token.canonicalName

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
        assert listener.collected.size == 25
    }
}
