package textanalysis.wikipediaindex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class WikipediaSearcher {

	private Directory directory;
	private IndexSearcher indexSearcher;

	private QueryParser queryParser;

	public WikipediaSearcher() {
		BooleanQuery.setMaxClauseCount(1024 * 100);
		try {
			directory = FSDirectory.open(new File(
					WikipediaIndexer.WIKIPEDIA_INDEX_PATH));

			indexSearcher = new IndexSearcher(directory);
			queryParser = new QueryParser(Version.LUCENE_30, "articleContent",
					new ArabicAnalyzer(Version.LUCENE_30));

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public List<Article> search(String text) {

		List<Article> matchings = new ArrayList<Article>();

		try {
			text = QueryParser.escape(text);

			Query query = queryParser.parse(text);

			TopDocs hits = indexSearcher.search(query, 100);
			ScoreDoc[] scoreDocs = hits.scoreDocs;

			for (ScoreDoc scoreDoc : scoreDocs) {
				Article art = getArticle(scoreDoc.doc);
				art.matchingScore = scoreDoc.score;
				matchings.add(art);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

		return matchings;
	}

	public Article getArticle(int indexId) {
		try {
			Document doc = indexSearcher.doc(indexId);
			Article article = new Article();
			article.indexId = indexId;
			article.name = doc.get("articleName");
			article.url = doc.get("articleURL");
			return article;
		} catch (CorruptIndexException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}

	}

	public static void main(String[] args) throws FileNotFoundException {
		// test

		StringBuilder queryBuilder = new StringBuilder();

		Scanner sc = new Scanner(new File("resources/dataset/test"));

		while (sc.hasNextLine()) {
			queryBuilder.append(sc.nextLine() + " ");
		}
		sc.close();
		WikipediaSearcher searcher = new WikipediaSearcher();
		List<Article> results = searcher.search(queryBuilder.toString());
		for (Article art : results) {
			System.out.println(art.name + "\t" + art.matchingScore + "\t"
					+ art.indexId);
		}

		/*
		 * WikipediaSearcher searcher = new WikipediaSearcher(); Article a =
		 * searcher.getArticle(6000); System.out.println(a.name);
		 * System.out.println(a.url); System.out.println(a.content);
		 */
	}
}
