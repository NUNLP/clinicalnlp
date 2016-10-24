package gov.va.queri.chunk

import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

@Log4j
class ChunkAnnotatorTest {
    
    @BeforeClass
    public static void setupClass() {
        BasicConfigurator.configure()
    }
    
    @Before
    public void setUp() throws Exception {
        log.setLevel(Level.INFO)
    }

    @Test
    public void smokeTestOpenNLP() {

    }
}
