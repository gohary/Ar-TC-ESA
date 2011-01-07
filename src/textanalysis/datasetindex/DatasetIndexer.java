package textanalysis.datasetindex;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import dataset.DatasetDocument;

public class DatasetIndexer {

	private Directory directory;

	private IndexWriter indexWriter;

	public DatasetIndexer(String indexName) {

		try {
			directory = FSDirectory.open(new File("resources/indexes/"
					+ indexName));
			indexWriter = new IndexWriter(directory, new ArabicAnalyzer(
					Version.LUCENE_30), true,
					IndexWriter.MaxFieldLength.UNLIMITED);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

	}

	private int indexId = 0;

	/**
	 * 
	 * @return indexId of the document
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public int indexDocument(DatasetDocument doc) throws CorruptIndexException,
			IOException {
		Document indexDoc = new Document();

		indexDoc.add(new Field("category", doc.getCategory(), Store.YES,
				Index.ANALYZED));

		indexDoc.add(new Field("text", doc.getText(), Store.YES,
				Index.ANALYZED, TermVector.YES));

		indexWriter.addDocument(indexDoc);
		return indexId++;
	}

	public void end() {
		try {
			indexWriter.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
