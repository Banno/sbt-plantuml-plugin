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
package net.sourceforge.plantuml.braille;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.ugraphic.AbstractCommonUGraphic;
import net.sourceforge.plantuml.ugraphic.AbstractUGraphic;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.UCenteredCharacter;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic2;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UText;

// https://www.branah.com/braille-translator
public class UGraphicBraille extends AbstractUGraphic<BrailleGrid> implements ClipContainer, UGraphic2 {

	public static final int QUANTA = 4;
	private final BrailleGrid grid;

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		return new UGraphicBraille(this);
	}

	public UGraphicBraille(ColorMapper colorMapper, FileFormat fileFormat) {
		this(colorMapper, new BrailleGrid(QUANTA));
	}

	private UGraphicBraille(UGraphicBraille other) {
		super(other);
		this.grid = other.grid;
		register();
	}

	// public UGraphicBraille(ColorMapper colorMapper, String backcolor, boolean textAsPath, double scale, String
	// linkTarget) {
	// this(colorMapper, new SvgGraphics(backcolor, scale), textAsPath, linkTarget);
	// }
	//
	// public UGraphicBraille(ColorMapper colorMapper, boolean textAsPath, double scale, String linkTarget) {
	// this(colorMapper, new SvgGraphics(scale), textAsPath, linkTarget);
	// }
	//
	// public UGraphicBraille(ColorMapper mapper, HtmlColorGradient gr, boolean textAsPath, double scale, String
	// linkTarget) {
	// this(mapper, new SvgGraphics(scale), textAsPath, linkTarget);
	//
	// final SvgGraphics svg = getGraphicObject();
	// svg.paintBackcolorGradient(mapper, gr);
	// }

	private UGraphicBraille(ColorMapper colorMapper, BrailleGrid grid) {
		super(colorMapper, grid);
		this.grid = grid;
		register();
	}

	private void register() {
		registerDriver(URectangle.class, new DriverNoneBraille());
		registerDriver(URectangle.class, new DriverRectangleBraille(this));
		registerDriver(UText.class, new DriverTextBraille());
		registerDriver(ULine.class, new DriverLineBraille(this));
		registerDriver(UPolygon.class, new DriverPolygonBraille(this));
		registerDriver(UEllipse.class, new DriverNoneBraille());
		registerDriver(UImage.class, new DriverNoneBraille());
		registerDriver(UPath.class, new DriverNoneBraille());
		registerDriver(DotPath.class, new DriverDotPathBraille());
		registerDriver(UCenteredCharacter.class, new DriverCenteredCharacterBraille());
	}

	public StringBounder getStringBounder() {
		return FileFormat.BRAILLE_PNG.getDefaultStringBounder();
	}

	public void startUrl(Url url) {
	}

	public void closeAction() {
	}

	public void writeImageTOBEMOVED(OutputStream os, String metadata, int dpi) throws IOException {
		final ImageBuilder imageBuilder = new ImageBuilder(new ColorMapperIdentity(), 1.0, HtmlColorUtils.WHITE,
				metadata, null, 0, 0, null, false);
		imageBuilder.setUDrawable(new BrailleDrawer(getGraphicObject()));

		imageBuilder.writeImageTOBEMOVED(new FileFormatOption(FileFormat.PNG), os);

	}
}
