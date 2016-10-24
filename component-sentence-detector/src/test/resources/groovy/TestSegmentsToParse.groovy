import gov.va.queri.types.Segment
import org.apache.uima.jcas.JCas

JCas jcas = (JCas) getProperty('jcas')

segs = jcas.select(type:Segment, filter:{ it.code in ['FINAL_DIAGNOSIS'] })

