package clinicalnlp.dsl.ae

import gov.va.vinci.leo.ae.LeoBaseAnnotator
import gov.va.vinci.leo.descriptors.LeoConfigurationParameter
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription
import groovy.util.logging.Log4j
import org.apache.uima.UimaContext
import org.apache.uima.analysis_engine.AnalysisEngineProcessException
import org.apache.uima.jcas.JCas
import org.apache.uima.resource.ResourceInitializationException

@Log4j
class LeoDSLAnnotator extends LeoBaseAnnotator {

	@LeoConfigurationParameter(mandatory = true)
	protected String scriptFileName

	@LeoConfigurationParameter(mandatory = false)
	protected String bindingScriptFileName

	LeoDSLAnnotator setScriptFileName(String scriptFileName) {
		this.scriptFileName = scriptFileName
		return this;
	}

	LeoDSLAnnotator setBindingScriptFileName(String bindingScriptFileName) {
		this.bindingScriptFileName = bindingScriptFileName
		return this;
	}

	private DSLAnnotatorImpl impl = new DSLAnnotatorImpl()

	@Override
	void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext)
		this.impl.initialize(aContext, this.bindingScriptFileName, this.scriptFileName)
	}

	@Override
	void annotate(JCas aJCas) throws AnalysisEngineProcessException {
		this.impl.process(aJCas)
	}

	@Override
	LeoTypeSystemDescription getLeoTypeSystemDescription() {
		return super.getLeoTypeSystemDescription()
	}

	@Override
	<T extends LeoBaseAnnotator> T setLeoTypeSystemDescription(LeoTypeSystemDescription typeSystemDescription) {
		return super.setLeoTypeSystemDescription(typeSystemDescription)
	}
}