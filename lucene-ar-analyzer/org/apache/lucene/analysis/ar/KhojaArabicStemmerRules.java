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

 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class KhojaArabicStemmerRules {
	// --------------------------------------------------------------------------

	// the gui
	// protected ArabicStemmerGUI arabicStemmerGUI;

	// a vector composed of vectors containing the static files
	@SuppressWarnings("rawtypes")
	public Vector staticFiles;

	private String filesLocation;
	// the current input file
	protected boolean currentInputFilePanelFileNeedsSaving = false;
	protected File currentInputFilePanelFile;

	// the statistics for the stemmed text
	protected int[] stemmedTextStatistics;

	// the stemmed words, roots found, words not stemmed, and stopwords for the
	// stemmed text
	@SuppressWarnings("rawtypes")
	protected Vector stemmedTextLists = new Vector();

	// the possible roots for any unstemmed text
	protected int unstemmedTextNumberOfPossibleRoots;
	protected String[][] unstemmedTextPossibleRoots;

	// --------------------------------------------------------------------------

	// // execution starts here
	// public static void main(String[] args) throws IOException {
	// // create the stemmer
	// ArabicStemmer arabicStemmer = new ArabicStemmer();
	// }

	// --------------------------------------------------------------------------

	// constructor
	public KhojaArabicStemmerRules(String fileLocation) throws IOException {
		// create the gui
		// arabicStemmerGUI = new ArabicStemmerGUI ( this );

		// read in the static files
		this.filesLocation = fileLocation;
		readInStaticFiles();
	}

	// --------------------------------------------------------------------------

	// --------------------------------------------------------------------------

	// read in the static files
	@SuppressWarnings("rawtypes")
	protected void readInStaticFiles() throws IOException {

		String pathToStemmerFiles = filesLocation;

		// create the vector composed of vectors containing the static files
		staticFiles = new Vector();
		if (addVectorFromFile(new StringBuffer(pathToStemmerFiles
				+ "definite_article.txt").toString()))
			if (addVectorFromFile(new StringBuffer(pathToStemmerFiles
					+ "duplicate.txt").toString()))
				if (addVectorFromFile(new StringBuffer(pathToStemmerFiles
						+ "first_waw.txt").toString()))
					if (addVectorFromFile(new StringBuffer(pathToStemmerFiles
							+ "first_yah.txt").toString()))
						if (addVectorFromFile(new StringBuffer(
								pathToStemmerFiles + "last_alif.txt")
								.toString()))
							if (addVectorFromFile(new StringBuffer(
									pathToStemmerFiles + "last_hamza.txt")
									.toString()))
								if (addVectorFromFile(new StringBuffer(
										pathToStemmerFiles
												+ "last_maksoura.txt")
										.toString()))
									if (addVectorFromFile(new StringBuffer(
											pathToStemmerFiles + "last_yah.txt")
											.toString()))
										if (addVectorFromFile(new StringBuffer(
												pathToStemmerFiles
														+ "mid_waw.txt")
												.toString()))
											if (addVectorFromFile(new StringBuffer(
													pathToStemmerFiles
															+ "mid_yah.txt")
													.toString()))
												if (addVectorFromFile(new StringBuffer(
														pathToStemmerFiles
																+ "prefixes.txt")
														.toString()))
													if (addVectorFromFile(new StringBuffer(
															pathToStemmerFiles
																	+ "punctuation.txt")
															.toString()))
														if (addVectorFromFile(new StringBuffer(
																pathToStemmerFiles
																		+ "quad_roots.txt")
																.toString()))
															if (addVectorFromFile(new StringBuffer(
																	pathToStemmerFiles
																			+ "stopwords.txt")
																	.toString()))
																if (addVectorFromFile(new StringBuffer(
																		pathToStemmerFiles
																				+ "suffixes.txt")
																		.toString()))
																	if (addVectorFromFile(new StringBuffer(
																			pathToStemmerFiles
																					+ "tri_patt.txt")
																			.toString()))
																		if (addVectorFromFile(new StringBuffer(
																				pathToStemmerFiles
																						+ "tri_roots.txt")
																				.toString()))
																			if (addVectorFromFile(new StringBuffer(
																					pathToStemmerFiles
																							+ "diacritics.txt")
																					.toString()))
																				if (addVectorFromFile(new StringBuffer(
																						pathToStemmerFiles
																								+ "strange.txt")
																						.toString())) {
																					// the
																					// vector
																					// was
																					// successfully
																					// created

																				}
	}

	// --------------------------------------------------------------------------

	// read in the contents of a file, put it into a vector, and add that vector
	// to the vector composed of vectors containing the static files
	@SuppressWarnings("unchecked")
	protected boolean addVectorFromFile(String fileName) throws IOException {
		boolean returnValue;
		// try
		// {
		// the vector we are going to fill
		@SuppressWarnings("rawtypes")
		Vector vectorFromFile = new Vector();

		// create a buffered reader
		File file = new File(fileName);
		FileInputStream fileInputStream = new FileInputStream(file);
		InputStreamReader inputStreamReader = new InputStreamReader(
				fileInputStream, "UTF-16");

		// If the bufferedReader is not big enough for a file, I should change
		// the size of it here
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader,
				20000);

		// read in the text a line at a time
		String part;
		StringBuffer word = new StringBuffer();
		while ((part = bufferedReader.readLine()) != null) {
			// add spaces at the end of the line
			part = part + "  ";

			// for each line
			for (int index = 0; index < part.length() - 1; index++) {
				// if the character is not a space, append it to a word
				if (!(Character.isWhitespace(part.charAt(index)))) {
					word.append(part.charAt(index));
				}
				// otherwise, if the word contains some characters, add it
				// to the vector
				else {
					if (word.length() != 0) {
						vectorFromFile.addElement(word.toString());
						word.setLength(0);
					}
				}
			}
		}

		// trim the vector
		vectorFromFile.trimToSize();

		// destroy the buffered reader
		bufferedReader.close();
		fileInputStream.close();

		// add the vector to the vector composed of vectors containing the
		// static files
		staticFiles.addElement(vectorFromFile);
		returnValue = true;
		// }
		// catch ( Exception exception )
		// {

		// System.out.println("ERROR");
		// JOptionPane.showMessageDialog ( arabicStemmerGUI, "Could not open '"
		// + fileName + "'.", " Error ", JOptionPane.ERROR_MESSAGE );
		// returnValue = false;
		// }
		return returnValue;
	}

	// --------------------------------------------------------------------------
}