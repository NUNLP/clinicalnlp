package gov.va.queri.dict.stringdist

public interface DynamicStringDist extends StringDist {
	public Double push(final char c);
	public void pop();
}
