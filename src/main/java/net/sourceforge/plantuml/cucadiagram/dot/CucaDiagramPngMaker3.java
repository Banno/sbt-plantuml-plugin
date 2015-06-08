/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2014, Arnaud Roques
 *
 * Project Info:  http://plantuml.sourceforge.net
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
package net.sourceforge.plantuml.cucadiagram.dot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.EmptyImageBuilder;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.cucadiagram.CucaDiagram;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.graph.ANode;
import net.sourceforge.plantuml.graph.ANodeImpl;
import net.sourceforge.plantuml.graph.Board;
import net.sourceforge.plantuml.graph.BoardExplorer;
import net.sourceforge.plantuml.graph.Graph5;
import net.sourceforge.plantuml.graph.Heap;
import net.sourceforge.plantuml.graph.Zoda2;

public final class CucaDiagramPngMaker3 {

	private final CucaDiagram diagram;

	public CucaDiagramPngMaker3(CucaDiagram diagram) {
		this.diagram = diagram;
	}

	public void createPng(OutputStream os) throws IOException {
		final Zoda2 zoda2 = new Zoda2();

		for (Link link : diagram.getLinks()) {
			final String s = link.getEntity1().getCode() + "->" + link.getEntity2().getCode();
			// Log.error("CucaDiagramPngMaker3:: " + s);
			final int diffHeight = link.getLength() - 1;
			// Log.error("CucaDiagramPngMaker3:: " + s + " " + diffHeight);
			zoda2.addLink(s, diffHeight, link);
		}
		for (IEntity ent : diagram.getLeafsvalues()) {
			ANode n = zoda2.getNode(ent.getCode().getFullName());
			if (n == null) {
				n = zoda2.createAloneNode(ent.getCode().getFullName());
			}
			((ANodeImpl) n).setUserData(ent);
		}

		final List<Graph5> graphs = getGraphs3(zoda2.getHeaps());

		final Dimension2D totalDim = getTotalDimension(graphs);
		final EmptyImageBuilder im = new EmptyImageBuilder(totalDim.getWidth(), totalDim.getHeight(),
				Color.WHITE);

		double x = 0;

		final Graphics2D g2d = im.getGraphics2D();

		for (Graph5 g : graphs) {
			g2d.setTransform(new AffineTransform());
			g2d.translate(x, 0);
			g.draw(g2d);
			x += g.getDimension().getWidth();
		}

		ImageIO.write(im.getBufferedImage(), "png", os);
	}

	private Dimension2D getTotalDimension(List<Graph5> graphs) {
		double width = 0;
		double height = 0;
		for (Graph5 g : graphs) {
			width += g.getDimension().getWidth();
			height = Math.max(height, g.getDimension().getHeight());
		}
		return new Dimension2DDouble(width, height);

	}

	private List<Graph5> getGraphs3(Collection<Heap> heaps) {
		final List<Graph5> result = new ArrayList<Graph5>();
		for (Heap h : heaps) {
			h.computeRows();
			Board board = new Board(h.getNodes(), h.getLinks());

			final BoardExplorer boardExplorer = new BoardExplorer(board);
			final long start = System.currentTimeMillis();
			for (int i = 0; i < 400; i++) {
				final boolean finished = boardExplorer.onePass();
				if (finished) {
					break;
				}
				if (i % 100 == 0) {
					Log.info("" + i + " boardExplorer.getBestCost()=" + boardExplorer.getBestCost() + " "
							+ boardExplorer.collectionSize());
				}
			}
			Log.info("################# DURATION = " + (System.currentTimeMillis() - start));
			board = boardExplorer.getBestBoard();

			result.add(new Graph5(board));
		}
		return result;
	}

//	public List<File> createPng(File pngFile) throws IOException {
//		OutputStream os = null;
//		try {
//			os = new BufferedOutputStream(new FileOutputStream(pngFile));
//			createPng(os);
//		} finally {
//			if (os != null) {
//				os.close();
//			}
//		}
//
//		return new PngSplitter(pngFile, diagram.getHorizontalPages(), diagram.getVerticalPages(), diagram.getMetadata(), 96)
//				.getFiles();
//	}
}
