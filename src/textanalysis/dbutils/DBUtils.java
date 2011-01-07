package textanalysis.dbutils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

import textanalysis.wikipediaindex.Article;

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
					.prepareStatement("insert into document (title, dataset_id, category, index_name, index_id) values (?, ?, ?, ?, ?)");
		}
		// title, dataset_id, category, index_name, index_id
		addDocStmt.setString(1, doc.getTitle());
		addDocStmt.setInt(2, doc.getDataset().datasetId);
		addDocStmt.setString(3, doc.getCategory());
		addDocStmt.setString(4, doc.getIndexName());
		addDocStmt.setInt(5, doc.getIndexId());
		addDocStmt.executeUpdate();
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
