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
package net.sourceforge.plantuml.ugraphic.visio;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.creole.AtomText;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.ugraphic.AbstractCommonUGraphic;
import net.sourceforge.plantuml.ugraphic.AbstractUGraphic;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UCenteredCharacter;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic2;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UImageSvg;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UText;

public class UGraphicVdx extends AbstractUGraphic<VisioGraphics> implements ClipContainer, UGraphic2 {

	private final StringBounder stringBounder;

	private UGraphicVdx(ColorMapper colorMapper, VisioGraphics visio) {
		super(colorMapper, visio);
		this.stringBounder = FileFormat.PNG.getDefaultStringBounder();
		register();

	}

	public UGraphicVdx(ColorMapper colorMapper) {
		this(colorMapper, new VisioGraphics());

	}

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		return new UGraphicVdx(this);
	}

	private UGraphicVdx(UGraphicVdx other) {
		super(other);
		this.stringBounder = other.stringBounder;
		register();
	}

	private void register() {
		registerDriver(URectangle.class, new DriverRectangleVdx());
		registerDriver(UText.class, new DriverTextVdx(stringBounder));
		registerDriver(AtomText.class, new DriverNoneVdx());
		registerDriver(ULine.class, new DriverLineVdx());
		registerDriver(UPolygon.class, new DriverPolygonVdx());
		registerDriver(UEllipse.class, new DriverNoneVdx());
		registerDriver(UImage.class, new DriverNoneVdx());
		registerDriver(UImageSvg.class, new DriverNoneVdx());
		registerDriver(UPath.class, new DriverUPathVdx());
		registerDriver(DotPath.class, new DriverDotPathVdx());
		registerDriver(UCenteredCharacter.class, new DriverNoneVdx());
	}

	public StringBounder getStringBounder() {
		return stringBounder;
	}

	public void startUrl(Url url) {
	}

	public void closeAction() {
	}

	public void writeImageTOBEMOVED(OutputStream os, String metadata, int dpi) throws IOException {
		createVsd(os);
	}

	public void createVsd(OutputStream os) throws IOException {
		getGraphicObject().createVsd(os);
	}

	public boolean isSpecialTxt() {
		return true;
	}

}
