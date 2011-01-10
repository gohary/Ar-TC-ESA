package org.apache.lucene.analysis.ar;

/*

 Arabic Stemmer: This program stems Arabic words and returns their root.
 Copyright (C) 2002 Shereen Khoja

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 Computing Department
 Lancaster University
 Lancaster
 LA1 4YR
 s.Khoja@lancaster.ac.uk


 Stem class
 This is the class that does all the work
 It takes in a file containg the text to
 be stemmed. The text is then read one line
 at a time so large amounts of text can be
 efficiently processed.
 The class also takes in the vector
 containing all the contents of the static
 files.
 The class now tests for stopwords correctly,
 and removes punctuation and diacritics (though
 I haven't decided whether to return them to the
 word in the final document or not)


 Last Modified: 11/6/2001
 */

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

//TODO
@SuppressWarnings("all")
public class KhojaArabicStemerManager {

	// --------------------------------------------------------------------------

	// boolean variable to check the files
	protected boolean couldNotOpenFile = false;

	// one line of text
	private String oneLine;

	// all text to be passed to the text window
	private StringBuffer all = new StringBuffer("");

	// the stemmed Arabic text
	private Vector stemmedText = new Vector();

	// the tokenized line with all punctuation removed
	@SuppressWarnings("rawtypes")
	private Vector tokenizedLine = new Vector();

	// the files containing prefixes, suffixes and so on
	private Vector staticFiles;

	// have the root, pattern, stopword or strange words been found
	private boolean rootFound = false;

	private boolean stopwordFound = false;
	private boolean strangeWordFound = false;
	private boolean rootNotFound = false;
	private boolean fromSuffixes = false;

	private int wordNumber = 0;
	private int stemmedWordsNumber = 0;
	private int notStemmedWordsNumber = 0;
	private int stopwordNumber = 0;
	private int punctuationWordNumber = 0;
	private int notWordNumber = 0;
	private int[] numberStatistics;
	private Vector listStemmedWords = new Vector();
	private Vector listRootsFound = new Vector();
	private Vector listNotStemmedWords = new Vector();
	private Vector listStopwordsFound = new Vector();
	private Vector listOriginalStopword = new Vector();
	private Vector listsVector = new Vector();
	private Vector wordsNotStemmed = new Vector();
	private Vector wordsWithNoRoots = new Vector();
	int number = 0;

	// --------------------------------------------------------------------------

	// constructor
	public KhojaArabicStemerManager(Vector statFiles) {

		// clone the static files
		staticFiles = (Vector) statFiles.clone();

		// read the contents of the file, one line at a time
		// after each line stem the words
		// readFromFile ( fileToBeStemmed );

		// timestamp
		// DateFormat defaultDate = DateFormat.getDateInstance();
		// all.append("This file was created on ");
		// all.append(defaultDate.format(new Date()));
		//
		// DateFormat shortTime = DateFormat.getTimeInstance(DateFormat.SHORT);
		// all.append(" , at ");
		// all.append(shortTime.format(new Date()));
		// all.append("\n\n");
		// // timestamp
		//
		// // store results of stemming into a string variable (all)
		// all.append("Document contained " + wordNumber + " words\n");
		// for (int i = 0; i < wordNumber; i++)
		// all.append(stemmedDocument[i][0] + "\t" + stemmedDocument[i][1]
		// + "\t" + stemmedDocument[i][2] + "\n");
	}

	// --------------------------------------------------------------------------

	// return the results
	public String displayText() {
		return (all.toString());
	}

	// --------------------------------------------------------------------------

	// return the stemming statistics
	public int[] returnNumberStatistics() {
		numberStatistics = new int[6];
		numberStatistics[0] = wordNumber;
		numberStatistics[1] = stemmedWordsNumber;
		numberStatistics[2] = notStemmedWordsNumber;
		numberStatistics[3] = stopwordNumber;
		numberStatistics[4] = punctuationWordNumber;
		numberStatistics[5] = notWordNumber;
		return numberStatistics;
	}

	// --------------------------------------------------------------------------

	// --------------------------------------------------------------------------

