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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class FileInOut {

	private File file = null;
	private String fileName = "";
	private String oldFileName = "";
	private String newFileName = "";

	/**
	 * Default constructor
	 */
	FileInOut() {

	}

	/**
	 * Constructor with specified file name
	 * 
	 * @param fileName
	 *            Name of file with extension to use as the file
	 */
	FileInOut(String fileName) {
		this.setFileName(fileName);
		this.makeFile();
	}

	/**
	 * Initially set the file
	 */
	public void makeFile() {
		this.file = new File(this.getFileName());
		return;
	}

	/**
	 * Gets the file as a File Object
	 * 
	 * @return Returns the File associated with this object
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Sets the file name
	 * 
	 * @param fileName
	 *            File name including extension
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
		this.makeFile();
		return;
	}

	/**
	 * Gets the File object name
	 * 
	 * @return Name of File
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * Sets the old file name for renaming purposes
	 * 
	 * @param oldFileName
	 *            Old file name
	 */
	public void setOldFileName(String oldFileName) {
		this.oldFileName = oldFileName;
		return;
	}

	/**
	 * Gets the old file name for renaming purposes
	 * 
	 * @return Old file name
	 */
	public String getOldFileName() {
		return this.oldFileName;
	}

	/**
	 * Sets the new file name that will be created
	 * 
	 * @param newFileName
	 *            New file name
	 */
	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
		return;
	}

	/**
	 * Gets the new file name that will be created
	 * 
	 * @return New file name
	 */
	public String getNewFileName() {
		return this.newFileName;
	}

	/**
	 * Renames file to newFileName, changes oldFileName to deleted file, and
	 * sets current file name to new file
	 */
	public void renameFile() {
		try {
			// Moves the current file to the new specified file name in the same
			// working directory
			Files.move(this.getFile().toPath(), this.getFile().toPath()
					.resolveSibling(this.getNewFileName()));

			// Old file name is set to the previous file
			this.setOldFileName(this.getFileName());
			// Current file name is set to the newly made file
			this.setFileName(this.getNewFileName());
			// New file name is set to the newly made file
			this.setNewFileName(this.getFileName());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads the entire file as a string
	 * 
	 * @return String of data read
	 */
	public String read() {
		BufferedReader reader = null;
		try {
			// Read the entirety of the file
			reader = new BufferedReader(new FileReader(this.file));
			String returnVal = "";
			String lineRead = "";

			// Read each line of the file, appending a new line to the result
			// and adding it to the return value
			while ((lineRead = reader.readLine()) != null) {
				returnVal += lineRead + '\n';
			}
			reader.close();
			return returnVal;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns a list of files if FileInOut is a folder
	 * 
	 * @return List of all files in the folder
	 */
	public FileInOut[] listFiles() {
		// Gets a list of all of the files in the folder, as long as the File
		// Object is of a folder
		File[] files = this.getFile().listFiles();

		// Creates new FileInOut Objects of all of the files in the folder
		FileInOut[] filesIO = new FileInOut[files.length];
		for (int x = 0; x < filesIO.length; x++) {
			filesIO[x] = new FileInOut(files[x].getName());
		}
		return filesIO;
	}

	/**
	 * Gets the name of the file without the extension
	 * 
	 * @return File name without extension
	 */
	public String getName() {
		// Get the file name up to the point of the last '.' (the start of the
		// extension specifier)
		return this.getFileName().substring(0,
				this.getFileName().lastIndexOf("."));
	}

}
