package dataset;

public enum Methods {

	UMASS_LIGHT_STEMMER(1), khoja_root_stemmer(2);
	private Methods(int id) {
		this.id = id;
	}

	public int id;
}
