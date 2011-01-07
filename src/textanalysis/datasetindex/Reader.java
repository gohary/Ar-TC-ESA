package textanalysis.datasetindex;

import java.io.File;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import dataset.DatasetDocument;

//TODO check the TF*IDF Formula

public class Reader {

	private IndexReader indexReader;
	private IndexSearcher indexSearcher;
	private QueryParser queryParser;
	private String indexName;
	private final int numDocs;

	public Reader(String indexName) {
		try {
			Directory directory = FSDirectory.open(new File(
					"resources/indexes/" + indexName));
			indexReader = IndexReader.open(directory);
			indexSearcher = new IndexSearcher(directory);
			queryParser = new QueryParser(Version.LUCENE_30, "text",
					new ArabicAnalyzer(Version.LUCENE_30));
			this.indexName = indexName;
			numDocs = indexReader.numDocs();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public DatasetDocument getDocument(int id) throws CorruptIndexException,
			IOException {
		Document doc = indexReader.document(id);
		DatasetDocument datasetDocument = new DatasetDocument();
		datasetDocument.setCategory(doc.get("category"));
		datasetDocument.setText(doc.get("text"));
		datasetDocument.setIndexId(id);
		datasetDocument.setIndexName(indexName);
		return datasetDocument;
	}

	public Map<String, Float> getDocumentFeatureVector(int docId)
			throws IOException, ParseException {
		TermFreqVector termFreqVector = indexReader.getTermFreqVectors(docId)[0];
		Map<String, Float> featureVector = new HashMap<String, Float>();
		int len = termFreqVector.size();
		int sumDocFreqs = 0;
		for (int i = 0; i < len; i++) {
			String term = termFreqVector.getTerms()[i];
			int tf = termFreqVector.getTermFrequencies()[i];
			sumDocFreqs += tf;
			float idf = getIDF(term);
			featureVector.put(term, tf * idf);
		}
		for (Entry<String, Float> feature : featureVector.entrySet()) {
			featureVector.put(feature.getKey(), feature.getValue()
					/ sumDocFreqs);
		}

		return featureVector;
	}

	private float getIDF(String term) throws IOException, ParseException {
		term = QueryParser.escape(term);
		int hits = indexSearcher.search(queryParser.parse(term), numDocs).totalHits;
		return (float) Math.log10((float) numDocs / hits);
	}

	public List<DatasetDocument> getCategoryDocuments(String category) {
		// TODO
		return null;
	}

	public void close() {
		try {
			indexReader.close();
			indexSearcher.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws CorruptIndexException,
			IOException, ParseException {
		// testing
		Reader r = new Reader("jordanian-light-lucene-stemmer");
		Map<String, Float> ftrVector = r.getDocumentFeatureVector(50);

		for (Entry<String, Float> ftr : ftrVector.entrySet()) {
			System.out.println(ftr.getKey() + "\t" + ftr.getValue());
		}
	}
}