	// return the lists
	public Vector returnLists() {
		listsVector.addElement(listStemmedWords);
		listsVector.addElement(listRootsFound);
		listsVector.addElement(listNotStemmedWords);
		listsVector.addElement(listOriginalStopword);
		listsVector.addElement(listStopwordsFound);
		return listsVector;
	}

	// --------------------------------------------------------------------------

	// return the number of possible roots
	public int returnNoPossibleRoots() {
		return number;
	}

	// --------------------------------------------------------------------------

	// read the contents of the file and tokenize the text
	private void readFromFile(File currentFile) {
		try {
			// read from the file using FileReader
			FileInputStream in = new FileInputStream(currentFile);
			InputStreamReader str = new InputStreamReader(in, "UTF-16");
			BufferedReader br = new BufferedReader(str);

			// initialize index
			StringBuffer word = new StringBuffer();
			Character character;
			String currentWord;
			int lineNumber = 1;

			// read in the text a line at a time
			while ((oneLine = br.readLine()) != null) {
				// add spaces at the end of the line
				oneLine = oneLine + "  ";

				lineNumber++;

				// tokenize each line
				for (int i = 0; i < oneLine.length(); i++) {
					// if the character is not a space, append it to a word
					if ((!(character = new Character(oneLine.charAt(i)))
							.isWhitespace(oneLine.charAt(i)))) {
						word.append(oneLine.charAt(i));
					}
					// otherwise, if the word contains some characters, add it
					// to the vector
					else {
						if (word.length() != 0) {
							tokenizedLine.addElement(word.toString());
							word.setLength(0);
						}
					}

				}

				// now we have tokenized one line, we should stem it
				for (int i = 0; i < tokenizedLine.size(); i++) {
					// set the word in a string
					currentWord = tokenizedLine.elementAt(i).toString();

					// store the original word in the array stemmedDocument

					// stem the word
					currentWord = formatWord(currentWord, i);

					// if the word wasn't stemmed, indicate this in
					// stemmedDocument

					// increment wordNumber
					wordNumber++;

					// re-initialize the variable rootFound
					rootFound = false;

					// add the stemmed word to the vector
					stemmedText.addElement(currentWord);
				}

				// after adding all the stemmed word on this line, we should add
				// a new line character
				stemmedText.addElement("\n");

				// after we have finished processing this line we should clear
				// it
				tokenizedLine.removeAllElements();

			}

			// close the FileReader
			br.close();
			in.close();
		}

		catch (IOException e) {
			// insert an error message box here
			System.out.println("Could not open file ");
			couldNotOpenFile = true;
		}
	}

	// --------------------------------------------------------------------------

	// format the word by removing any punctuation, diacritics and non-letter
	// charracters
	public String formatWord(String currentWord, int index) {
		StringBuffer modifiedWord = new StringBuffer();

		// remove any diacritics (short vowels)
		if (removeDiacritics(currentWord, modifiedWord)) {
			tokenizedLine.setElementAt(currentWord = modifiedWord.toString(),
					index);
		}

		// remove any punctuation from the word
		if (removePunctuation(currentWord, modifiedWord)) {
			tokenizedLine.setElementAt(currentWord = modifiedWord.toString(),
					index);
		}

		// there could also be characters that aren't letters which should be
		// removed
		if (removeNonLetter(currentWord, modifiedWord)) {
			tokenizedLine.setElementAt(currentWord = modifiedWord.toString(),
					index);
		}

		// check for stopwords
		if (!checkStrangeWords(currentWord))
			// check for stopwords
			if (!checkStopwords(currentWord))
				currentWord = stemWord(currentWord);

		// rootFound = false;
		// stopwordFound = false;
		return currentWord;
	}

	// --------------------------------------------------------------------------

	// stem the word
	public String stemWord(String word) {
		rootFound = false;
		stopwordFound = false;
		// check if the word consists of two letters
		// and find it's root
		if (word.length() == 2)
			word = isTwoLetters(word);

		// if the word consists of three letters
		if (word.length() == 3 && !rootFound)
			// check if it's a root
			word = isThreeLetters(word);

		// if the word consists of four letters
		if (word.length() == 4)
			// check if it's a root
			isFourLetters(word);

		// if the root hasn't yet been found
		if (!rootFound) {
			// check if the word is a pattern
			word = checkPatterns(word);
		}

		// if the root still hasn't been found
		if (!rootFound) {
			// check for a definite article, and remove it
			word = checkDefiniteArticle(word);
		}

		// if the root still hasn't been found
		if (!rootFound && !stopwordFound) {
			// check for the prefix waw
			word = checkPrefixWaw(word);
		}

		// if the root STILL hasnt' been found
		if (!rootFound && !stopwordFound) {
			// check for suffixes
			word = checkForSuffixes(word);
		}

		// if the root STILL hasn't been found
		if (!rootFound && !stopwordFound) {
			// check for prefixes
			word = checkForPrefixes(word);
		}
		return word;
	}

