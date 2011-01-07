package annotators;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;

import dataset.Datasets_Methods;
import textanalysis.datasetindex.Reader;
import textanalysis.dbutils.DBUtils;

public class TermBasedAnnotator {

	private DBUtils dbUtils;
	private Reader datasetReader;

	private Datasets_Methods datasetMethod;

	public TermBasedAnnotator(Datasets_Methods method) {
		dbUtils = new DBUtils();
		datasetReader = new Reader(method.indexName);
		this.datasetMethod = method;
	}

	public void annotateDataset() throws SQLException, IOException,
			ParseException {
		List<Integer> ids = dbUtils.getDatasetDocs(datasetMethod.method);

		int numDocs = datasetReader.getNumDocs();

		for (int i = 0; i < numDocs; i++) {
			dbUtils.addTermAnnotations(
					datasetReader.getDocumentFeatureVector(i), ids.get(i),
					datasetMethod.method);
		}
	}

	public static void main(String[] args) throws SQLException, IOException,
			ParseException {
		new TermBasedAnnotator(Datasets_Methods.JORDANIAN_UMASS_LIGHT_STEMMER)
				.annotateDataset();
	}
}
