import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

// SymSpell: 1 million times faster through Symmetric Delete spelling correction algorithm
//
// The Symmetric Delete spelling correction algorithm reduces the complexity of edit candidate generation and dictionary lookup 
// for a given Damerau-Levenshtein distance. It is six orders of magnitude faster and language independent.
// Opposite to other algorithms only deletes are required, no transposes + replaces + inserts.
// Transposes + replaces + inserts of the input term are transformed into deletes of the dictionary term.
// Replaces and inserts are expensive and language dependent: e.g. Chinese has 70,000 Unicode Han characters!
//
// Copyright (C) 2015 Wolf Garbe
// Version: 3.0
// Author: Wolf Garbe <wolf.garbe@faroo.com>
// Maintainer: Wolf Garbe <wolf.garbe@faroo.com>
// URL: http://blog.faroo.com/2012/06/07/improved-edit-distance-based-spelling-correction/
// Description: http://blog.faroo.com/2012/06/07/improved-edit-distance-based-spelling-correction/
//
// License:
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License, 
// version 3.0 (LGPL-3.0) as published by the Free Software Foundation.
// http://www.opensource.org/licenses/LGPL-3.0

public class SymSpell {
	private int editDistanceMax = 0;

	SymSpell() {

	}

	SymSpell(int maxDistance) {
		this.editDistanceMax = maxDistance;
	}

	private class dictionaryItem {
		public List<Integer> suggestions = new ArrayList<Integer>();
		public int count = 0;
	}

	private class suggestItem {
		public String term = "";
		public int distance = 0;
		public int count = 0;

		@Override
		public boolean equals(Object obj) {
			return term.equals(((suggestItem) obj).term);
		}

		@Override
		public int hashCode() {
			return term.hashCode();
		}
	}

	private HashMap<String, Object> dictionary = new HashMap<String, Object>();
	private List<String> wordlist = new ArrayList<String>();

	public int maxlength = 0;

	private boolean CreateDictionaryEntry(String key) {
		boolean result = false;
		dictionaryItem value = null;
		Object valueo;
		valueo = dictionary.get(key);
		if (valueo != null) {
			if (valueo instanceof Integer) {
				int tmp = (int) valueo;
				value = new dictionaryItem();
				value.suggestions.add(tmp);
				dictionary.put(key, value);
			}

			else {
				value = (dictionaryItem) valueo;
			}

			if (value.count < Integer.MAX_VALUE)
				value.count++;
		} else if (wordlist.size() < Integer.MAX_VALUE) {
			value = new dictionaryItem();
			value.count++;
			dictionary.put(key, value);

			if (key.length() > maxlength)
				maxlength = key.length();
		}
		if (value.count == 1) {
			wordlist.add(key);
			int keyint = (int) (wordlist.size() - 1);

			result = true;

			for (String delete : Edits(key, 0, new HashSet<String>())) {
				Object value2;
				value2 = dictionary.get(delete);
				if (value2 != null) {
					if (value2 instanceof Integer) {
						int tmp = (int) value2;
						dictionaryItem di = new dictionaryItem();
						di.suggestions.add(tmp);
						dictionary.put(delete, di);
						if (!di.suggestions.contains(keyint))
							AddLowestDistance(di, key, keyint, delete);
					} else if (!((dictionaryItem) value2).suggestions
							.contains(keyint))
						AddLowestDistance((dictionaryItem) value2, key, keyint,
								delete);
				} else {
					dictionary.put(delete, keyint);
				}

			}
		}
		return result;
	}

	public void CreateDictionary(String corpus) {
		File f = new File(corpus);
		if (!(f.exists() && !f.isDirectory())) {
			System.out.println("File not found: " + corpus);
			return;
		}

		System.out.println("Creating dictionary ...");
		long startTime = System.currentTimeMillis();
		long wordCount = 0;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(corpus));
			String line;
			while ((line = br.readLine()) != null) {
				if (CreateDictionaryEntry(line))
					wordCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("\rDictionary: " + wordCount + " words, "
				+ dictionary.size() + " entries, edit distance="
				+ editDistanceMax + " in " + (endTime - startTime) + "ms ");
	}

	private void AddLowestDistance(dictionaryItem item, String suggestion,
			int suggestionint, String delete) {
		if ((item.suggestions.size() > 0)
				&& (wordlist.get(item.suggestions.get(0)).length()
						- delete.length() > suggestion.length()
						- delete.length()))
			item.suggestions.clear();
	}

	private HashSet<String> Edits(String word, int editDistance,
			HashSet<String> deletes) {
		editDistance++;
		if (word.length() > 1) {
			for (int i = 0; i < word.length(); i++) {
				String delete = word.substring(0, i) + word.substring(i + 1);
				if (deletes.add(delete)) {
					if (editDistance < editDistanceMax)
						Edits(delete, editDistance, deletes);
				}
			}
		}
		return deletes;
	}

