package org.apache.lucene.analysis.ar;

import java.io.IOException;

public class ArabicKhojaRootStemmer {

	private KhojaArabicStemmerRules stemmingRules;
	private KhojaArabicStemerManager stemmer;

	/**
	 * A NullPointerException is going to be thrown if the directory
	 * "StemmerFiles" could not be opened.
	 */
	public ArabicKhojaRootStemmer() {
		try {
			stemmingRules = new KhojaArabicStemmerRules(
					"resources/StemmerFiles/");

		} catch (IOException e) {

			e.printStackTrace();
		}
		stemmer = new KhojaArabicStemerManager(stemmingRules.staticFiles);

	}

	public int stem(char s[], int len) {

		String word = new String(s, 0, len);
		String stem = stemmer.stemWord(word);		
		int stemLen = stem.length();
		System.arraycopy(stem.toCharArray(), 0, s, 0, stemLen);
		return stemLen;

	}
}
