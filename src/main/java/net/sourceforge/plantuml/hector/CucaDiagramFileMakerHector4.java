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
package net.sourceforge.plantuml.hector;

import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.api.ImageDataSimple;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.CucaDiagram;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.svek.CucaDiagramFileMaker;
import net.sourceforge.plantuml.svek.CucaDiagramFileMakerSvek2;
import net.sourceforge.plantuml.svek.IEntityImage;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphic2;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class CucaDiagramFileMakerHector4 implements CucaDiagramFileMaker {

	private final CucaDiagram diagram;
	private SkeletonConfiguration configuration;

	private double singleWidth;
	private double singleHeight;
	private double nodeMargin = 40;

	public CucaDiagramFileMakerHector4(CucaDiagram diagram) {
		this.diagram = diagram;
	}

	final private Map<Pin, IEntityImage> images = new LinkedHashMap<Pin, IEntityImage>();
	final private Map<Pin, Box2D> boxes = new LinkedHashMap<Pin, Box2D>();

	final private Map<Link, PinLink> links = new LinkedHashMap<Link, PinLink>();

	final private List<Box2D> forbidden = new ArrayList<Box2D>();

	private double getX(Pin pin) {
		return singleWidth * configuration.getCol(pin);
	}

	private double getY(Pin pin) {
		return singleHeight * pin.getRow();
	}

	private double getCenterX(Pin pin) {
		return singleWidth * configuration.getCol(pin) + singleWidth / 2.0;
	}

	private double getCenterY(Pin pin) {
		return singleHeight * pin.getRow() + singleHeight / 2.0;
	}

	public ImageData createFile(OutputStream os, List<String> dotStrings, FileFormatOption fileFormatOption)
			throws IOException {
		final PinFactory pinFactory = new PinFactory();
		final SkeletonBuilder skeletonBuilder = new SkeletonBuilder();
		links.clear();
		for (Link link : diagram.getLinks()) {
			final PinLink pinLink = pinFactory.createPinLink(link);
			links.put(link, pinLink);
			skeletonBuilder.add(pinLink);
		}

		final Skeleton skeleton = skeletonBuilder.createSkeletons().get(0);
		this.configuration = SkeletonConfigurationUtils.getBest(skeleton);

		this.singleWidth = 0;
		this.singleHeight = 0;

		images.clear();
		for (Pin pin : skeleton.getPins()) {
			final ILeaf leaf = (ILeaf) pin.getUserData();
			final IEntityImage image = computeImage(leaf);
			final Dimension2D dim = TextBlockUtils.getDimension(image);
			if (dim.getWidth() > singleWidth) {
				singleWidth = dim.getWidth();
			}
			if (dim.getHeight() > singleHeight) {
				singleHeight = dim.getHeight();
			}
			images.put(pin, image);
		}
		singleHeight += nodeMargin;
		singleWidth += nodeMargin;

		MinMax minMax = MinMax.getEmpty(false);
		for (Pin pin : skeleton.getPins()) {
			minMax = minMax.addPoint(getX(pin), getY(pin));
			minMax = minMax.addPoint(getX(pin) + singleWidth, getY(pin) + singleHeight);
		}

		final double borderMargin = 10;

		final Dimension2D dimTotal = new Dimension2DDouble(2 * borderMargin + minMax.getMaxX(), 2 * borderMargin
				+ minMax.getMaxY());
		UGraphic2 ug = null; //fileFormatOption.createUGraphic(diagram.getColorMapper(), diagram.getDpiFactor(fileFormatOption),
				// dimTotal, null, false);
		ug = (UGraphic2) ug.apply(new UTranslate(borderMargin, borderMargin));

		forbidden.clear();
		for (Map.Entry<Pin, IEntityImage> ent : images.entrySet()) {
			final Pin pin = ent.getKey();
			final IEntityImage im = ent.getValue();
			final Dimension2D dimImage = im.calculateDimension(ug.getStringBounder());
			final double x = getX(pin) + (singleWidth - dimImage.getWidth()) / 2;
			final double y = getY(pin) + (singleHeight - dimImage.getHeight()) / 2;
			final Box2D box = Box2D.create(x, y, dimImage);
			boxes.put(pin, box);
			forbidden.add(box);
		}

		for (PinLink pinLink : skeleton.getPinLinks()) {
			drawPinLink(ug, pinLink);
		}

		for (Map.Entry<Pin, IEntityImage> ent : images.entrySet()) {
			final Pin pin = ent.getKey();
			final IEntityImage im = ent.getValue();
			final Dimension2D dimImage = im.calculateDimension(ug.getStringBounder());
			final double x = getX(pin) + (singleWidth - dimImage.getWidth()) / 2;
			final double y = getY(pin) + (singleHeight - dimImage.getHeight()) / 2;
			im.drawU(ug.apply(new UTranslate(x, y)));
		}

//		ug.writeImageTOBEMOVED(os, null, diagram.getDpi(fileFormatOption));
//		return new ImageDataSimple(dimTotal);
		throw new UnsupportedOperationException();
	}

	private void drawPinLink(UGraphic ug, PinLink pinLink) {
		final double x1 = getCenterX(pinLink.getPin1());
		final double y1 = getCenterY(pinLink.getPin1());
		final double x2 = getCenterX(pinLink.getPin2());
		final double y2 = getCenterY(pinLink.getPin2());

		final Rose rose = new Rose();
		final HtmlColor color = rose.getHtmlColor(diagram.getSkinParam(), ColorParam.classArrow);
		final List<Box2D> b = new ArrayList<Box2D>(forbidden);
		b.remove(boxes.get(pinLink.getPin1()));
		b.remove(boxes.get(pinLink.getPin2()));
		final SmartConnection connection = new SmartConnection(x1, y1, x2, y2, b);
		connection.draw(ug, color);
	}

	private IEntityImage computeImage(final ILeaf leaf) {
		final IEntityImage image = CucaDiagramFileMakerSvek2.createEntityImageBlock(leaf, diagram.getSkinParam(),
				false, diagram, null, null, null);
		return image;
	}
}