	private List<suggestItem> Lookup(String input, int editDistanceMax) {
		if (input.length() - editDistanceMax > maxlength)
			return new ArrayList<suggestItem>();

		List<String> candidates = new ArrayList<String>();
		HashSet<String> hashset1 = new HashSet<String>();

		List<suggestItem> suggestions = new ArrayList<suggestItem>();
		HashSet<String> hashset2 = new HashSet<String>();

		Object valueo;

		candidates.add(input);

		while (candidates.size() > 0) {
			String candidate = candidates.remove(0);

			nosort: {

				if ((suggestions.size() > 0)
						&& (input.length() - candidate.length() > suggestions
								.get(0).distance))
					break nosort;

				valueo = dictionary.get(candidate);
				if (valueo != null) {
					dictionaryItem value = new dictionaryItem();
					if (valueo instanceof Integer)
						value.suggestions.add((int) valueo);
					else
						value = (dictionaryItem) valueo;

					if ((value.count > 0) && hashset2.add(candidate)) {
						suggestItem si = new suggestItem();
						si.term = candidate;
						si.count = value.count;
						si.distance = input.length() - candidate.length();
						suggestions.add(si);
						if ((input.length() - candidate.length() == 0))
							break nosort;
					}

					Object value2;
					for (int suggestionint : value.suggestions) {
						String suggestion = wordlist.get(suggestionint);
						if (hashset2.add(suggestion)) {
							int distance = 0;
							if (suggestion != input) {
								if (suggestion.length() == candidate.length())
									distance = input.length()
											- candidate.length();
								else if (input.length() == candidate.length())
									distance = suggestion.length()
											- candidate.length();
								else {
									int ii = 0;
									int jj = 0;
									while ((ii < suggestion.length())
											&& (ii < input.length())
											&& (suggestion.charAt(ii) == input
													.charAt(ii)))
										ii++;
									while ((jj < suggestion.length() - ii)
											&& (jj < input.length() - ii)
											&& (suggestion.charAt(suggestion
													.length() - jj - 1) == input
													.charAt(input.length() - jj
															- 1)))
										jj++;
									if ((ii > 0) || (jj > 0)) {
										distance = DamerauLevenshteinDistance(
												suggestion.substring(ii,
														suggestion.length()
																- jj),
												input.substring(ii,
														input.length() - jj));
									} else
										distance = DamerauLevenshteinDistance(
												suggestion, input);
								}
							}

							if ((suggestions.size() > 0)
									&& (suggestions.get(0).distance > distance))
								suggestions.clear();
							if ((suggestions.size() > 0)
									&& (distance > suggestions.get(0).distance))
								continue;

							if (distance <= editDistanceMax) {
								value2 = dictionary.get(suggestion);
								if (value2 != null) {
									suggestItem si = new suggestItem();
									si.term = suggestion;
									si.count = ((dictionaryItem) value2).count;
									si.distance = distance;
									suggestions.add(si);
								}
							}
						}
					}
				}

				if (input.length() - candidate.length() < editDistanceMax) {
					if ((suggestions.size() > 0)
							&& (input.length() - candidate.length() >= suggestions
									.get(0).distance))
						continue;

					for (int i = 0; i < candidate.length(); i++) {
						String delete = candidate.substring(0, i)
								+ candidate.substring(i + 1);
						if (hashset1.add(delete))
							candidates.add(delete);
					}
				}
			}
		}

		Collections.sort(suggestions, new Comparator<suggestItem>() {
			public int compare(suggestItem f1, suggestItem f2) {
				return -(f1.count - f2.count);
			}
		});

		if (suggestions.size() > 1)
			return suggestions.subList(0, 1);
		else
			return suggestions;
	}

	public String Correct(String input) {
		List<suggestItem> suggestions = null;
		suggestions = Lookup(input, editDistanceMax);

		for (suggestItem suggestion : suggestions) {
			System.out.println(suggestion.term + " " + suggestion.distance
					+ " " + suggestion.count);
			return suggestion.term;
		}
		return input;

	}

	public void createDictionary(String fileName) {
		CreateDictionary(fileName);
		return;
	}

	public static int DamerauLevenshteinDistance(String a, String b) {
		final int inf = a.length() + b.length() + 1;
		int[][] H = new int[a.length() + 2][b.length() + 2];
		for (int i = 0; i <= a.length(); i++) {
			H[i + 1][1] = i;
			H[i + 1][0] = inf;
		}
		for (int j = 0; j <= b.length(); j++) {
			H[1][j + 1] = j;
			H[0][j + 1] = inf;
		}
		HashMap<Character, Integer> DA = new HashMap<Character, Integer>();
		for (int d = 0; d < a.length(); d++)
			if (!DA.containsKey(a.charAt(d)))
				DA.put(a.charAt(d), 0);

		for (int d = 0; d < b.length(); d++)
			if (!DA.containsKey(b.charAt(d)))
				DA.put(b.charAt(d), 0);

		for (int i = 1; i <= a.length(); i++) {
			int DB = 0;
			for (int j = 1; j <= b.length(); j++) {
				final int i1 = DA.get(b.charAt(j - 1));
				final int j1 = DB;
				int d = 1;
				if (a.charAt(i - 1) == b.charAt(j - 1)) {
					d = 0;
					DB = j;
				}
				H[i + 1][j + 1] = min(H[i][j] + d, H[i + 1][j] + 1,
						H[i][j + 1] + 1, H[i1][j1] + ((i - i1 - 1)) + 1
								+ ((j - j1 - 1)));
			}
			DA.put(a.charAt(i - 1), i);
		}
		return H[a.length() + 1][b.length() + 1];
	}

	public static int min(int a, int b, int c, int d) {
		return Math.min(a, Math.min(b, Math.min(c, d)));
	}
}