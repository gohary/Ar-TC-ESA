package textanalysis.wikipediaindex;

public class Article {
	public String name, url;
	public String[] tags;
	public String content;
	public int indexId;

	public float matchingScore;
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Article) {
			return ((Article) obj).indexId == indexId;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.indexId;
	}
}