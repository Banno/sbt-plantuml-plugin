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
package net.sourceforge.plantuml.code;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionGZip implements Compression {

	class MyGZIPOutputStream extends GZIPOutputStream {

		public MyGZIPOutputStream(OutputStream baos) throws IOException {
			super(baos);
			def.setLevel(9);
		}

	}

	public byte[] compress(byte[] in) {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			final GZIPOutputStream gz = new MyGZIPOutputStream(baos);
			gz.write(in);
			gz.close();
			baos.close();
			return baos.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException(e.toString());
		}
	}

	public byte[] decompress(byte[] in) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();

		final ByteArrayInputStream bais = new ByteArrayInputStream(in);
		final GZIPInputStream gz = new GZIPInputStream(bais);
		int read;
		while ((read = gz.read()) != -1) {
			baos.write(read);
		}
		gz.close();
		bais.close();
		baos.close();
		return baos.toByteArray();
	}

}
