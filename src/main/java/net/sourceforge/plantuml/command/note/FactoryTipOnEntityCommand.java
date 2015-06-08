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
package net.sourceforge.plantuml.command.note;

import java.util.List;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlBuilder.ModeUrl;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.command.regex.IRegex;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;

public final class FactoryTipOnEntityCommand implements SingleMultiFactoryCommand<AbstractEntityDiagram> {

	private final IRegex partialPattern;

	public FactoryTipOnEntityCommand(IRegex partialPattern) {
		this.partialPattern = partialPattern;
	}

	private RegexConcat getRegexConcatMultiLine(IRegex partialPattern) {
		return new RegexConcat(new RegexLeaf("^note[%s]+"), //
				new RegexLeaf("POSITION", "(right|left)"), //
				new RegexLeaf("[%s]+of[%s]+"), partialPattern, //
				new RegexLeaf("[%s]*"), //
				new RegexLeaf("COLOR", "(" + HtmlColorUtils.COLOR_REGEXP + ")?"), //
				new RegexLeaf("[%s]*\\{?"), //
				new RegexLeaf("$") //
		);
	}

	public Command<AbstractEntityDiagram> createSingleLine() {
		throw new UnsupportedOperationException();
	}

	public Command<AbstractEntityDiagram> createMultiLine() {
		return new CommandMultilines2<AbstractEntityDiagram>(getRegexConcatMultiLine(partialPattern),
				MultilinesStrategy.KEEP_STARTING_QUOTE) {

			@Override
			public String getPatternEnd() {
				return "(?i)^(end[%s]?note|\\})$";
			}

			public CommandExecutionResult executeNow(final AbstractEntityDiagram system, List<String> lines) {
				// StringUtils.trim(lines, false);
				final RegexResult line0 = getStartingPattern().matcher(StringUtils.trin(lines.get(0)));

				List<String> strings = StringUtils.removeEmptyColumns(lines.subList(1, lines.size() - 1));
				Url url = null;
				if (strings.size() > 0) {
					final UrlBuilder urlBuilder = new UrlBuilder(system.getSkinParam().getValue("topurl"),
							ModeUrl.STRICT);
					url = urlBuilder.getUrl(strings.get(0));
				}
				if (url != null) {
					strings = strings.subList(1, strings.size());
				}

				return executeInternal(line0, system, url, strings);
			}
		};
	}

	private CommandExecutionResult executeInternal(RegexResult line0, AbstractEntityDiagram diagram, Url url,
			List<? extends CharSequence> s) {

		final String pos = line0.get("POSITION", 0);

		final Code code = Code.of(line0.get("ENTITY", 0));
		final String member = line0.get("ENTITY", 1);
		if (code == null) {
			return CommandExecutionResult.error("Nothing to note to");
		}
		final IEntity cl1 = diagram.getOrCreateLeaf(code, null, null);
		final Position position = Position.valueOf(StringUtils.goUpperCase(pos)).withRankdir(
				diagram.getSkinParam().getRankdir());

		final Code codeTip = code.addSuffix("$$$" + position.name());
		IEntity tips = diagram.getLeafsget(codeTip);
		if (tips == null) {
			tips = diagram.getOrCreateLeaf(codeTip, LeafType.TIPS, null);
			final LinkType type = new LinkType(LinkDecor.NONE, LinkDecor.NONE).getInvisible();
			final Link link;
			if (position == Position.RIGHT) {
				link = new Link(cl1, (IEntity) tips, type, null, 1);
			} else {
				link = new Link((IEntity) tips, cl1, type, null, 1);
			}
			diagram.addLink(link);
		}
		tips.putTip(member, Display.create(s));

		// final IEntity note = diagram.createLeaf(UniqueSequence.getCode("GMN"), Display.create(s), LeafType.NOTE,
		// null);
		// note.setSpecificBackcolor(diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(line0.get("COLOR", 0)));
		// if (url != null) {
		// note.addUrl(url);
		// }
		//
		// final Position position = Position.valueOf(StringUtils.goUpperCase(pos)).withRankdir(
		// diagram.getSkinParam().getRankdir());
		// final Link link;
		//
		// final LinkType type = new LinkType(LinkDecor.NONE, LinkDecor.NONE).getDashed();
		// if (position == Position.RIGHT) {
		// link = new Link(cl1, note, type, null, 1);
		// link.setHorizontalSolitary(true);
		// } else if (position == Position.LEFT) {
		// link = new Link(note, cl1, type, null, 1);
		// link.setHorizontalSolitary(true);
		// } else if (position == Position.BOTTOM) {
		// link = new Link(cl1, note, type, null, 2);
		// } else if (position == Position.TOP) {
		// link = new Link(note, cl1, type, null, 2);
		// } else {
		// throw new IllegalArgumentException();
		// }
		// diagram.addLink(link);
		return CommandExecutionResult.ok();
	}

}
