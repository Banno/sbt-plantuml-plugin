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
package net.sourceforge.plantuml.svek;

import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;
import net.sourceforge.plantuml.flashcode.FlashCodeFactory;
import net.sourceforge.plantuml.flashcode.FlashCodeUtils;
import net.sourceforge.plantuml.fun.IconLoader;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.GraphicPosition;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.QuoteUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UAntiAliasing;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.version.Version;

public class GraphvizCrash extends AbstractTextBlock implements IEntityImage {

	private static final UFont font = new UFont("SansSerif", Font.PLAIN, 12);

	private final GraphicStrings graphicStrings;
	private final BufferedImage flashCode;

	public GraphvizCrash(String text) {
		final FlashCodeUtils utils = FlashCodeFactory.getFlashCodeUtils();
		flashCode = utils.exportFlashcode(text);
		this.graphicStrings = new GraphicStrings(init(), font, HtmlColorUtils.BLACK, HtmlColorUtils.WHITE,
				UAntiAliasing.ANTI_ALIASING_ON, IconLoader.getRandom(), GraphicPosition.BACKGROUND_CORNER_TOP_RIGHT);
	}

	private List<String> init() {
		final List<String> strings = new ArrayList<String>();
		strings.add("An error has occured!");
		final String quote = QuoteUtils.getSomeQuote();
		strings.add("<i>" + quote);
		strings.add(" ");
		strings.add("For some reason, dot/Graphviz has crashed.");
		strings.add("This has been generated with PlantUML (" + Version.versionString() + ").");
		strings.add(" ");
		addProperties(strings);
		strings.add(" ");
		try {
			final String dotVersion = GraphvizUtils.dotVersion();
			strings.add("Default dot version: " + dotVersion);
		} catch (Throwable e) {
			strings.add("Cannot determine dot version: " + e.toString());
		}
		strings.add(" ");
		strings.add("You should send this diagram and this image to <b>plantuml@gmail.com</b> to solve this issue.");
		strings.add("You can try to turn arround this issue by simplifing your diagram.");
		if (flashCode != null) {
			addDecodeHint(strings);
		}

		return strings;
	}

	public static void addDecodeHint(final List<String> strings) {
		strings.add(" ");
		strings.add(" Diagram source: (Use http://zxing.org/w/decode.jspx to decode the flashcode)");
	}

	public static void addProperties(final List<String> strings) {
		addTextProperty(strings, "os.version");
		addTextProperty(strings, "os.name");
		addTextProperty(strings, "java.vm.vendor");
		addTextProperty(strings, "java.vm.version");
		addTextProperty(strings, "java.version");
		addTextProperty(strings, "user.language");
	}

	private static void addTextProperty(final List<String> strings, String prop) {
		strings.add(prop + ": " + System.getProperty(prop));
	}

	public boolean isHidden() {
		return false;
	}

	public HtmlColor getBackcolor() {
		return HtmlColorUtils.WHITE;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		Dimension2D result = graphicStrings.calculateDimension(stringBounder);
		if (flashCode != null) {
			result = Dimension2DDouble.mergeTB(result,
					new Dimension2DDouble(flashCode.getWidth(), flashCode.getHeight()));
		}
		return result;
	}

	public void drawU(UGraphic ug) {
		graphicStrings.drawU(ug);
		if (flashCode != null) {
			final double h = graphicStrings.calculateDimension(ug.getStringBounder()).getHeight();
			ug = ug.apply(new UTranslate(0, h));
			ug.draw(new UImage(flashCode));
		}
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public int getShield() {
		return 0;
	}

}
