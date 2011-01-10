package morphologytesting;

import org.apache.lucene.analysis.ar.ArabicKhojaRootStemmer;

public class StemmersTesting {

	public static void main(String[] args) {
		ArabicKhojaRootStemmer stemmer = new ArabicKhojaRootStemmer();
		char[] s = "الشريعة".toCharArray();
		int len = s.length;
		int newLen = stemmer.stem(s, len);
		System.out.println("root: "+new String(s, 0, newLen));

	}
}
