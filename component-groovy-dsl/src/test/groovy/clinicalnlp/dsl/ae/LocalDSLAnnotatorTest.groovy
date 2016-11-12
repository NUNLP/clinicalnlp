package clinicalnlp.dsl.ae

import clinicalnlp.types.NamedEntityMention
import gov.va.vinci.leo.sentence.types.Sentence
import org.apache.uima.analysis_engine.AnalysisEngine
import org.apache.uima.fit.factory.AggregateBuilder
import org.apache.uima.fit.factory.AnalysisEngineFactory
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory
import org.apache.uima.fit.pipeline.SimplePipeline
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.metadata.TypeSystemDescription
import org.junit.Test

class LocalDSLAnnotatorTest {

    @Test
    public void smokeTest() {
        def text = """\
        Patient has fever but no cough and pneumonia is ruled out.
        There is no increase in weakness.
        Patient does not have measles.
        """

        TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription()
        tsd.resolveImports()

        AggregateBuilder builder = new AggregateBuilder()
        builder.with {
            add(AnalysisEngineFactory.createEngineDescription(LocalDSLAnnotator,
                    LocalDSLAnnotator.PARAM_SCRIPT_FILE,
                    'groovy/TestSentenceDetector.groovy'))
        }
        AnalysisEngine engine = builder.createAggregate()
        JCas jcas = engine.newJCas()
        jcas.setDocumentText(text)
        SimplePipeline.runPipeline(jcas, engine)
        assert jcas.select(type:Sentence).size() == 3

        builder = new AggregateBuilder()
        builder.with {
            add(AnalysisEngineFactory.createEngineDescription(LocalDSLAnnotator,
                    LocalDSLAnnotator.PARAM_BINDING_SCRIPT_FILE,
                    'groovy/TestBindingScript.groovy',
                    LocalDSLAnnotator.PARAM_SCRIPT_FILE,
                    'groovy/TestConceptDetector.groovy'))
        }
        engine = builder.createAggregate()
        jcas = engine.newJCas()
        jcas.setDocumentText(text)
        SimplePipeline.runPipeline(jcas, engine)
        assert jcas.select(type:NamedEntityMention).size() == 5
    }
}
