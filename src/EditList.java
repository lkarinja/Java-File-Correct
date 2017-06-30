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

import java.util.ArrayList;

public class EditList {

	private ArrayList<String> list = null;

	/**
	 * Default constructor that creates a new String ArrayList
	 */
	EditList() {
		this.list = new ArrayList<String>();
	}

	/**
	 * Populates the ArrayList with data, with each element being separated with
	 * a regular expression
	 * 
	 * @param data
	 *            Entire data to populate the ArrayList with
	 * @param regex
	 *            Regular Expression to split the String into elements
	 */
	public void populate(String data, String regex) {
		// Split the data about the regular expression
		String[] dataArray = data.split(regex);

		// Add the data elements to the ArrayList
		for (int x = 0; x < dataArray.length; x++) {
			this.addToList(dataArray[x]);
		}
		return;
	}

	/**
	 * Adds a new item to the list
	 * 
	 * @param item
	 *            String value of new item
	 */
	public void addToList(String item) {
		this.list.add(item);
		return;
	}

	/**
	 * Adds new items to the list, with each element being separated by a given
	 * regular expression
	 * 
	 * @param data
	 *            Entire data to populate the ArrayList with
	 * @param regex
	 *            Regular Expression to split the String into elements
	 */
	public void addToList(String data, String regex) {
		// Split the data about the regular expression
		String[] dataArray = data.split(regex);

		// Add the data elements to the ArrayList
		for (int x = 0; x < dataArray.length; x++) {
			this.addToList(dataArray[x + this.size()]);
		}
		return;
	}

	/**
	 * Removes the last item from the ArrayList
	 */
	public void removeFromList() {
		this.removeFromList(this.list.size() - 1);
	}

	/**
	 * Removes the given item at the index from the list
	 * 
	 * @param index
	 *            Index of item to remove from the ArrayList
	 */
	public void removeFromList(int index) {
		this.list.remove(index);
		return;
	}

	/**
	 * Gets the last item from the ArrayList
	 * 
	 * @return Last item from the ArrayList
	 */
	public String getFromList() {
		return this.getFromList(this.size() - 1);
	}

	/**
	 * Gets the given item at the index from the list
	 * 
	 * @param index
	 *            Index of item to get from the ArrayList
	 * @return Item received from the list
	 */
	public String getFromList(int index) {
		return this.list.get(index);
	}

	/**
	 * Gets the size of the ArrayList Object
	 * 
	 * @return Size of the list
	 */
	public int size() {
		return this.list.size();
	}

	/**
	 * Prints all items in the current ArrayList
	 */
	public void printAllItems() {
		for (int x = 0; x < this.size(); x++) {
			System.out.println(this.getFromList(x).toString());
		}
		return;
	}
}
