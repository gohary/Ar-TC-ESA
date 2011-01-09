package textanalysis.dbutils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import textanalysis.wikipediaindex.Article;
import utils.Utils;

import dataset.DatasetDocument;
import dataset.Datasets;

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

	public List<Integer> getDatasetDocs(Datasets dataset) throws SQLException {
		List<Integer> ids = new ArrayList<Integer>();
		ResultSet rs = dbConnection.createStatement().executeQuery(
				"SELECT id FROM document where dataset_id = "
						+ dataset.datasetId);

		while (rs.next()) {
			ids.add(rs.getInt("id"));
		}
		return ids;
	}

	private PreparedStatement getWikipediaConcept;

	public Article getWikipediaConcept(int conceptId) throws SQLException {
		if (getWikipediaConcept == null)
			getWikipediaConcept = dbConnection
					.prepareStatement("SELECT name, url, tags FROM wikipedia_concept where id = ?");

		getWikipediaConcept.setInt(1, conceptId);
		ResultSet rs = getWikipediaConcept.executeQuery();
		if (!rs.next())
			return null;

		Article article = new Article();
		article.name = rs.getString("name");
		article.url = rs.getString("url");
		article.tags = rs.getString("tags").split("|");
		article.indexId = conceptId;
		return article;
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

	private PreparedStatement getTermAnnotations;

	public Map<String, Float> getTermAnnotations(int docId, int method)
			throws SQLException {
		if (getTermAnnotations == null)
			getTermAnnotations = dbConnection
					.prepareStatement("SELECT term, weight FROM term_annotation where doc_id = ? and method = ?");

		getTermAnnotations.setInt(1, docId);
		getTermAnnotations.setInt(2, method);
		ResultSet rs = getTermAnnotations.executeQuery();
		Map<String, Float> annotations = new HashMap<String, Float>();
		while (rs.next()) {
			annotations.put(rs.getString("term"), rs.getFloat("weight"));
		}
		return annotations;
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

	private PreparedStatement getConceptAnnotations;

	public Map<String, Float> getConceptAnnotations(int docId, int method)
			throws SQLException {
		if (getConceptAnnotations == null)
			getConceptAnnotations = dbConnection
					.prepareStatement("SELECT concept_id, weight FROM semantic_annotation where document = ? and method = ?");
		getConceptAnnotations.setInt(1, docId);
		getConceptAnnotations.setInt(2, method);
		ResultSet rs = getConceptAnnotations.executeQuery();
		Map<String, Float> annotations = new HashMap<String, Float>();
		while (rs.next()) {
			annotations
					.put(rs.getInt("concept_id") + "", rs.getFloat("weight"));
		}
		return annotations;
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

	private PreparedStatement getCatDocsStmt;

	public List<Integer> getCategoryDocuments(Datasets dataset, String category)
			throws SQLException {
		if (getCatDocsStmt == null)
			getCatDocsStmt = dbConnection
					.prepareStatement("SELECT id FROM document where dataset_id = ? and category = ?");

		getCatDocsStmt.setInt(1, dataset.datasetId);
		getCatDocsStmt.setString(2, category);

		ResultSet rs = getCatDocsStmt.executeQuery();
		List<Integer> docs = new ArrayList<Integer>();

		while (rs.next()) {
			docs.add(rs.getInt("id"));
		}
		return docs;
	}

	private PreparedStatement getDsCatsStmt;

	public List<String> getDatasetCategories(Datasets dataset)
			throws SQLException {
		if (getDsCatsStmt == null)
			getDsCatsStmt = dbConnection
					.prepareStatement("SELECT distinct category FROM document where dataset_id = ?");

		getDsCatsStmt.setInt(1, dataset.datasetId);
		ResultSet rs = getDsCatsStmt.executeQuery();
		List<String> cats = new ArrayList<String>();
		while (rs.next()) {
			cats.add(rs.getString("category"));
		}
		return cats;
	}

	private PreparedStatement getDocCatStmt;

	public String getDocumentCategory(int docId) throws SQLException {
		if (getDocCatStmt == null)
			getDocCatStmt = dbConnection
					.prepareStatement("SELECT category FROM document where id = ?");

		getDocCatStmt.setInt(1, docId);
		ResultSet rs = getDocCatStmt.executeQuery();
		if (!rs.next())
			return null;

		return rs.getString("category");
	}

}
