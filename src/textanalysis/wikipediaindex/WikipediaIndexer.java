package textanalysis.wikipediaindex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class WikipediaIndexer {

	final static String WIKIPEDIA_INDEX_PATH = "resources/indexes/wikipedia";
	private final static String WIKIPEDIA_DUMP_APTH = "resources/ar-wikipedia/ar-wiki-dump";

	private Directory directory;

	private IndexWriter indexWriter;

	final static int NUM_ARTICLES = 124494;

	public WikipediaIndexer() {
		try {
			directory = FSDirectory.open(new File(WIKIPEDIA_INDEX_PATH));
			indexWriter = new IndexWriter(directory, new ArabicAnalyzer(
					Version.LUCENE_30), true,
					IndexWriter.MaxFieldLength.UNLIMITED);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	private BufferedReader dumpReader;

	public void buildIndex() throws IOException {
		dumpReader = new BufferedReader(new FileReader(WIKIPEDIA_DUMP_APTH));
		int counter = 0;
		Article article;
		HashSet<String> distinctName = new HashSet<String>();
		while ((article = nextArticle()) != null) {
			if (distinctName.contains(article.name))
				continue;

			distinctName.add(article.name);
			Document document = new Document();
			document.add(new Field("articleName", article.name, Store.YES,
					Index.NOT_ANALYZED));

			document.add(new Field("articleURL", article.url, Store.YES,
					Index.NOT_ANALYZED));

			for (String tag : article.tags)
				document.add(new Field("articleTags", tag, Store.YES,
						Index.NOT_ANALYZED));

			document.add(new Field("articleContent", article.content + " "
					+ article.name, Store.YES, Index.ANALYZED));

			indexWriter.addDocument(document);
			System.out.println(counter++);
		}
		try {

			indexWriter.close();
			dumpReader.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(distinctName.size());
	}

	public class Article {
		public String name, url;
		public String[] tags;
		public String content;
	}

	/**
	 * 
	 * @return null if no more articles
	 * @throws IOException
	 */
	private Article nextArticle() throws IOException {
		String line;
		if ((line = dumpReader.readLine()) == null)
			return null;
		StringBuilder contentBuilder = new StringBuilder("");
		Article article = new Article();
		article.name = line;
		article.url = dumpReader.readLine();
		article.tags = dumpReader.readLine().split(",");

		do {
			line = dumpReader.readLine();

			if (line.equals(ARTICLE_TERMINATION)) {
				article.content = contentBuilder.toString();
				return article;
			}

			contentBuilder.append(line);
		} while (true);

	}

	private final static String ARTICLE_TERMINATION = "=================*==============END=================*==============";

	public static void main(String[] args) throws IOException {
		new WikipediaIndexer().buildIndex();
	}
}
