package textanalysis.wikipediaindex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;

import textanalysis.dbutils.DBUtils;

public class IndexInfoDBWriter {

	private final static String WIKIPEDIA_DUMP_APTH = "resources/ar-wikipedia/ar-wiki-dump";

	private DBUtils dbUtils;

	public IndexInfoDBWriter() {
		dbUtils = new DBUtils();
	}

	private BufferedReader dumpReader;

	public void writeToDB() throws IOException, SQLException {
		dumpReader = new BufferedReader(new FileReader(WIKIPEDIA_DUMP_APTH));
		int counter = 0;
		Article article;
		HashSet<String> distinctName = new HashSet<String>();
		while ((article = nextArticle()) != null) {
			if (distinctName.contains(article.name))
				continue;

			distinctName.add(article.name);
			article.indexId = counter;
			dbUtils.addWikipediaConcept(article);

			System.out.println(counter++);
		}
		System.out.println(distinctName.size());
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

	public static void main(String[] args) throws IOException, SQLException {
		new IndexInfoDBWriter().writeToDB();
	}
}
