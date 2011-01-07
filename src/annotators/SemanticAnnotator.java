package annotators;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;

import dataset.DatasetDocument;
import dataset.Datasets_Methods;
import textanalysis.datasetindex.Reader;
import textanalysis.dbutils.DBUtils;
import textanalysis.wikipediaindex.Article;
import textanalysis.wikipediaindex.WikipediaSearcher;

public class SemanticAnnotator {

	private DBUtils dbUtils;
	private Datasets_Methods datasetMethod;
	private WikipediaSearcher wikipediaSearcher;
	private Reader datasetReader;

	public SemanticAnnotator(Datasets_Methods method) {
		dbUtils = new DBUtils();
		wikipediaSearcher = new WikipediaSearcher();
		datasetReader = new Reader(method.indexName);
		this.datasetMethod = method;
	}

	public void annotateDataset() throws SQLException, CorruptIndexException,
			IOException {
		List<Integer> ids = dbUtils.getDatasetDocs(datasetMethod.method);

		int numDocs = datasetReader.getNumDocs();

		for (int i = 0; i < numDocs; i++) {
			DatasetDocument doc = datasetReader.getDocument(i);
			List<Article> matchingArticles = wikipediaSearcher.search(doc
					.getText());
			Map<Integer, Float> annotations = new HashMap<Integer, Float>();
			for (Article article : matchingArticles) {
				annotations.put(article.indexId, article.matchingScore);
			}

			dbUtils.addConceptAnnotations(annotations, ids.get(i),
					datasetMethod.method);
		}
	}

	public static void main(String[] args) throws CorruptIndexException, SQLException, IOException {
		new SemanticAnnotator(Datasets_Methods.JORDANIAN_UMASS_LIGHT_STEMMER)
				.annotateDataset();
	}
}