	// --------------------------------------------------------------------------

	// check and remove any prefixes
	private String checkForPrefixes(String word) {
		String prefix = "";
		String modifiedWord = word;
		Vector prefixes = (Vector) staticFiles.elementAt(10);

		// for every prefix in the list
		for (int i = 0; i < prefixes.size(); i++) {
			prefix = (String) prefixes.elementAt(i);
			// if the prefix was found
			if (prefix.regionMatches(0, modifiedWord, 0, prefix.length())) {
				modifiedWord = modifiedWord.substring(prefix.length());

				// check to see if the word is a stopword
				if (checkStopwords(modifiedWord))
					return modifiedWord;

				// check to see if the word is a root of three or four letters
				// if the word has only two letters, test to see if one was
				// removed
				if (modifiedWord.length() == 2)
					modifiedWord = isTwoLetters(modifiedWord);
				else if (modifiedWord.length() == 3 && !rootFound)
					modifiedWord = isThreeLetters(modifiedWord);
				else if (modifiedWord.length() == 4)
					isFourLetters(modifiedWord);

				// if the root hasn't been found, check for patterns
				if (!rootFound && modifiedWord.length() > 2)
					modifiedWord = checkPatterns(modifiedWord);

				// if the root STILL hasn't been found
				if (!rootFound && !stopwordFound && !fromSuffixes) {
					// check for suffixes
					modifiedWord = checkForSuffixes(modifiedWord);
				}

				if (stopwordFound)
					return modifiedWord;

				// if the root was found, return the modified word
				if (rootFound && !stopwordFound) {
					return modifiedWord;
				}
			}
		}
		return word;
	}

	// --------------------------------------------------------------------------

	// METHOD CHECKFORSUFFIXES
	private String checkForSuffixes(String word) {
		String suffix = "";
		String modifiedWord = word;
		Vector suffixes = (Vector) staticFiles.elementAt(14);
		fromSuffixes = true;

		// for every suffix in the list
		for (int i = 0; i < suffixes.size(); i++) {
			suffix = (String) suffixes.elementAt(i);

			// if the suffix was found
			if (suffix.regionMatches(0, modifiedWord, modifiedWord.length()
					- suffix.length(), suffix.length())) {
				modifiedWord = modifiedWord.substring(0, modifiedWord.length()
						- suffix.length());

				// check to see if the word is a stopword
				if (checkStopwords(modifiedWord)) {
					fromSuffixes = false;
					return modifiedWord;
				}

				// check to see if the word is a root of three or four letters
				// if the word has only two letters, test to see if one was
				// removed
				if (modifiedWord.length() == 2) {
					modifiedWord = isTwoLetters(modifiedWord);
				} else if (modifiedWord.length() == 3) {
					modifiedWord = isThreeLetters(modifiedWord);
				} else if (modifiedWord.length() == 4) {
					isFourLetters(modifiedWord);
				}

				// if the root hasn't been found, check for patterns
				if (!rootFound && modifiedWord.length() > 2) {
					modifiedWord = checkPatterns(modifiedWord);
				}

				if (stopwordFound) {
					fromSuffixes = false;
					return modifiedWord;
				}

				// if the root was found, return the modified word
				if (rootFound) {
					fromSuffixes = false;
					return modifiedWord;
				}
			}
		}
		fromSuffixes = false;
		return word;
	}

	// --------------------------------------------------------------------------

