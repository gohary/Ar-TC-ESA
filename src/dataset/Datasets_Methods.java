package dataset;

public enum Datasets_Methods {

	JORDANIAN_UMASS_LIGHT_STEMMER("jordanian-light-lucene-stemmer", 1, 1);

	private Datasets_Methods(String indexName, int dataset, int method) {
		this.indexName = indexName;
		this.dataset = dataset;
		this.method = method;
	}

	public String indexName;
	public int dataset, method;
}
