/*
Copyright © 2015-2017 Leejae Karinja

This file is part of Java File Correct.

Java File Correct is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Java File Correct is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Java File Correct.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.PrintWriter;

public class FileNameCorrect {

	/**
	 * Corrects a given folder of files with a dictionary file
	 * 
	 * @param folderName
	 *            Folder to correct all file names
	 * @param fileName
	 *            File of the dictionary file
	 * @param iterations
	 *            How many iterations should words be checked for
	 */
	public static void correctFileNames(String folderName, String fileName,
			int iterations) {
		try {
			// Create a new FileInOut object of the folder to work in
			FileInOut folder = new FileInOut(folderName);
			// Get a list of all of the files in the working directory
			FileInOut[] filesInFolder = folder.listFiles();
			// Get the file to use as the dictionary
			// FileInOut file = new FileInOut(fileName);
			// String dictionaryData = file.read();

			// Logging file
			PrintWriter writer = new PrintWriter("log.txt");
			writer.write("");

			// SpellingCorrector sc = new SpellingCorrector();
			SymSpell sc = new SymSpell(iterations);

			// Create new Lists of the correct names from the dictionary and the
			// given names of the files
			// EditList correctNames = new EditList();
			EditList givenNames = new EditList();
			sc.CreateDictionary(fileName);

			// Set the List of the correct names to the data from the dictionary
			// file
			// correctNames.populate(dictionaryData, "\n");
			// for (int x = 0; x < correctNames.size(); x++) {
			// sc.addToDictionary(correctNames.getFromList(x));
			// }

			// Sets the file names into the List of all file names
			for (FileInOut currentFile : filesInFolder) {
				givenNames.addToList(currentFile.getName());
			}

			// Run the spelling correction algorithm
			for (int x = 0; x < givenNames.size(); x++) {
				/*
				 * if (!dictionaryData.contains(givenNames.getFromList(x))) {
				 * String correct = sc.correct(givenNames.getFromList(x),
				 * iterations); if (!correct.equals(givenNames.getFromList(x)))
				 * { writer.append("[" + System.currentTimeMillis() +
				 * "]: Corrected \"" + givenNames.getFromList(x) + "\" to \"" +
				 * correct + "\"\n"); System.out.println("[" +
				 * System.currentTimeMillis() + "]: Corrected \"" +
				 * givenNames.getFromList(x) + "\" to \"" + correct + "\""); }
				 * else { writer.append("[" + System.currentTimeMillis() +
				 * "]: Could not correct \"" + givenNames.getFromList(x) +
				 * "\"\n"); System.out.println("[" + System.currentTimeMillis()
				 * + "]: Could not correct \"" + givenNames.getFromList(x) +
				 * "\""); } }
				 */
				sc.Correct(givenNames.getFromList(x));
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
}
