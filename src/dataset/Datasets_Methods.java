package dataset;

public enum Datasets_Methods {

	JORDANIAN_UMASS_LIGHT_STEMMER("jordanian-light-lucene-stemmer",
			Datasets.JORDANIAN, 1), JORDANIAN_KHOJA_ROOT_STEMMER(
			"jordanian_khoja_root_stemmer", Datasets.JORDANIAN, 2);

	private Datasets_Methods(String indexName, Datasets dataset, int method) {
		this.indexName = indexName;
		this.dataset = dataset;
		this.method = method;
	}

	public String indexName;
	public int method;
	public Datasets dataset;
}
