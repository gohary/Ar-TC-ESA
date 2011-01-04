package textanalysis.dbutils;

import java.util.Map;

import textanalysis.wikipediaindex.Article;

import dataset.DatasetDocument;

public class DBUtils {

	public void addDocument(DatasetDocument doc) {

	}

	public Article getWikipediaConcept(int conceptId) {
		return null;
	}

	public void addWikipediaConcept(Article concept) {
	}

	public Map<String, Float> getTermAnnotations(int docId, int method) {
		return null;
	}

	public void addTermAnnotations(Map<String, Float> annotations, int docId,
			int method) {
	}

	public Map<Integer, Float> getConceptAnnotations(int docId, int method) {
		return null;
	}

	public void addConceptAnnotations(Map<Integer, Float> annotations,
			int docId, int method) {

	}

}
