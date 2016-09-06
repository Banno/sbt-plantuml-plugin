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
/*	This file is part of javavp8decoder.

    javavp8decoder is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    javavp8decoder is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with javavp8decoder.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.plantuml.webp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

public class VP8Decoder {
	private int[][][][] coefProbs;
	VP8Frame f;
	private int frameCount = 0;

	public VP8Decoder() {
		coefProbs = Globals.getDefaultCoefProbs();
	}

	public void decodeFrame(ImageInputStream stream, boolean debug)
			throws IOException {
		coefProbs = Globals.getDefaultCoefProbs();
		f = new VP8Frame(stream, coefProbs);
		if (f.decodeFrame(debug)) {

		}
		frameCount++;
	}

	public VP8Frame getFrame() {
		return f;
	}

	public int getHeight() {
		return f.getHeight();
	}

	public int getWidth() {
		return f.getWidth();
	}

	@SuppressWarnings("unused")
	private void writeFile(int[][] data) {
		FileOutputStream out;
		try {
			out = new FileOutputStream("outagain.raw");
			for (int y = 0; y < data[0].length; y++)
				for (int x = 0; x < data.length; x++) {
					out.write(data[x][y]);
					out.write(data[x][y]);
					out.write(data[x][y]);
				}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writePGMFile(String fileName, VP8Frame frame) {


		FileOutputStream out;
		try {
			int[][] yData = frame.getYBuffer();
			int[][] uData = frame.getUBuffer();
			int[][] vData = frame.getVBuffer();
			int outStride = (f.getWidth()+1)& ~1;
			int uvHeight = (f.getHeight() +1) / 2;
			out = new FileOutputStream(fileName);
			out.write((byte) 'P');
			out.write((byte) '5');
			out.write((byte) 0x0a);
			out.write(("" + outStride).getBytes());
			out.write((byte) ' ');

			out.write(("" + (f.getHeight() + uvHeight)).getBytes());
			
			out.write((byte) 0x0a);
			out.write(("255").getBytes());
			out.write((byte) 0xa);
			for (int y = 0; y < f.getHeight(); y++) {
				for (int x = 0; x < f.getWidth(); x++) {
					out.write(yData[x][y]);
				}
				if((f.getWidth() & 1)==1)
					out.write(0x0);
			}
			for (int y = 0; y < (f.getHeight() + 1) / 2; y++) {
				for (int x = 0; x < (f.getWidth() + 1) / 2; x++) {
					out.write(uData[x][y]);
				}
				for (int x = 0; x < (f.getWidth() + 1) / 2; x++) {
					out.write(vData[x][y]);
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeYV12File(String fileName, VP8Frame frame) {

		FileOutputStream out;
		try {
			int[][] yData = frame.getYBuffer();
			int[][] uData = frame.getUBuffer();
			int[][] vData = frame.getVBuffer();
			out = new FileOutputStream(fileName);
			out.write((byte) 'P');
			out.write((byte) '5');
			out.write((byte) 0x0a);
			out.write(("" + f.getWidth()).getBytes());
			out.write((byte) ' ');

			out.write(("" + (f.getHeight() * 3 / 2)).getBytes());
			out.write((byte) 0x0a);
			out.write(("255").getBytes());
			out.write((byte) 0xa);
			for (int y = 0; y < f.getHeight(); y++) {
				for (int x = 0; x < f.getWidth(); x++) {
					out.write(yData[x][y]);
				}

			}
			for (int y = 0; y < (f.getHeight() + 1) / 2; y++)
				for (int x = 0; x < (f.getWidth() + 1) / 2; x++) {
					out.write(uData[x][y]);
				}
			for (int y = 0; y < (f.getHeight() + 1) / 2; y++)
				for (int x = 0; x < (f.getWidth() + 1) / 2; x++) {
					out.write(vData[x][y]);
				}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
