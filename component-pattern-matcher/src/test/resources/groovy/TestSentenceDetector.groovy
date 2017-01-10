import gov.va.vinci.leo.sentence.types.Sentence
import org.apache.uima.jcas.JCas

import java.util.regex.Matcher

JCas jcas = (JCas) getProperty('jcas')

Matcher m = (jcas.documentText =~ /([A-Z].+\.)/)
m.each {
	println m.group(1)
	jcas.create(type:Sentence, begin:m.start(1), end:m.end(1))
}