	// check and remove the special prefix (waw)
	private String checkPrefixWaw(String word) {
		String modifiedWord = "";

		if (word.length() > 3 && word.charAt(0) == '\u0648') {
			modifiedWord = word.substring(1);

			// check to see if the word is a stopword
			if (checkStopwords(modifiedWord))
				return modifiedWord;

			// check to see if the word is a root of three or four letters
			// if the word has only two letters, test to see if one was removed
			if (modifiedWord.length() == 2)
				modifiedWord = isTwoLetters(modifiedWord);
			else if (modifiedWord.length() == 3 && !rootFound)
				modifiedWord = isThreeLetters(modifiedWord);
			else if (modifiedWord.length() == 4)
				isFourLetters(modifiedWord);

			// if the root hasn't been found, check for patterns
			if (!rootFound && modifiedWord.length() > 2)
				modifiedWord = checkPatterns(modifiedWord);

			// if the root STILL hasnt' been found
			if (!rootFound && !stopwordFound) {
				// check for suffixes
				modifiedWord = checkForSuffixes(modifiedWord);
			}

			// iIf the root STILL hasn't been found
			if (!rootFound && !stopwordFound) {
				// check for prefixes
				modifiedWord = checkForPrefixes(modifiedWord);
			}

			if (stopwordFound)
				return modifiedWord;

			if (rootFound && !stopwordFound) {
				return modifiedWord;
			}
		}
		return word;
	}

	// --------------------------------------------------------------------------

	// check and remove the definite article
	private String checkDefiniteArticle(String word) {
		// looking through the vector of definite articles
		// search through each definite article, and try and
		// find a match
		String definiteArticle = "";
		String modifiedWord = "";
		Vector definiteArticles = (Vector) staticFiles.elementAt(0);

		// for every definite article in the list
		for (int i = 0; i < definiteArticles.size(); i++) {
			definiteArticle = (String) definiteArticles.elementAt(i);
			// if the definite article was found
			if (definiteArticle.regionMatches(0, word, 0,
					definiteArticle.length())) {
				// remove the definite article
				modifiedWord = word.substring(definiteArticle.length(),
						word.length());

				// check to see if the word is a stopword
				if (checkStopwords(modifiedWord))
					return modifiedWord;

				// check to see if the word is a root of three or four letters
				// if the word has only two letters, test to see if one was
				// removed
				if (modifiedWord.length() == 2)
					modifiedWord = isTwoLetters(modifiedWord);
				else if (modifiedWord.length() == 3 && !rootFound)
					modifiedWord = isThreeLetters(modifiedWord);
				else if (modifiedWord.length() == 4)
					isFourLetters(modifiedWord);

				// if the root hasn't been found, check for patterns
				if (!rootFound && modifiedWord.length() > 2)
					modifiedWord = checkPatterns(modifiedWord);

				// if the root STILL hasnt' been found
				if (!rootFound && !stopwordFound) {
					// check for suffixes
					modifiedWord = checkForSuffixes(modifiedWord);
				}

				// if the root STILL hasn't been found
				if (!rootFound && !stopwordFound) {
					// check for prefixes
					modifiedWord = checkForPrefixes(modifiedWord);
				}

				if (stopwordFound)
					return modifiedWord;

				// if the root was found, return the modified word
				if (rootFound && !stopwordFound) {
					return modifiedWord;
				}
			}
		}
		if (modifiedWord.length() > 3)
			return modifiedWord;
		return word;
	}

	// --------------------------------------------------------------------------

	// if the word consists of two letters
	private String isTwoLetters(String word) {
		// if the word consists of two letters, then this could be either
		// - because it is a root consisting of two letters (though I can't
		// think of any!)
		// - because a letter was deleted as it is duplicated or a weak middle
		// or last letter.

		word = duplicate(word);

		// check if the last letter was weak
		if (!rootFound)
			word = lastWeak(word);

		// check if the first letter was weak
		if (!rootFound)
			word = firstWeak(word);

		// check if the middle letter was weak
		if (!rootFound)
			word = middleWeak(word);

		return word;
	}

	// --------------------------------------------------------------------------

