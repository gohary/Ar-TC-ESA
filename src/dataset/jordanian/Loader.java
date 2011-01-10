package dataset.jordanian;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import textanalysis.datasetindex.DatasetIndexer;
import dataset.DatasetDocument;
import dataset.Datasets;
import dataset.Datasets_Methods;

public class Loader {

	public static void main(String[] args) throws IOException, SQLException {

		DatasetIndexer indexer = new DatasetIndexer(
				Datasets_Methods.JORDANIAN_KHOJA_ROOT_STEMMER.indexName);

		Map<String, String> categoryDocuments = new HashMap<String, String>();

		categoryDocuments.put("computer",
				"resources/dataset/jordanian/00_Computer");

		categoryDocuments.put("economics",
				"resources/dataset/jordanian/01_Economics");

		categoryDocuments.put("education",
				"resources/dataset/jordanian/02_Education");

		categoryDocuments.put("engineering",
				"resources/dataset/jordanian/03_Engineering");

		categoryDocuments.put("law", "resources/dataset/jordanian/04_Law");

		categoryDocuments.put("medicine",
				"resources/dataset/jordanian/05_Medicine");

		categoryDocuments.put("politics",
				"resources/dataset/jordanian/06_Politics");

		categoryDocuments.put("religion",
				"resources/dataset/jordanian/07_Religion");

		categoryDocuments
				.put("sports", "resources/dataset/jordanian/08_Sports");

		for (Entry<String, String> cat : categoryDocuments.entrySet()) {
			String trainPath = cat.getValue() + "/train";
			String testPath = cat.getValue() + "/test";
			File train = new File(trainPath);
			File[] trainDocs = train.listFiles();
			File test = new File(testPath);
			File[] testDocs = test.listFiles();
			File[] all = new File[testDocs.length + trainDocs.length];
			System.arraycopy(trainDocs, 0, all, 0, trainDocs.length);
			System.arraycopy(testDocs, 0, all, trainDocs.length,
					testDocs.length);
			for (File f : all) {

				DatasetDocument doc = new DatasetDocument();
				doc.setCategory(cat.getKey());
				doc.setTitle(f.getName());
				doc.setDataset(Datasets.JORDANIAN);
				doc.setText(getFileContent(f));
				indexer.indexDocument(doc);
			}
		}

		indexer.end();

	}

	public static String getFileContent(File f) throws IOException {
		StringBuilder sb = new StringBuilder();
		Scanner sc = new Scanner(f, "Cp1256");

		while (sc.hasNextLine()) {
			sb.append(sc.nextLine() + " ");
		}
		sc.close();

		return sb.toString();

	}
}
