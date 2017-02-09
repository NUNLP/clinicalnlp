package clinicalnlp.dict.stringdist

interface DynamicStringDist extends StringDist {
	Float push(final char c)
	void pop()
}
