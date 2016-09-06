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
package net.sourceforge.plantuml.version;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.DiagramDescriptionImpl;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;
import net.sourceforge.plantuml.graphic.GraphicPosition;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.UAntiAliasing;
import net.sourceforge.plantuml.ugraphic.UFont;

public class PSystemVersion extends AbstractPSystem {

	private final List<String> strings = new ArrayList<String>();
	private BufferedImage image;

	PSystemVersion(boolean withImage, List<String> args) {
		strings.addAll(args);
		if (withImage) {
			image = getPlantumlImage();
		}
	}

	public static BufferedImage getPlantumlImage() {
		try {
			final InputStream is = PSystemVersion.class.getResourceAsStream("logo.png");
			final BufferedImage image = ImageIO.read(is);
			is.close();
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static BufferedImage getCharlieImage() {
		try {
			final InputStream is = PSystemVersion.class.getResourceAsStream("charlie.png");
			final BufferedImage image = ImageIO.read(is);
			is.close();
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static BufferedImage getPlantumlSmallIcon() {
		try {
			final InputStream is = PSystemVersion.class.getResourceAsStream("favicon.png");
			final BufferedImage image = ImageIO.read(is);
			is.close();
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static BufferedImage transparentIcon;

	public static BufferedImage getPlantumlSmallIcon2() {
		if (transparentIcon != null) {
			return transparentIcon;
		}
		final BufferedImage ico = getPlantumlSmallIcon();
		if (ico == null) {
			return null;
		}
		transparentIcon = new BufferedImage(ico.getWidth(), ico.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		for (int i = 0; i < ico.getWidth(); i++) {
			for (int j = 0; j < ico.getHeight(); j++) {
				final int col = ico.getRGB(i, j);
				if (col != ico.getRGB(0, 0)) {
					transparentIcon.setRGB(i, j, col);
				}
			}
		}
		return transparentIcon;
	}

	public ImageData exportDiagram(OutputStream os, int num, FileFormatOption fileFormat) throws IOException {
		final GraphicStrings result = getGraphicStrings();
		final ImageBuilder imageBuilder = new ImageBuilder(new ColorMapperIdentity(), 1.0, result.getBackcolor(),
				getMetadata(), null, 0, 0, null, false);
		imageBuilder.setUDrawable(result);
		return imageBuilder.writeImageTOBEMOVED(fileFormat, os);
	}

	public static PSystemVersion createShowVersion() {
		final List<String> strings = new ArrayList<String>();
		strings.add("<b>PlantUML version " + Version.versionString() + "</b> (" + Version.compileTimeString() + ")");
		strings.add("(" + License.getCurrent() + " source distribution)");
		strings.add("Loaded from " + Version.getJarPath());
		strings.add(" ");

		strings.addAll(GraphvizUtils.getTestDotStrings(true));
		strings.add(" ");
		final Properties p = System.getProperties();
		strings.add(p.getProperty("java.runtime.name"));
		strings.add(p.getProperty("java.vm.name"));
		strings.add(p.getProperty("java.runtime.version"));
		strings.add(p.getProperty("os.name"));
		strings.add("Processors: " + Runtime.getRuntime().availableProcessors());
		return new PSystemVersion(true, strings);
	}

	public static PSystemVersion createShowAuthors() {
		// Duplicate in OptionPrint
		final List<String> strings = new ArrayList<String>();
		strings.add("<b>PlantUML version " + Version.versionString() + "</b> (" + Version.compileTimeString() + ")");
		strings.add("(" + License.getCurrent() + " source distribution)");
		strings.add(" ");
		strings.add("<u>Original idea</u>: Arnaud Roques");
		strings.add("<u>Word Macro</u>: Alain Bertucat & Matthieu Sabatier");
		strings.add("<u>Word Add-in</u>: Adriaan van den Brand");
		strings.add("<u>J2V8 & viz.js integration</u>: Andreas Studer");
		strings.add("<u>Eclipse Plugin</u>: Claude Durif & Anne Pecoil");
		strings.add("<u>Servlet & XWiki</u>: Maxime Sinclair");
		strings.add("<u>Site design</u>: Raphael Cotisson");
		strings.add("<u>Logo</u>: Benjamin Croizet");

		strings.add(" ");
		strings.add("http://plantuml.com");
		strings.add(" ");
		return new PSystemVersion(true, strings);
	}

	public static PSystemVersion createCheckVersions(String host, String port) {
		final List<String> strings = new ArrayList<String>();
		strings.add("<b>PlantUML version " + Version.versionString() + "</b> (" + Version.compileTimeString() + ")");

		final int lastversion = extractDownloadableVersion(host, port);

		int lim = 7;
		if (lastversion == -1) {
			strings.add("<b><color:red>Error");
			strings.add("<color:red>Cannot connect to http://plantuml.com/");
			strings.add("Maybe you should set your proxy ?");
			strings.add("@startuml");
			strings.add("checkversion(proxy=myproxy.com,port=8080)");
			strings.add("@enduml");
			lim = 9;
		} else if (lastversion == 0) {
			strings.add("<b><color:red>Error</b>");
			strings.add("Cannot retrieve last version from http://plantuml.com/");
		} else {
			strings.add("<b>Last available version for download</b> : " + lastversion);
			strings.add(" ");
			if (Version.version() >= lastversion) {
				strings.add("<b><color:green>Your version is up to date.");
			} else {
				strings.add("<b><color:red>A newer version is available for download.");
			}
		}

		while (strings.size() < lim) {
			strings.add(" ");
		}

		return new PSystemVersion(true, strings);
	}

	public static int extractDownloadableVersion(String host, String port) {
		if (host != null && port != null) {
			System.setProperty("http.proxyHost", host);
			System.setProperty("http.proxyPort", port);
		}

		try {
			final URL url = new URL("http://plantuml.com/download.html");
			final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setUseCaches(false);
			urlConnection.connect();
			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				final BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				final int lastversion = extractVersion(in);
				in.close();
				urlConnection.disconnect();
				return lastversion;
			}
		} catch (IOException e) {
			Log.error(e.toString());
		}
		return -1;
	}

	private static int extractVersion(BufferedReader in) throws IOException {
		String s;
		final Pattern p = Pattern.compile(".*\\.(\\d{4,5})\\..*");
		while ((s = in.readLine()) != null) {
			final Matcher m = p.matcher(s);
			if (m.matches()) {
				final String v = m.group(1);
				return Integer.parseInt(v);
			}
		}
		return 0;
	}

	public static PSystemVersion createTestDot() throws IOException {
		final List<String> strings = new ArrayList<String>();
		strings.addAll(GraphvizUtils.getTestDotStrings(true));
		return new PSystemVersion(false, strings);
	}

	private GraphicStrings getGraphicStrings() throws IOException {
		final UFont font = new UFont("SansSerif", Font.PLAIN, 12);
		return new GraphicStrings(strings, font, HtmlColorUtils.BLACK, HtmlColorUtils.WHITE,
				UAntiAliasing.ANTI_ALIASING_ON, image, GraphicPosition.BACKGROUND_CORNER_BOTTOM_RIGHT);
	}

	public DiagramDescription getDescription() {
		return new DiagramDescriptionImpl("(Version)", getClass());
	}

	public List<String> getLines() {
		return Collections.unmodifiableList(strings);
	}

}
