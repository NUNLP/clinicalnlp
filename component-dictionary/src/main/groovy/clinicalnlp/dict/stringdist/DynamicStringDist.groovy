package clinicalnlp.dict.stringdist

interface DynamicStringDist extends StringDist {
	Double push(final char c)
	void pop()
}
