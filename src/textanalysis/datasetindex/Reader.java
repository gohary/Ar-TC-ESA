package textanalysis.datasetindex;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
	private final int numDocs;

	public Reader(String indexName) {
		try {
			Directory directory = FSDirectory.open(new File(
					"resources/indexes/" + indexName));
			indexReader = IndexReader.open(directory);
			indexSearcher = new IndexSearcher(directory);
			queryParser = new QueryParser(Version.LUCENE_30, "text",
					new ArabicAnalyzer(Version.LUCENE_30));
			numDocs = indexReader.numDocs();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public int getNumDocs() {
		return this.numDocs;
	}

	public DatasetDocument getDocument(int id) throws CorruptIndexException,
			IOException {
		Document doc = indexReader.document(id);
		DatasetDocument datasetDocument = new DatasetDocument();
		datasetDocument.setCategory(doc.get("category"));
		datasetDocument.setText(doc.get("text"));
		datasetDocument.setIndexId(id);

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
			if (term.length() == 0)
				continue;
			//System.out.println(term);
			int tf = termFreqVector.getTermFrequencies()[i];
			sumDocFreqs += tf;
			float idf = getIDF(term);
			if (idf == -1)
				continue;
			featureVector.put(term, tf * idf);
		}

		for (Entry<String, Float> feature : featureVector.entrySet()) {
			featureVector.put(feature.getKey(), feature.getValue()
					/ sumDocFreqs);
		}

		return featureVector;
	}

	/**
	 * 
	 * @param term
	 * @return -1 if hits = 0 which means that the term is a stop word
	 * @throws IOException
	 * @throws ParseException
	 */
	private float getIDF(String term) throws IOException, ParseException {
		term = QueryParser.escape(term);
		int hits = indexSearcher.search(queryParser.parse(term), numDocs).totalHits;
		if (hits == 0)
			return -1;
		float idf = (float) Math.log10((float) numDocs / hits);
		return idf;
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
		Reader r = new Reader("jordanian_khoja_root_stemmer");
		System.out.println(r.getNumDocs());
		Map<String, Float> ann = r.getDocumentFeatureVector(0);
		for(Map.Entry<String, Float> term: ann.entrySet()){
			System.out.println(term.getValue()+" "+term.getKey());
		}
	}
}
