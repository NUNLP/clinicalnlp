package clinicalnlp.dict.ae

import clinicalnlp.dict.trie.TrieDictModel
import clinicalnlp.dsl.ae.LeoDSLAnnotator
import clinicalnlp.listener.TestListener
import clinicalnlp.reader.TestCollectionReader
import clinicalnlp.sent.ae.LeoSentenceDetector
import clinicalnlp.token.ae.LeoTokenAnnotator
import clinicalnlp.types.DictMatch
import clinicalnlp.types.Token
import gov.va.vinci.leo.descriptors.LeoAEDescriptor
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.uima.aae.client.UimaAsBaseCallbackListener
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

@Log4j
class LeoDictAnnotatorTest {
    private Process process = null;

    @BeforeClass
    static void setupClass() {
        Class.forName('clinicalnlp.dsl.DSL')
        BasicConfigurator.configure()
    }

    @Before
    void startService() {
        log.setLevel(Level.INFO)
        String UIMA_HOME = System.getenv('UIMA_HOME')
        String OS_NAME = System.getProperty("os.name")
        String ext = OS_NAME == 'Mac OS X' ? 'sh' : 'bat'
        this.process = new ProcessBuilder("${UIMA_HOME}/bin/startBroker.${ext}").start();
        assert this.process.alive
    }

    @After
    void stopService() {
        this.process.destroy()
        this.process = null
    }

    @Test
    void smokeTest() {

        String text = """\
        The patient has a diagnosis of spongioblastoma multiforme.  GBM does not have a good prognosis.
        But I can't rule out meningioma in the brain and spinal cord.
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
        pipeline.addDelegate(
            new LeoDictAnnotator()
                .setDictionaryPath('/abstractionSchema/histology-abstraction-schema.json')
                .setTokenModelPath('classpath:clinicalnlp/models/en-token.bin')
                .setContainerClassName('gov.va.vinci.leo.sentence.types.Sentence')
                .setTokenClassName('clinicalnlp.types.Token')
                .setDictionaryType(TrieDictModel.canonicalName)
                .setTolerance(0.0)
                .setMaxDistance(0)
                .setLeoTypeSystemDescription(types)
                .getLeoAEDescriptor()
        )
        pipeline.setIsAsync(false)
        pipeline.setNumberOfInstances(1)

        // create listeners
        UimaAsBaseCallbackListener tokenListener = new TestListener()
        tokenListener.typeName = Token.canonicalName
        UimaAsBaseCallbackListener dictMatchListener = new TestListener()
        dictMatchListener.typeName = DictMatch.canonicalName

        // run service
        gov.va.vinci.leo.Service service = new gov.va.vinci.leo.Service()
        service.setDescriptorDirectory('src/test/resources/descriptors')
        service.setDeleteOnExit(true)
        service.deploy(pipeline)

        // create client
        gov.va.vinci.leo.Client client = new gov.va.vinci.leo.Client();
        client.setBrokerURL('tcp://localhost:61616')
        client.setEndpoint('mySimpleQueueName')
        client.setCasPoolSize(1)
        client.setCCTimeout(1000)
        client.addUABListener(tokenListener)
        client.addUABListener(dictMatchListener)

        // run client
        client.run(new TestCollectionReader(text))

        // validate output
        assert tokenListener.collected.size == 31
        assert dictMatchListener.collected.size == 2
        dictMatchListener.collected.each {
            println "DictMatch: ${it}"
        }
    }
}
