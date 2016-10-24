package clinicalnlp.sent.ae

import clinicalnlp.dsl.ae.LeoDSLAnnotator
import clinicalnlp.listener.TestListener
import clinicalnlp.reader.TestCollectionReader
import gov.va.queri.types.NamedEntityMention
import gov.va.vinci.leo.descriptors.LeoAEDescriptor
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription
import gov.va.vinci.leo.sentence.types.Sentence
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.uima.aae.client.UimaAsBaseCallbackListener
import org.junit.*

@Log4j
class LeoSentenceDectectorTest {

    private Process process = null;

    @BeforeClass
	public static void setupClass() {
		BasicConfigurator.configure()
    }

    @Before
    public void startService() {
        log.setLevel(Level.INFO)

        String UIMA_HOME = System.getenv('UIMA_HOME')
        String OS_NAME = System.getProperty("os.name")
        String ext = OS_NAME == 'Mac OS X' ? 'sh' : 'bat'
        this.process = new ProcessBuilder("${UIMA_HOME}/bin/startBroker.${ext}").start();
        assert this.process.alive

    }

    @After
    public void stopService() {
        this.process.destroy()
        this.process = null
    }


    @Ignore
    @Test
    public void smokeTest() {

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
                        .setScriptFileName('groovy/SimpleSegmenter.groovy')
                        .setLeoTypeSystemDescription(types)
                        .getLeoAEDescriptor()
        )
        pipeline.addDelegate(
                new LeoSentenceDetector()
                        .setSentModelPath('classpath:clinicalnlp.models/sd-med-model.zip')
                        .setLeoTypeSystemDescription(types)
                        .getLeoAEDescriptor()
        )
        pipeline.setIsAsync(false)
        pipeline.setNumberOfInstances(1)

        // create listener
        UimaAsBaseCallbackListener listener = new TestListener()

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
        assert listener.sents.size == 3
    }

    @Test
    public void testAnchoredSentenceDetection() {

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
                new NamedEntityMatcher()
                        .setPatternStr('(?i)(cough|pneumonia|measles)')
                        .setLeoTypeSystemDescription(types)
                        .getLeoAEDescriptor()
        )
        pipeline.addDelegate(
                new LeoDSLAnnotator()
                        .setScriptFileName('groovy/SimpleSegmenter.groovy')
                        .setLeoTypeSystemDescription(types)
                        .getLeoAEDescriptor()
        )
        pipeline.addDelegate(
                new LeoSentenceDetector()
                        .setAnchorTypes([NamedEntityMention.canonicalName])
                        .setSentModelPath('classpath:clinicalnlp.models/sd-med-model.zip')
                        .setLeoTypeSystemDescription(types)
                        .getLeoAEDescriptor()
        )
        pipeline.setIsAsync(false)
        pipeline.setNumberOfInstances(1)

        // create listener
        UimaAsBaseCallbackListener listener = new TestListener()
        listener.typeName = Sentence.canonicalName

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
        assert listener.collected.size == 2
    }

}