	// if the word consists of three letters
	private String isThreeLetters(String word) {
		StringBuffer modifiedWord = new StringBuffer(word);
		String root = "";
		// if the first letter is a '�', '�' or '�'
		// then change it to a '�'
		if (word.length() > 0) {
			if (word.charAt(0) == '\u0627' || word.charAt(0) == '\u0624'
					|| word.charAt(0) == '\u0626') {
				modifiedWord.setLength(0);
				modifiedWord.append('\u0623');
				modifiedWord.append(word.substring(1));
				root = modifiedWord.toString();
			}

			// if the last letter is a weak letter or a hamza
			// then remove it and check for last weak letters
			if (word.charAt(2) == '\u0648' || word.charAt(2) == '\u064a'
					|| word.charAt(2) == '\u0627' || word.charAt(2) == '\u0649'
					|| word.charAt(2) == '\u0621' || word.charAt(2) == '\u0626') {
				root = word.substring(0, 2);
				root = lastWeak(root);
				if (rootFound) {
					return root;
				}
			}

			// if the second letter is a weak letter or a hamza
			// then remove it
			if (word.charAt(1) == '\u0648' || word.charAt(1) == '\u064a'
					|| word.charAt(1) == '\u0627' || word.charAt(1) == '\u0626') {
				root = word.substring(0, 1);
				root = root + word.substring(2);

				root = middleWeak(root);
				if (rootFound) {
					return root;
				}
			}

			// if the second letter has a hamza, and it's not on a alif
			// then it must be returned to the alif
			if (word.charAt(1) == '\u0624' || word.charAt(1) == '\u0626') {
				if (word.charAt(2) == '\u0645' || word.charAt(2) == '\u0632'
						|| word.charAt(2) == '\u0631') {
					root = word.substring(0, 1);
					root = root + '\u0627';
					root = root + word.substring(2);
				} else {
					root = word.substring(0, 1);
					root = root + '\u0623';
					root = root + word.substring(2);
				}
			}

			// if the last letter is a shadda, remove it and
			// duplicate the last letter
			if (word.charAt(2) == '\u0651') {
				root = word.substring(0, 1);
				root = root + word.substring(1, 2);
			}
		}

		// if word is a root, then rootFound is true
		if (root.length() == 0) {
			if (((Vector) staticFiles.elementAt(16)).contains(word)) {
				rootFound = true;

				stemmedWordsNumber++;

				listRootsFound.addElement(word);
				if (rootNotFound) {
					for (int i = 0; i < number; i++)
						wordsNotStemmed.removeElement(wordsNotStemmed
								.lastElement());
					rootNotFound = false;
				}
				return word;
			}
		}
		// check for the root that we just derived
		else if (((Vector) staticFiles.elementAt(16)).contains(root)) {
			rootFound = true;

			stemmedWordsNumber++;

			listRootsFound.addElement(word);
			if (rootNotFound) {
				for (int i = 0; i < number; i++)
					wordsNotStemmed
							.removeElement(wordsNotStemmed.lastElement());
				rootNotFound = false;
			}
			return root;
		}

		if (root.length() == 3) {

			number++;
		} else {

			number++;
		}
		return word;
	}

	// --------------------------------------------------------------------------

	// if the word has four letters
	private void isFourLetters(String word) {
		// if word is a root, then rootFound is true
		if (((Vector) staticFiles.elementAt(12)).contains(word)) {
			rootFound = true;

			stemmedWordsNumber++;
			listRootsFound.addElement(word);
		}
	}

	// --------------------------------------------------------------------------

