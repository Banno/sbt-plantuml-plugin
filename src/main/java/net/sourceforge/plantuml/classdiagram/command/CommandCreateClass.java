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
package net.sourceforge.plantuml.classdiagram.command;

import java.util.Locale;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlBuilder.ModeUrl;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;

public class CommandCreateClass extends SingleLineCommand2<ClassDiagram> {

	public static final String CODE = "[^%s{}%g<>]+";
	public static final String CODE_NO_DOTDOT = "[^%s{}%g<>:]+";

	enum Mode {
		EXTENDS, IMPLEMENTS
	};

	public CommandCreateClass() {
		super(getRegexConcat());
	}

	private static RegexConcat getRegexConcat() {
		return new RegexConcat(new RegexLeaf("^"), //
				new RegexLeaf("TYPE", //
						"(interface|enum|annotation|abstract[%s]+class|abstract|class)[%s]+"), //
				new RegexOr(//
						new RegexConcat(//
								new RegexLeaf("DISPLAY1", "[%g]([^%g]+)[%g]"), //
								new RegexLeaf("[%s]+as[%s]+"), //
								new RegexLeaf("CODE1", "(" + CODE + ")")), //
						new RegexConcat(//
								new RegexLeaf("CODE2", "(" + CODE + ")"), //
								new RegexLeaf("[%s]+as[%s]+"), // //
								new RegexLeaf("DISPLAY2", "[%g]([^%g]+)[%g]")), //
						new RegexLeaf("CODE3", "(" + CODE + ")"), //
						new RegexLeaf("CODE4", "[%g]([^%g]+)[%g]")), //
				new RegexLeaf("GENERIC", "(?:[%s]*\\<(" + GenericRegexProducer.PATTERN + ")\\>)?"), //
				new RegexLeaf("[%s]*"), //
				new RegexLeaf("STEREO", "(\\<{2}.*\\>{2})?"), //
				new RegexLeaf("[%s]*"), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
				new RegexLeaf("[%s]*"), //
				new RegexLeaf("COLOR", "(" + HtmlColorUtils.COLOR_REGEXP + ")?"), //
				new RegexLeaf("[%s]*"), //
				new RegexLeaf("LINECOLOR", "(?:##(?:\\[(dotted|dashed|bold)\\])?(\\w+)?)?"), //
				new RegexLeaf("EXTENDS", "([%s]+(extends)[%s]+(" + CommandCreateClassMultilines.CODES + "))?"), //
				new RegexLeaf("IMPLEMENTS", "([%s]+(implements)[%s]+(" + CommandCreateClassMultilines.CODES + "))?"), //
				new RegexLeaf("$"));
	}

	@Override
	protected CommandExecutionResult executeArg(ClassDiagram diagram, RegexResult arg) {
		final LeafType type = LeafType.getLeafType(StringUtils.goUpperCase(arg.get("TYPE", 0)));
		final Code code = Code.of(arg.getLazzy("CODE", 0)).eventuallyRemoveStartingAndEndingDoubleQuote("\"([:");
		final String display = arg.getLazzy("DISPLAY", 0);

		final String stereotype = arg.get("STEREO", 0);
		final String generic = arg.get("GENERIC", 0);
		final ILeaf entity;
		if (diagram.leafExist(code)) {
			entity = diagram.getOrCreateLeaf(code, type, null);
			entity.muteToType(type, null);
		} else {
			entity = diagram.createLeaf(code, Display.getWithNewlines(display), type, null);
		}
		if (stereotype != null) {
			entity.setStereotype(new Stereotype(stereotype, diagram.getSkinParam().getCircledCharacterRadius(), diagram
					.getSkinParam().getFont(FontParam.CIRCLED_CHARACTER, null, false), diagram.getSkinParam()
					.getIHtmlColorSet()));
		}
		if (generic != null) {
			entity.setGeneric(generic);
		}

		final String urlString = arg.get("URL", 0);
		if (urlString != null) {
			final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), ModeUrl.STRICT);
			final Url url = urlBuilder.getUrl(urlString);
			entity.addUrl(url);
		}

		entity.setSpecificBackcolor(diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get("COLOR", 0)));
		entity.setSpecificLineColor(diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get("LINECOLOR", 1)));
		CommandCreateClassMultilines.applyStroke(entity, arg.get("LINECOLOR", 0));

		// manageExtends(diagram, arg, entity);
		CommandCreateClassMultilines.manageExtends("EXTENDS", diagram, arg, entity);
		CommandCreateClassMultilines.manageExtends("IMPLEMENTS", diagram, arg, entity);

		return CommandExecutionResult.ok();
	}

	// public static void manageExtends(ClassDiagram system, RegexResult arg, final IEntity entity) {
	// if (arg.get("EXTENDS", 1) != null) {
	// final Mode mode = arg.get("EXTENDS", 1).equalsIgnoreCase("extends") ? Mode.EXTENDS : Mode.IMPLEMENTS;
	// final Code other = Code.of(arg.get("EXTENDS", 2));
	// LeafType type2 = LeafType.CLASS;
	// if (mode == Mode.IMPLEMENTS) {
	// type2 = LeafType.INTERFACE;
	// }
	// if (mode == Mode.EXTENDS && entity.getEntityType() == LeafType.INTERFACE) {
	// type2 = LeafType.INTERFACE;
	// }
	// final IEntity cl2 = system.getOrCreateLeaf(other, type2, null);
	// LinkType typeLink = new LinkType(LinkDecor.NONE, LinkDecor.EXTENDS);
	// if (type2 == LeafType.INTERFACE && entity.getEntityType() != LeafType.INTERFACE) {
	// typeLink = typeLink.getDashed();
	// }
	// final Link link = new Link(cl2, entity, typeLink, null, 2, null, null, system.getLabeldistance(),
	// system.getLabelangle());
	// system.addLink(link);
	// }
	// }

}
