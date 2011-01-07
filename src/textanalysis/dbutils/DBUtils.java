package textanalysis.dbutils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import textanalysis.wikipediaindex.Article;
import utils.Utils;

import dataset.DatasetDocument;

public class DBUtils {

	private static String HOST, USERNAME, PASSWORD, DATABASE;

	private Connection dbConnection;

	public DBUtils() {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Scanner sc = new Scanner(new File("config/dbconfig"));
			HOST = sc.nextLine().split("\\t")[1];
			USERNAME = sc.nextLine().split("\\t")[1];
			PASSWORD = sc.nextLine().split("\\t")[1];
			DATABASE = sc.nextLine().split("\\t")[1];

			dbConnection = DriverManager.getConnection("jdbc:mysql://" + HOST
					+ "/" + DATABASE
					+ "?useUnicode=true&characterEncoding=utf8", USERNAME,
					PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	private PreparedStatement addDocStmt;

	public void addDocument(DatasetDocument doc) throws SQLException {
		//
		if (addDocStmt == null) {
			addDocStmt = dbConnection
					.prepareStatement("insert into document (title, dataset_id, category) values (?, ?, ?)");
		}
		// title, dataset_id, category, index_name, index_id
		addDocStmt.setString(1, doc.getTitle());
		addDocStmt.setInt(2, doc.getDataset().datasetId);
		addDocStmt.setString(3, doc.getCategory());
		addDocStmt.executeUpdate();
	}

	public List<Integer> getDatasetDocs(int datasetId) throws SQLException {
		List<Integer> ids = new ArrayList<Integer>();
		ResultSet rs = dbConnection.createStatement().executeQuery(
				"SELECT id FROM document where dataset_id = " + datasetId);

		while (rs.next()) {
			ids.add(rs.getInt("id"));
		}
		return ids;
	}

	public Article getWikipediaConcept(int conceptId) {
		return null;
	}

	private PreparedStatement insertWikipediaConceptStmt;

	public void addWikipediaConcept(Article concept) throws SQLException {
		if (insertWikipediaConceptStmt == null)
			insertWikipediaConceptStmt = dbConnection
					.prepareStatement("insert into wikipedia_concept (id, name, url, tags) values (?, ?, ?, ?)");

		insertWikipediaConceptStmt.setInt(1, concept.indexId);
		insertWikipediaConceptStmt.setString(2, concept.name);
		insertWikipediaConceptStmt.setString(3, concept.url);
		insertWikipediaConceptStmt.setString(4,
				Utils.implode(concept.tags, "|"));
		insertWikipediaConceptStmt.executeUpdate();
	}

	public Map<String, Float> getTermAnnotations(int docId, int method) {
		return null;
	}

	private PreparedStatement insertTermAnnotationsStmt;

	public void addTermAnnotations(Map<String, Float> annotations, int docId,
			int method) throws SQLException {

		if (insertTermAnnotationsStmt == null)
			insertTermAnnotationsStmt = dbConnection
					.prepareStatement("insert into term_annotation (doc_id, term, weight, method) values (?,?,?,?)");

		for (Entry<String, Float> term : annotations.entrySet()) {
			insertTermAnnotationsStmt.setInt(1, docId);
			insertTermAnnotationsStmt.setString(2, term.getKey());
			insertTermAnnotationsStmt.setFloat(3, term.getValue());
			insertTermAnnotationsStmt.setInt(4, method);
			insertTermAnnotationsStmt.executeUpdate();
		}

	}

	public Map<Integer, Float> getConceptAnnotations(int docId, int method) {
		return null;
	}

	private PreparedStatement insertSemanticsStmt;

	public void addConceptAnnotations(Map<Integer, Float> annotations,
			int docId, int method) throws SQLException {

		if (insertSemanticsStmt == null)
			insertSemanticsStmt = dbConnection
					.prepareStatement("insert into semantic_annotation (document, concept_id, weight, method) values (?, ?, ?, ?)");

		for (Entry<Integer, Float> concept : annotations.entrySet()) {
			insertSemanticsStmt.setInt(1, docId);
			insertSemanticsStmt.setInt(2, concept.getKey());
			insertSemanticsStmt.setFloat(3, concept.getValue());
			insertSemanticsStmt.setInt(4, method);
			insertSemanticsStmt.executeUpdate();
		}
	}

}