	// check if the word matches any of the patterns
	private String checkPatterns(String word) {
		StringBuffer root = new StringBuffer("");
		// if the first letter is a hamza, change it to an alif
		if (word.length() > 0)
			if (word.charAt(0) == '\u0623' || word.charAt(0) == '\u0625'
					|| word.charAt(0) == '\u0622') {
				root.append("j");
				root.setCharAt(0, '\u0627');
				root.append(word.substring(1));
				word = root.toString();
			}

		// try and find a pattern that matches the word
		Vector patterns = (Vector) staticFiles.elementAt(15);
		int numberSameLetters = 0;
		String pattern = "";
		String modifiedWord = "";

		// for every pattern
		for (int i = 0; i < patterns.size(); i++) {
			pattern = (String) patterns.elementAt(i);
			root.setLength(0);
			// if the length of the words are the same
			if (pattern.length() == word.length()) {
				numberSameLetters = 0;
				// find out how many letters are the same at the same index
				// so long as they're not a fa, ain, or lam
				for (int j = 0; j < word.length(); j++)
					if (pattern.charAt(j) == word.charAt(j)
							&& pattern.charAt(j) != '\u0641'
							&& pattern.charAt(j) != '\u0639'
							&& pattern.charAt(j) != '\u0644')
						numberSameLetters++;

				// test to see if the word matches the pattern �����
				if (word.length() == 6 && word.charAt(3) == word.charAt(5)
						&& numberSameLetters == 2) {
					root.append(word.charAt(1));
					root.append(word.charAt(2));
					root.append(word.charAt(3));
					modifiedWord = root.toString();
					modifiedWord = isThreeLetters(modifiedWord);
					if (rootFound)
						return modifiedWord;
					else
						root.setLength(0);
				}

				// if the word matches the pattern, get the root
				if (word.length() - 3 <= numberSameLetters) {
					// derive the root from the word by matching it with the
					// pattern
					for (int j = 0; j < word.length(); j++)
						if (pattern.charAt(j) == '\u0641'
								|| pattern.charAt(j) == '\u0639'
								|| pattern.charAt(j) == '\u0644')
							root.append(word.charAt(j));

					modifiedWord = root.toString();
					modifiedWord = isThreeLetters(modifiedWord);

					if (rootFound) {
						word = modifiedWord;
						return word;
					}
				}
			}
		}
		return word;
	}

	// --------------------------------------------------------------------------

	// remove non-letters from the word
	private boolean removeNonLetter(String currentWord,
			StringBuffer modifiedWord) {
		boolean nonLetterFound = false;
		modifiedWord.setLength(0);

		// if any of the word is not a letter then remove it
		for (int i = 0; i < currentWord.length(); i++) {
			if (Character.isLetter(currentWord.charAt(i))) {
				modifiedWord.append(currentWord.charAt(i));

			} else {
				nonLetterFound = true;

			}
		}
		return nonLetterFound;
	}

	// --------------------------------------------------------------------------

	// handle duplicate letters in the word
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String duplicate(String word) {
		// check if a letter was duplicated
		if (((Vector) staticFiles.elementAt(1)).contains(word)) {
			// if so, then return the deleted duplicate letter
			word = word + word.substring(1);

			// root was found, so set variable
			rootFound = true;

			stemmedWordsNumber++;

			listRootsFound.addElement(word);

			return word;
		}
		return word;
	}

	// --------------------------------------------------------------------------

	// check if the last letter of the word is a weak letter
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String lastWeak(String word) {
		StringBuffer stemmedWord = new StringBuffer("");
		// check if the last letter was an alif
		if (((Vector) staticFiles.elementAt(4)).contains(word)) {
			stemmedWord.append(word);
			stemmedWord.append("\u0627");
			word = stemmedWord.toString();
			stemmedWord.setLength(0);

			// root was found, so set variable
			rootFound = true;

			stemmedWordsNumber++;

			listRootsFound.addElement(word);

			return word;
		}
		// check if the last letter was an hamza
		else if (((Vector) staticFiles.elementAt(5)).contains(word)) {
			stemmedWord.append(word);
			stemmedWord.append("\u0623");
			word = stemmedWord.toString();
			stemmedWord.setLength(0);

			// root was found, so set variable
			rootFound = true;

			stemmedWordsNumber++;

			listRootsFound.addElement(word);

			return word;
		}
		// check if the last letter was an maksoura
		else if (((Vector) staticFiles.elementAt(6)).contains(word)) {
			stemmedWord.append(word);
			stemmedWord.append("\u0649");
			word = stemmedWord.toString();
			stemmedWord.setLength(0);

			// root was found, so set variable
			rootFound = true;

			stemmedWordsNumber++;

			listRootsFound.addElement(word);

			return word;
		}
		// check if the last letter was an yah
		else if (((Vector) staticFiles.elementAt(7)).contains(word)) {
			stemmedWord.append(word);
			stemmedWord.append("\u064a");
			word = stemmedWord.toString();
			stemmedWord.setLength(0);

			// root was found, so set variable
			rootFound = true;

			stemmedWordsNumber++;

			listRootsFound.addElement(word);

			return word;
		}
		return word;
	}

	// --------------------------------------------------------------------------

