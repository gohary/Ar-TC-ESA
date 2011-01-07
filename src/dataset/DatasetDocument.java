package dataset;

public class DatasetDocument {

	private int id;
	private String title;
	private String category;
	private String text;
	private Datasets dataset;

	private int indexId;

	public int getIndexId() {
		return indexId;
	}

	public void setIndexId(int indexId) {
		this.indexId = indexId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Datasets getDataset() {
		return dataset;
	}

	public void setDataset(Datasets dataset) {
		this.dataset = dataset;
	}

}
