import clinicalnlp.types.Segment
import org.apache.uima.jcas.JCas

JCas jcas = (JCas) getProperty('jcas')

jcas.create(type:Segment, begin:0, end:jcas.documentText.length())