	// check if the first letter is a weak letter
	private String firstWeak(String word) {
		StringBuffer stemmedWord = new StringBuffer("");
		// check if the firs letter was a waw
		if (((Vector) staticFiles.elementAt(2)).contains(word)) {
			stemmedWord.append("\u0648");
			stemmedWord.append(word);
			word = stemmedWord.toString();
			stemmedWord.setLength(0);

			// root was found, so set variable
			rootFound = true;

			stemmedWordsNumber++;

			listRootsFound.addElement(word);

			return word;
		}
		// check if the first letter was a yah
		else if (((Vector) staticFiles.elementAt(3)).contains(word)) {
			stemmedWord.append("\u064a");
			stemmedWord.append(word);
			word = stemmedWord.toString();
			stemmedWord.setLength(0);

			// root was found, so set variable
			rootFound = true;

			stemmedWordsNumber++;

			listRootsFound.addElement(word);

			return word;
		}
		return word;
	}

	// --------------------------------------------------------------------------

	// check if the middle letter of the root is weak
	private String middleWeak(String word) {
		StringBuffer stemmedWord = new StringBuffer("j");
		// check if the middle letter is a waw
		if (((Vector) staticFiles.elementAt(8)).contains(word)) {
			// return the waw to the word
			stemmedWord.setCharAt(0, word.charAt(0));
			stemmedWord.append("\u0648");
			stemmedWord.append(word.substring(1));
			word = stemmedWord.toString();
			stemmedWord.setLength(0);

			// root was found, so set variable
			rootFound = true;

			stemmedWordsNumber++;

			listRootsFound.addElement(word);

			return word;
		}
		// check if the middle letter is a yah
		else if (((Vector) staticFiles.elementAt(9)).contains(word)) {
			// return the waw to the word
			stemmedWord.setCharAt(0, word.charAt(0));
			stemmedWord.append("\u064a");
			stemmedWord.append(word.substring(1));
			word = stemmedWord.toString();
			stemmedWord.setLength(0);

			// root was found, so set variable
			rootFound = true;

			stemmedWordsNumber++;

			listRootsFound.addElement(word);

			return word;
		}
		return word;
	}

	// --------------------------------------------------------------------------

	// remove punctuation from the word
	private boolean removePunctuation(String currentWord,
			StringBuffer modifiedWord) {
		boolean punctuationFound = false;
		modifiedWord.setLength(0);
		Vector punctuations = (Vector) staticFiles.elementAt(11);

		// for every character in the current word, if it is a punctuation then
		// do nothing
		// otherwise, copy this character to the modified word
		for (int i = 0; i < currentWord.length(); i++) {
			if (!(punctuations.contains(currentWord.substring(i, i + 1)))) {
				modifiedWord.append(currentWord.charAt(i));

			} else {
				punctuationFound = true;

			}
		}

		return punctuationFound;
	}

	// --------------------------------------------------------------------------

	// remove diacritics from the word
	private boolean removeDiacritics(String currentWord,
			StringBuffer modifiedWord) {
		boolean diacriticFound = false;
		modifiedWord.setLength(0);
		Vector diacritics = (Vector) staticFiles.elementAt(17);

		for (int i = 0; i < currentWord.length(); i++)
			// if the character is not a diacritic, append it to modified word
			if (!(diacritics.contains(currentWord.substring(i, i + 1))))
				modifiedWord.append(currentWord.substring(i, i + 1));
			else {
				diacriticFound = true;
			}
		return diacriticFound;
	}

	// --------------------------------------------------------------------------

	// check that the word is a stopword
	private boolean checkStopwords(String currentWord) {
		Vector v = (Vector) staticFiles.elementAt(13);

		if (stopwordFound = v.contains(currentWord)) {
			stopwordNumber++;
			listStopwordsFound.addElement(currentWord);

		}
		return stopwordFound;
	}

	// --------------------------------------------------------------------------

	// check that the word is a strange word
	private boolean checkStrangeWords(String currentWord) {
		Vector v = (Vector) staticFiles.elementAt(18);

		if (strangeWordFound = v.contains(currentWord)) {
			stopwordNumber++;
			listStopwordsFound.addElement(currentWord);

		}
		return strangeWordFound;
	}

}