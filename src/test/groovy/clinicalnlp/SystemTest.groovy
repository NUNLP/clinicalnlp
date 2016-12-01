package clinicalnlp

import groovy.util.logging.Log4j
import org.junit.Test

@Log4j
class SystemTest {
    @Test
    void startService() {
        String UIMA_HOME = System.getenv('UIMA_HOME')
        assert UIMA_HOME != null
        String OS_NAME = System.getProperty("os.name")
        println "OS_NAME == ${OS_NAME}"
        String ext = OS_NAME == 'Mac OS X' ? 'sh' : 'bat'
        Process process = new ProcessBuilder("${UIMA_HOME}/bin/startBroker.${ext}").start();
        assert process.alive
        process.destroy()
    }
}
