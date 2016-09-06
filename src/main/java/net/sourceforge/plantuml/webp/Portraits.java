/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.webp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.version.PSystemVersion;

public class Portraits {

	private final static List<Portrait> all = new ArrayList<Portrait>();
	private final static AtomicInteger current = new AtomicInteger();

	private static InputStream getInputStream() {
		return PSystemVersion.class.getResourceAsStream("out.png");
	}

	static {
		final InputStream is = getInputStream();
		if (is != null) {
			try {
				read(is);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static void read(InputStream is) throws IOException {
		final DataInputStream dis = new DataInputStream(is);
		final int nb = dis.readShort();
		final List<String> names = new ArrayList<String>();
		final List<Integer> ages = new ArrayList<Integer>();
		final List<String> quotes = new ArrayList<String>();
		for (int i = 0; i < nb; i++) {
			names.add(dis.readUTF());
			ages.add((int) dis.readByte());
			quotes.add(dis.readUTF());
		}
		for (int i = 0; i < nb; i++) {
			final int len = dis.readShort();
			final byte data[] = new byte[len];
			dis.readFully(data);
			all.add(new Portrait(names.get(i), ages.get(i), quotes.get(i), data));
		}
		Collections.shuffle(all);
	}

	public static Portrait getOne() {
		if (all.size() == 0) {
			return null;
		}
		final int nb = current.get() % all.size();
		return all.get(nb);
	}

	public static void nextOne() {
		current.getAndIncrement();
	}

	public static Portrait getOne(String line) {
		Portrait candidat = null;
		for (Portrait p : all) {
			final int dist = similar(p.getName(), line);
			if (dist <= 3) {
				if (candidat != null && dist < similar(candidat.getName(), line)) {
					continue;
				}
				candidat = p;
			}
		}
		return candidat;
	}

	public static int similar(String s1, String s2) {
		final int[] tab1 = countLetter(s1);
		final int[] tab2 = countLetter(s2);
		int result = 0;
		for (int i = 0; i < tab1.length; i++) {
			result += Math.abs(tab1[i] - tab2[i]);
		}
		return result;
	}

	private static String noAccent(String str) {
		final String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

	private static int[] countLetter(String s) {
		s = noAccent(s).toLowerCase(Locale.US);
		final int[] result = new int[26];
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if (c >= 'a' && c <= 'z') {
				result[c - 'a']++;
			}
		}
		return result;
	}

	static final List<Portrait> getAll() {
		return all;
	}
}
